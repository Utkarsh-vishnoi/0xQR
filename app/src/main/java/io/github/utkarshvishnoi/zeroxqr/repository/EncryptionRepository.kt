package io.github.utkarshvishnoi.zeroxqr.repository

import io.github.utkarshvishnoi.zeroxqr.data.dao.EncryptedDataDao
import io.github.utkarshvishnoi.zeroxqr.data.entities.EncryptedDataEntity
import io.github.utkarshvishnoi.zeroxqr.encryption.AESEncryptionService
import io.github.utkarshvishnoi.zeroxqr.encryption.DecryptionResult
import io.github.utkarshvishnoi.zeroxqr.ui.models.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for handling encryption operations and data persistence.
 *
 * Phase 2: Integrates AES encryption service with Room database.
 * Provides clean API for UI layer while handling data layer complexity.
 */
class EncryptionRepository(
    private val encryptedDataDao: EncryptedDataDao,
    private val encryptionService: AESEncryptionService
) {

    /**
     * Encrypts text and saves to local database.
     *
     * @param plaintext Text to encrypt
     * @param password Password for encryption (Phase 2 interim solution)
     * @return RepositoryResult with operation outcome and unified format data
     */
    suspend fun encryptAndSave(
        plaintext: String,
        password: String
    ): RepositoryResult<EncryptionResultData> {
        // Validate input
        val validation = encryptionService.validateEncryptionInput(plaintext, password)
        if (validation is io.github.utkarshvishnoi.zeroxqr.encryption.ValidationResult.Error) {
            return RepositoryResult.Error(validation.message)
        }

        // Perform encryption with unified format
        when (val encryptionResult =
            encryptionService.encryptToUnifiedFormat(plaintext, password)) {
            is io.github.utkarshvishnoi.zeroxqr.encryption.UnifiedEncryptionResult.Success -> {
                try {
                    // Create database entity
                    val entity = EncryptedDataEntity(
                        encryptedContent = encryptionResult.components.encryptedContent,
                        salt = encryptionResult.components.salt,
                        iv = encryptionResult.components.iv,
                        authTag = encryptionResult.components.authTag,
                        operationType = "ENCRYPT",
                        contentPreview = generatePreview(plaintext),
                        timestamp = System.currentTimeMillis()
                    )

                    // Save to database
                    val id = encryptedDataDao.insertEncryptedData(entity)

                    return RepositoryResult.Success(
                        EncryptionResultData(
                            id = id,
                            unifiedFormat = encryptionResult.unifiedData,
                            preview = generatePreview(plaintext)
                        )
                    )

                } catch (e: Exception) {
                    return RepositoryResult.Error("Failed to save encrypted data: ${e.message}")
                }
            }

            is io.github.utkarshvishnoi.zeroxqr.encryption.UnifiedEncryptionResult.Error -> {
                return RepositoryResult.Error(encryptionResult.message)
            }
        }
    }

    /**
     * Decrypts data from unified format string.
     *
     * @param unifiedData The unified format string (from QR code or manual input)
     * @param password Password for decryption
     * @param saveToHistory Whether to save decryption operation to history
     * @return RepositoryResult with decrypted text
     */
    suspend fun decryptFromUnified(
        unifiedData: String,
        password: String,
        saveToHistory: Boolean = true
    ): RepositoryResult<String> {
        // Perform decryption using unified format
        when (val decryptionResult =
            encryptionService.decryptFromUnifiedFormat(unifiedData, password)) {
            is DecryptionResult.Success -> {
                // Optionally save decryption operation to history
                if (saveToHistory) {
                    try {
                        // Parse the unified format to get components for storage
                        val parseResult = encryptionService.parseUnifiedFormat(unifiedData)
                        if (parseResult is io.github.utkarshvishnoi.zeroxqr.encryption.UnifiedFormatResult.Success) {
                            val components = parseResult.components
                            val entity = EncryptedDataEntity(
                                encryptedContent = components.encryptedContent,
                                salt = components.salt,
                                iv = components.iv,
                                authTag = components.authTag,
                                operationType = "DECRYPT",
                                contentPreview = generatePreview(decryptionResult.plaintext),
                                timestamp = System.currentTimeMillis()
                            )
                            encryptedDataDao.insertEncryptedData(entity)
                        }
                    } catch (e: Exception) {
                        // Don't fail decryption if history save fails
                        android.util.Log.w(
                            "EncryptionRepository",
                            "Failed to save decrypt history",
                            e
                        )
                    }
                }

                return RepositoryResult.Success(decryptionResult.plaintext)
            }

            is DecryptionResult.Error -> {
                return RepositoryResult.Error(decryptionResult.message)
            }
        }
    }

    /**
     * Retrieves encryption history for display in UI.
     */
    fun getEncryptionHistory(): Flow<List<HistoryItem>> {
        return encryptedDataDao.getAllEncryptedData().map { entities ->
            entities.map { entity ->
                HistoryItem(
                    id = entity.id,
                    operationType = when (entity.operationType) {
                        "ENCRYPT" -> HistoryItem.OperationType.ENCRYPT
                        "DECRYPT" -> HistoryItem.OperationType.DECRYPT
                        else -> HistoryItem.OperationType.ENCRYPT
                    },
                    preview = entity.contentPreview,
                    timestamp = entity.timestamp,
                    fullData = "${entity.encryptedContent}|${entity.salt}|${entity.iv}|${entity.authTag}"
                )
            }
        }
    }

    /**
     * Retrieves encrypted data by ID for detailed operations.
     */
    suspend fun getEncryptedDataById(id: Long): RepositoryResult<EncryptedDataEntity> {
        return try {
            val entity = encryptedDataDao.getEncryptedDataById(id)
            if (entity != null) {
                RepositoryResult.Success(entity)
            } else {
                RepositoryResult.Error("Encrypted data not found")
            }
        } catch (e: Exception) {
            RepositoryResult.Error("Failed to retrieve encrypted data: ${e.message}")
        }
    }

    /**
     * Deletes a specific history item.
     */
    suspend fun deleteHistoryItem(id: Long): RepositoryResult<Unit> {
        return try {
            encryptedDataDao.deleteEncryptedData(id)
            RepositoryResult.Success(Unit)
        } catch (e: Exception) {
            RepositoryResult.Error("Failed to delete history item: ${e.message}")
        }
    }

    /**
     * Deletes all encryption history.
     */
    suspend fun deleteAllHistory(): RepositoryResult<Unit> {
        return try {
            encryptedDataDao.deleteAllEncryptedData()
            RepositoryResult.Success(Unit)
        } catch (e: Exception) {
            RepositoryResult.Error("Failed to delete all history: ${e.message}")
        }
    }

    /**
     * Gets total count of encrypted records.
     */
    suspend fun getHistoryCount(): Int {
        return try {
            encryptedDataDao.getEncryptedDataCount()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Cleans up old records (older than specified days).
     */
    suspend fun cleanupOldRecords(olderThanDays: Int): RepositoryResult<Unit> {
        return try {
            val cutoffTimestamp =
                System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
            encryptedDataDao.deleteOldRecords(cutoffTimestamp)
            RepositoryResult.Success(Unit)
        } catch (e: Exception) {
            RepositoryResult.Error("Failed to cleanup old records: ${e.message}")
        }
    }

    /**
     * Generates a preview string for display purposes.
     */
    private fun generatePreview(text: String): String {
        return if (text.length <= 50) {
            text
        } else {
            text.take(47) + "..."
        }
    }
}

/**
 * Result wrapper for repository operations.
 */
sealed class RepositoryResult<out T> {
    data class Success<out T>(val data: T) : RepositoryResult<T>()
    data class Error(val message: String) : RepositoryResult<Nothing>()
}

/**
 * Data class for encryption result with unified format.
 */
data class EncryptionResultData(
    val id: Long,
    val unifiedFormat: String,
    val preview: String
)