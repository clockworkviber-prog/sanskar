package com.sanskar.app

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanskar.app.data.AuthResult
import com.sanskar.app.data.Booking
import com.sanskar.app.data.BookingStatus
import com.sanskar.app.data.PortfolioItem
import com.sanskar.app.data.Priest
import com.sanskar.app.data.PujaMode
import com.sanskar.app.data.SanskarRepository
import com.sanskar.app.data.UserProfile
import com.sanskar.app.data.db.BookingEntity
import com.sanskar.app.data.db.PortfolioEntity
import com.sanskar.app.data.db.PriestEntity
import com.sanskar.app.data.db.SanskarDatabase
import com.sanskar.app.data.db.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

private fun PriestEntity.toModel() = Priest(
    id = id, name = name, title = title, templeAffiliation = templeAffiliation,
    specializations = specializations.split(",").map { it.trim() }.filter { it.isNotBlank() },
    languages = languages.split(",").map { it.trim() }.filter { it.isNotBlank() },
    experienceYears = experienceYears, rating = rating, pujasPerformed = pujasPerformed, isOnline = isOnline
)

private fun BookingEntity.toModel() = Booking(
    id = id, pujaName = pujaName, priestName = priestName,
    mode = PujaMode.valueOf(mode), date = date, timeSlot = timeSlot,
    priceUsd = priceUsd, status = BookingStatus.valueOf(status), sankalpName = sankalpName
)

private fun UserEntity.toModel() = UserProfile(
    fullName = fullName, email = email, phone = phone, city = city,
    country = country, gotra = gotra, timezone = timezone
)

private fun PortfolioEntity.toModel() = PortfolioItem(
    id = id, priestId = priestId, title = title, description = description, imageUri = imageUri
)

