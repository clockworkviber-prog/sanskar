package com.sanskar.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        PriestEntity::class,
        BookingEntity::class,
        TransactionEntity::class,
        PortfolioEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SanskarDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun priestDao(): PriestDao
    abstract fun bookingDao(): BookingDao
    abstract fun transactionDao(): TransactionDao
    abstract fun portfolioDao(): PortfolioDao

    companion object {
        @Volatile private var instance: SanskarDatabase? = null

        fun getInstance(context: Context): SanskarDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SanskarDatabase::class.java,
                    "sanskar.db"
                ).build().also { instance = it }
            }
    }
}
