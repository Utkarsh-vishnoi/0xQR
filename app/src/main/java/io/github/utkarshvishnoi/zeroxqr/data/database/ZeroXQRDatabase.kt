package io.github.utkarshvishnoi.zeroxqr.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.utkarshvishnoi.zeroxqr.data.dao.EncryptedDataDao
import io.github.utkarshvishnoi.zeroxqr.data.entities.EncryptedDataEntity

/**
 * Room database for 0xQR application.
 *
 * Phase 2: Local encrypted storage without network dependencies.
 * All data remains on device for air-gapped security.
 */
@Database(
    entities = [EncryptedDataEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ZeroXQRDatabase : RoomDatabase() {

    abstract fun encryptedDataDao(): EncryptedDataDao

    companion object {
        private const val DATABASE_NAME = "0xQR_db"

        @Volatile
        private var INSTANCE: ZeroXQRDatabase? = null

        fun getDatabase(context: Context): ZeroXQRDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZeroXQRDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
