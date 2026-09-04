package com.sanskar.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface PriestDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(priest: PriestEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(priests: List<PriestEntity>)

    @Update
    suspend fun update(priest: PriestEntity)

    @Query("SELECT * FROM priests ORDER BY rating DESC")
    fun observeAll(): Flow<List<PriestEntity>>

    @Query("SELECT * FROM priests ORDER BY rating DESC")
    suspend fun getAll(): List<PriestEntity>

    @Query("SELECT * FROM priests WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): PriestEntity?

    @Query("SELECT * FROM priests WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): PriestEntity?

    @Query("SELECT COUNT(*) FROM priests")
    suspend fun count(): Int
}

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(booking: BookingEntity)

    @Update
    suspend fun update(booking: BookingEntity)

    @Query("SELECT * FROM bookings WHERE ownerEmail = :email ORDER BY createdAtEpochMillis DESC")
    fun observeForUser(email: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE ownerEmail = :email ORDER BY createdAtEpochMillis DESC")
    suspend fun getForUser(email: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE priestName = :priestName AND status = :status ORDER BY dateEpochDay ASC")
    suspend fun getForPriest(priestName: String, status: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): BookingEntity?

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun count(): Int
}

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE bookingId = :bookingId ORDER BY createdAtEpochMillis DESC")
    suspend fun getForBooking(bookingId: String): List<TransactionEntity>

    @Query("""
        SELECT t.* FROM transactions t
        INNER JOIN bookings b ON b.id = t.bookingId
        WHERE b.ownerEmail = :email
        ORDER BY t.createdAtEpochMillis DESC
    """)
    suspend fun getForUser(email: String): List<TransactionEntity>
}

@Dao
interface PortfolioDao {
    @Insert
    suspend fun insert(item: PortfolioEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<PortfolioEntity>)

    @Query("SELECT * FROM portfolio_items WHERE priestId = :priestId ORDER BY createdAtEpochMillis DESC")
    suspend fun getForPriest(priestId: String): List<PortfolioEntity>

    @Query("SELECT COUNT(*) FROM portfolio_items")
    suspend fun count(): Int
}
