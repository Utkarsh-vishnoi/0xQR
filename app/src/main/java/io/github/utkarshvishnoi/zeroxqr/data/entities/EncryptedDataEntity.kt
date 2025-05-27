package io.github.utkarshvishnoi.zeroxqr.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing encrypted data locally.
 *
 * Phase 2: Basic encrypted storage with password-based encryption.
 * Phase 3: Will be enhanced with FIDO2 hardware-backed keys.
 */
@Entity(tableName = "encrypted_data")
data class EncryptedDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Encrypted content - Base64 encoded AES-GCM ciphertext
    val encryptedContent: String,

    // Salt used for key derivation - Base64 encoded
    val salt: String,

    // Initialization Vector for AES-GCM - Base64 encoded
    val iv: String,

    // Authentication tag from AES-GCM - Base64 encoded
    val authTag: String,

    // Operation type (ENCRYPT or DECRYPT)
    val operationType: String,

    // Preview of original content (first 50 chars, for history display)
    val contentPreview: String,

    // Timestamp when the operation was performed
    val timestamp: Long = System.currentTimeMillis(),

    // Optional: Key derivation parameters for future use
    val keyDerivationParams: String? = null
)
