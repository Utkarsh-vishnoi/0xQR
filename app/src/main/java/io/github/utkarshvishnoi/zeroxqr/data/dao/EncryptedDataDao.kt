package io.github.utkarshvishnoi.zeroxqr.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.utkarshvishnoi.zeroxqr.data.entities.EncryptedDataEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for encrypted data operations.
 *
 * Phase 2: Basic CRUD operations for local encrypted storage.
 * All operations are offline and do not require network connectivity.
 */
@Dao
interface EncryptedDataDao {

    /**
     * Insert a new encrypted data record.
     */
    @Insert
    suspend fun insertEncryptedData(data: EncryptedDataEntity): Long

    /**
     * Get all encrypted data records ordered by timestamp (newest first).
     */
    @Query("SELECT * FROM encrypted_data ORDER BY timestamp DESC")
    fun getAllEncryptedData(): Flow<List<EncryptedDataEntity>>

    /**
     * Get encrypted data by ID.
     */
    @Query("SELECT * FROM encrypted_data WHERE id = :id")
    suspend fun getEncryptedDataById(id: Long): EncryptedDataEntity?

    /**
     * Get encrypted data by operation type.
     */
    @Query("SELECT * FROM encrypted_data WHERE operationType = :operationType ORDER BY timestamp DESC")
    fun getEncryptedDataByType(operationType: String): Flow<List<EncryptedDataEntity>>

    /**
     * Delete encrypted data by ID.
     */
    @Query("DELETE FROM encrypted_data WHERE id = :id")
    suspend fun deleteEncryptedData(id: Long)

    /**
     * Delete all encrypted data records.
     */
    @Query("DELETE FROM encrypted_data")
    suspend fun deleteAllEncryptedData()

    /**
     * Get total count of encrypted records.
     */
    @Query("SELECT COUNT(*) FROM encrypted_data")
    suspend fun getEncryptedDataCount(): Int

    /**
     * Delete old records (older than specified timestamp).
     * Useful for automatic cleanup.
     */
    @Query("DELETE FROM encrypted_data WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldRecords(cutoffTimestamp: Long)
}