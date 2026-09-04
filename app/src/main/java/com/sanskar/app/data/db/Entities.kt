package com.sanskar.app.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** A devotee account. Password is never stored in plain text — see PasswordHasher. */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val fullName: String,
    val phone: String = "",
    val city: String = "",
    val country: String = "",
    val gotra: String = "",
    val timezone: String = "",
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

/** A priest account + public profile shown to devotees. */
@Entity(tableName = "priests")
data class PriestEntity(
    @PrimaryKey val id: String,
    val name: String,
    val title: String,
    val templeAffiliation: String,
    val specializations: String, // comma-separated
    val languages: String,       // comma-separated
    val experienceYears: Int,
    val rating: Double,
    val pujasPerformed: Int,
    val isOnline: Boolean,
    val email: String = "",
    val passwordHash: String = "",
    val passwordSalt: String = ""
)

/** A devotee's puja/consultation booking. */
@Entity(
    tableName = "bookings",
    indices = [Index("ownerEmail"), Index("priestName")],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["email"],
            childColumns = ["ownerEmail"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookingEntity(
    @PrimaryKey val id: String,
    val ownerEmail: String,
    val pujaName: String,
    val priestName: String,
    val mode: String,       // PujaMode enum name
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val timeSlot: String,
    val priceUsd: Int,
    val status: String,     // BookingStatus enum name
    val sankalpName: String = "",
    val createdAtEpochMillis: Long = System.currentTimeMillis()
) {
    val date: LocalDate get() = LocalDate.ofEpochDay(dateEpochDay)
}

/** A payment attempt/record against a booking — the transaction history. */
@Entity(
    tableName = "transactions",
    indices = [Index("bookingId")],
    foreignKeys = [
        ForeignKey(
            entity = BookingEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookingId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val txId: Long = 0,
    val bookingId: String,
    val amountUsd: Int,
    val method: String,   // e.g. "stripe", "pay_later"
    val status: String,   // PENDING, PAID, REFUNDED, FAILED
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)

/** A major puja showcased with an optional photo on a priest's public profile. */
@Entity(
    tableName = "portfolio_items",
    indices = [Index("priestId")],
    foreignKeys = [
        ForeignKey(
            entity = PriestEntity::class,
            parentColumns = ["id"],
            childColumns = ["priestId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PortfolioEntity(
    @PrimaryKey val id: String,
    val priestId: String,
    val title: String,
    val description: String = "",
    val imageUri: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
