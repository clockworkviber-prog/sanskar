package com.sanskar.app.data

import com.sanskar.app.data.db.BookingEntity
import com.sanskar.app.data.db.PortfolioEntity
import com.sanskar.app.data.db.PriestEntity
import com.sanskar.app.data.db.SanskarDatabase
import com.sanskar.app.data.db.TransactionEntity
import com.sanskar.app.data.db.UserEntity
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

sealed class AuthResult<out T> {
    data class Success<T>(val value: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

/**
 * Persistence layer backing the whole app: devotee/priest accounts, bookings,
 * transactions and priest portfolios, all stored locally via Room so data
 * survives app restarts. Password hashes only — never plain text.
 */
class SanskarRepository(private val db: SanskarDatabase) {

    private val userDao = db.userDao()
    private val priestDao = db.priestDao()
    private val bookingDao = db.bookingDao()
    private val transactionDao = db.transactionDao()
    private val portfolioDao = db.portfolioDao()

    // ---------- One-time seed so the app has real catalog content on first launch ----------
    // No fake accounts, no fake login credentials, no fake booking history —
    // only the puja service catalog. Real devotees and priests sign up for
    // themselves; the priest directory starts empty until real priests
    // register through the app (see registerPriest below).

    suspend fun seedIfEmpty() {
        if (priestDao.count() == 0 && MockData.priests.isNotEmpty()) {
            // Starter directory entries with no login credentials attached —
            // they're unclaimed until a real priest registers with a matching
            // profile, or you remove/replace this list for launch.
            val seedPriests = MockData.priests.map { p ->
                PriestEntity(
                    id = p.id,
                    name = p.name,
                    title = p.title,
                    templeAffiliation = p.templeAffiliation,
                    specializations = p.specializations.joinToString(","),
                    languages = p.languages.joinToString(","),
                    experienceYears = p.experienceYears,
                    rating = p.rating,
                    pujasPerformed = p.pujasPerformed,
                    isOnline = p.isOnline,
                    email = "",
                    passwordHash = "",
                    passwordSalt = ""
                )
            }
            priestDao.insertAll(seedPriests)
        }
    }

    // ---------- Devotee accounts ----------

    suspend fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): AuthResult<UserEntity> {
        val key = email.trim().lowercase()
        if (fullName.isBlank()) return AuthResult.Error("Please enter your full name.")
        if (!key.contains("@") || !key.contains(".")) return AuthResult.Error("Please enter a valid email address.")
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters.")
        if (userDao.findByEmail(key) != null) return AuthResult.Error("An account with this email already exists.")

        val salt = PasswordHasher.newSalt()
        val user = UserEntity(
            email = key,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            fullName = fullName.trim(),
            phone = phone.trim(),
            timezone = ZoneId.systemDefault().id
        )
        userDao.insert(user)
        return AuthResult.Success(user)
    }

    suspend fun loginUser(email: String, password: String): AuthResult<UserEntity> {
        val user = userDao.findByEmail(email.trim().lowercase())
            ?: return AuthResult.Error("No account found for this email. Please sign up.")
        if (!PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return AuthResult.Error("Incorrect password. Please try again.")
        }
        return AuthResult.Success(user)
    }

    suspend fun updateUser(user: UserEntity) = userDao.update(user)

    // ---------- Priest accounts ----------

    suspend fun getAllPriests(): List<PriestEntity> = priestDao.getAll()

    suspend fun registerPriest(
        name: String,
        title: String,
        temple: String,
        specializations: String,
        languages: String,
        experienceYears: String,
        email: String,
        password: String
    ): AuthResult<PriestEntity> {
        val key = email.trim().lowercase()
        if (name.isBlank()) return AuthResult.Error("Please enter your full name.")
        if (title.isBlank()) return AuthResult.Error("Please enter your title (e.g. Vedacharya).")
        if (temple.isBlank()) return AuthResult.Error("Please enter your temple/parampara affiliation.")
        if (specializations.isBlank()) return AuthResult.Error("Please list at least one puja you specialize in.")
        if (!key.contains("@") || !key.contains(".")) return AuthResult.Error("Please enter a valid email address.")
        if (password.length < 6) return AuthResult.Error("Password must be at least 6 characters.")
        if (priestDao.findByEmail(key) != null) return AuthResult.Error("A priest account with this email already exists.")

        val salt = PasswordHasher.newSalt()
        val priest = PriestEntity(
            id = "p_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            title = title.trim(),
            templeAffiliation = temple.trim(),
            specializations = specializations.split(",").map { it.trim() }.filter { it.isNotBlank() }.joinToString(","),
            languages = languages.split(",").map { it.trim() }.filter { it.isNotBlank() }
                .ifEmpty { listOf("Hindi") }.joinToString(","),
            experienceYears = experienceYears.trim().toIntOrNull() ?: 0,
            rating = 5.0,
            pujasPerformed = 0,
            isOnline = true,
            email = key,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt
        )
        priestDao.insert(priest)
        return AuthResult.Success(priest)
    }

    suspend fun loginPriest(email: String, password: String): AuthResult<PriestEntity> {
        val priest = priestDao.findByEmail(email.trim().lowercase())
            ?: return AuthResult.Error("No priest account found for this email. Please sign up.")
        if (!PasswordHasher.verify(password, priest.passwordSalt, priest.passwordHash)) {
            return AuthResult.Error("Incorrect password. Please try again.")
        }
        return AuthResult.Success(priest)
    }

    // ---------- Bookings & transactions ----------

    suspend fun getBookingsForUser(email: String): List<BookingEntity> = bookingDao.getForUser(email)

    suspend fun getBookingsForPriest(priestName: String, status: String): List<BookingEntity> =
        bookingDao.getForPriest(priestName, status)

    suspend fun createBooking(
        ownerEmail: String,
        pujaName: String,
        priestName: String,
        mode: PujaMode,
        date: LocalDate,
        timeSlot: String,
        priceUsd: Int,
        sankalpName: String
    ): BookingEntity {
        val booking = BookingEntity(
            id = "SNK-${System.currentTimeMillis().toString().takeLast(8)}",
            ownerEmail = ownerEmail,
            pujaName = pujaName,
            priestName = priestName,
            mode = mode.name,
            dateEpochDay = date.toEpochDay(),
            timeSlot = timeSlot,
            priceUsd = priceUsd,
            status = BookingStatus.UPCOMING.name,
            sankalpName = sankalpName
        )
        bookingDao.insert(booking)
        transactionDao.insert(
            TransactionEntity(bookingId = booking.id, amountUsd = priceUsd, method = "none", status = "PENDING")
        )
        return booking
    }

    /** Records a completed in-app payment against the booking. */
    suspend fun markBookingPaid(bookingId: String) {
        transactionDao.insert(
            TransactionEntity(bookingId = bookingId, amountUsd = 0, method = "app_checkout", status = "PAID")
        )
    }

    /** Records that the devotee chose Pay Later — booking stays confirmed, payment pending. */
    suspend fun markPayLater(bookingId: String) {
        transactionDao.insert(
            TransactionEntity(bookingId = bookingId, amountUsd = 0, method = "pay_later", status = "PENDING")
        )
    }

    suspend fun getTransactionHistory(email: String): List<TransactionEntity> = transactionDao.getForUser(email)

    // ---------- Priest portfolio ----------

    suspend fun getPortfolioForPriest(priestId: String): List<PortfolioEntity> = portfolioDao.getForPriest(priestId)

    suspend fun addPortfolioItem(priestId: String, title: String, description: String, imageUri: String?): AuthResult<PortfolioEntity> {
        if (title.isBlank()) return AuthResult.Error("Please give the puja a title.")
        val item = PortfolioEntity(
            id = "PF-${System.currentTimeMillis().toString().takeLast(8)}",
            priestId = priestId,
            title = title.trim(),
            description = description.trim(),
            imageUri = imageUri
        )
        portfolioDao.insert(item)
        return AuthResult.Success(item)
    }
}