/**
 * App-level state holder backed by a local Room database (see data/db) so
 * accounts, bookings, transactions and priest portfolios persist across
 * app restarts. Passwords are hashed (PasswordHasher) — never stored raw.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SanskarRepository(SanskarDatabase.getInstance(application))

    private var currentUserEntity: UserEntity? = null
    private var currentPriestEntity: PriestEntity? = null
    private var lastBookingId: String? = null

    var currentUser by mutableStateOf<UserProfile?>(null)
        private set

    var currentPriest by mutableStateOf<Priest?>(null)
        private set

    var isReady by mutableStateOf(false)
        private set

    val priests = mutableStateListOf<Priest>()
    val bookings = mutableStateListOf<Booking>()
    val portfolio = mutableStateListOf<PortfolioItem>()

    // Every DB-touching function joins this first, so a login/signup that
    // races the initial seed always sees the seeded demo accounts/catalog.
    private val seedJob: Job = viewModelScope.launch(Dispatchers.IO) {
        repository.seedIfEmpty()
        val loaded = repository.getAllPriests().map { it.toModel() }
        withContext(Dispatchers.Main) {
            priests.clear(); priests.addAll(loaded); isReady = true
        }
    }

    // ---------- Devotee auth ----------

    fun login(email: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            when (val result = repository.loginUser(email, password)) {
                is AuthResult.Error -> withContext(Dispatchers.Main) { onResult(result.message) }
                is AuthResult.Success -> {
                    val userBookings = repository.getBookingsForUser(result.value.email).map { it.toModel() }
                    withContext(Dispatchers.Main) {
                        currentUserEntity = result.value
                        currentUser = result.value.toModel()
                        currentPriestEntity = null
                        currentPriest = null
                        bookings.clear(); bookings.addAll(userBookings)
                        onResult(null)
                    }
                }
            }
        }
    }

    fun signUp(fullName: String, email: String, phone: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            when (val result = repository.registerUser(fullName, email, phone, password)) {
                is AuthResult.Error -> withContext(Dispatchers.Main) { onResult(result.message) }
                is AuthResult.Success -> withContext(Dispatchers.Main) {
                    currentUserEntity = result.value
                    currentUser = result.value.toModel()
                    currentPriestEntity = null
                    currentPriest = null
                    bookings.clear()
                    onResult(null)
                }
            }
        }
    }

    fun logout() {
        currentUserEntity = null
        currentUser = null
        currentPriestEntity = null
        currentPriest = null
        bookings.clear()
        portfolio.clear()
    }

    fun updateProfile(updated: UserProfile) {
        val entity = currentUserEntity ?: return
        val newEntity = entity.copy(
            fullName = updated.fullName, phone = updated.phone, city = updated.city,
            country = updated.country, gotra = updated.gotra, timezone = updated.timezone
        )
        currentUserEntity = newEntity
        currentUser = newEntity.toModel()
        viewModelScope.launch(Dispatchers.IO) { seedJob.join(); repository.updateUser(newEntity) }
    }

    fun bookPuja(
        pujaName: String,
        priestName: String,
        mode: PujaMode,
        date: LocalDate,
        timeSlot: String,
        priceUsd: Int,
        sankalpName: String
    ) {
        val email = currentUserEntity?.email ?: return
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            val entity = repository.createBooking(email, pujaName, priestName, mode, date, timeSlot, priceUsd, sankalpName)
            withContext(Dispatchers.Main) {
                bookings.add(0, entity.toModel())
                lastBookingId = entity.id
            }
        }
    }

    /** Called when the devotee taps Pay Later in checkout — records a pending transaction. */
    fun recordPayLater() {
        val id = lastBookingId ?: return
        viewModelScope.launch(Dispatchers.IO) { seedJob.join(); repository.markPayLater(id) }
    }

    /** Called when the dummy checkout screen reaches its "Payment Completed" stage. */
    fun recordPaymentSuccess() {
        val id = lastBookingId ?: return
        viewModelScope.launch(Dispatchers.IO) { seedJob.join(); repository.markBookingPaid(id) }
    }

    // ---------- Priest auth ----------

    fun priestLogin(email: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            when (val result = repository.loginPriest(email, password)) {
                is AuthResult.Error -> withContext(Dispatchers.Main) { onResult(result.message) }
                is AuthResult.Success -> {
                    val myPortfolio = repository.getPortfolioForPriest(result.value.id).map { it.toModel() }
                    withContext(Dispatchers.Main) {
                        currentPriestEntity = result.value
                        currentPriest = result.value.toModel()
                        currentUserEntity = null
                        currentUser = null
                        portfolio.clear(); portfolio.addAll(myPortfolio)
                        onResult(null)
                    }
                }
            }
        }
    }

    fun priestSignUp(
        name: String,
        title: String,
        temple: String,
        specializations: String,
        languages: String,
        experienceYears: String,
        email: String,
        password: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            when (val result = repository.registerPriest(
                name, title, temple, specializations, languages, experienceYears, email, password
            )) {
                is AuthResult.Error -> withContext(Dispatchers.Main) { onResult(result.message) }
                is AuthResult.Success -> withContext(Dispatchers.Main) {
                    currentPriestEntity = result.value
                    currentPriest = result.value.toModel()
                    currentUserEntity = null
                    currentUser = null
                    priests.add(result.value.toModel())
                    portfolio.clear()
                    onResult(null)
                }
            }
        }
    }

    /** Bookings assigned to the signed-in priest, filtered client-side from the priest's name. */
    fun priestBookings(status: BookingStatus, onResult: (List<Booking>) -> Unit) {
        val priest = currentPriestEntity ?: return onResult(emptyList())
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            val result = repository.getBookingsForPriest(priest.name, status.name).map { it.toModel() }
            withContext(Dispatchers.Main) { onResult(result) }
        }
    }

    fun addPortfolioItem(title: String, description: String, imageUri: String?, onResult: (String?) -> Unit = {}) {
        val priest = currentPriestEntity
        if (priest == null) { onResult("Not signed in as a priest."); return }
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            when (val result = repository.addPortfolioItem(priest.id, title, description, imageUri)) {
                is AuthResult.Error -> withContext(Dispatchers.Main) { onResult(result.message) }
                is AuthResult.Success -> withContext(Dispatchers.Main) {
                    portfolio.add(0, result.value.toModel())
                    onResult(null)
                }
            }
        }
    }

    /** Portfolio for any priest (used on the devotee-facing priest profile screen). */
    fun portfolioFor(priestId: String, onResult: (List<PortfolioItem>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            seedJob.join()
            val result = repository.getPortfolioForPriest(priestId).map { it.toModel() }
            withContext(Dispatchers.Main) { onResult(result) }
        }
    }
}
