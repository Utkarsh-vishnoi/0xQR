package io.github.utkarshvishnoi.zeroxqr.encryption

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.asKotlinRandom

/**
 * AES-GCM encryption service for Phase 2 implementation.
 *
 * Phase 2: Password-based encryption using PBKDF2 for key derivation.
 * Phase 3: Will be enhanced with FIDO2 hardware-backed keys.
 *
 * This implementation is designed for air-gapped environments with no
 * network dependencies or external key management services.
 *
 * Phase 2 Update: Unified encrypted data format for QR code compatibility.
 */
class AESEncryptionService {

    companion object {
        private const val AES_ALGORITHM = "AES"
        private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256"

        // Security parameters
        private const val AES_KEY_LENGTH = 256 // bits
        private const val GCM_IV_LENGTH = 12 // bytes (96 bits recommended for GCM)
        private const val GCM_TAG_LENGTH = 16 // bytes (128 bits)
        private const val SALT_LENGTH = 32 // bytes
        private const val PBKDF2_ITERATIONS = 100000 // Iterations for PBKDF2

        // Unified format constants for QR code compatibility
        private const val FORMAT_HEADER = "0xQR"
        private const val FORMAT_VERSION = "v2"
        private const val FORMAT_SEPARATOR = "|"
    }

    private val secureRandom = SecureRandom()

    /**
     * Encrypts plaintext using AES-GCM with password-based key derivation.
     *
     * @param plaintext The text to encrypt
     * @param password Password for key derivation (Phase 2 interim solution)
     * @return EncryptionResult containing all necessary components
     */
    fun encrypt(plaintext: String, password: String): EncryptionResult {
        try {
            // Generate random salt for key derivation
            val salt = ByteArray(SALT_LENGTH)
            secureRandom.nextBytes(salt)

            // Derive key using PBKDF2
            val secretKey = deriveKeyFromPassword(password, salt)

            // Generate random IV for GCM mode
            val iv = ByteArray(GCM_IV_LENGTH)
            secureRandom.nextBytes(iv)

            // Initialize cipher for encryption
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec)

            // Encrypt the plaintext
            val plaintextBytes = plaintext.toByteArray(Charsets.UTF_8)
            val ciphertextWithTag = cipher.doFinal(plaintextBytes)

            // Extract ciphertext and authentication tag
            val ciphertextLength = ciphertextWithTag.size - GCM_TAG_LENGTH
            val ciphertext = ciphertextWithTag.copyOfRange(0, ciphertextLength)
            val authTag = ciphertextWithTag.copyOfRange(ciphertextLength, ciphertextWithTag.size)

            return EncryptionResult.Success(
                encryptedContent = Base64.encodeToString(ciphertext, Base64.NO_WRAP),
                salt = Base64.encodeToString(salt, Base64.NO_WRAP),
                iv = Base64.encodeToString(iv, Base64.NO_WRAP),
                authTag = Base64.encodeToString(authTag, Base64.NO_WRAP)
            )

        } catch (e: Exception) {
            return EncryptionResult.Error("Encryption failed: ${e.message}")
        }
    }

    /**
     * Creates unified encrypted data format for QR code and easy sharing.
     * Format: 0xQR|v2|encrypted_content|salt|iv|auth_tag|checksum
     *
     * This format is designed to be:
     * - QR code friendly (compact but readable)
     * - Version-aware for future compatibility
     * - Self-validating with checksum
     * - Human-readable for debugging
     */
    fun createUnifiedFormat(encryptionResult: EncryptionResult.Success): String {
        val components = listOf(
            FORMAT_HEADER,
            FORMAT_VERSION,
            encryptionResult.encryptedContent,
            encryptionResult.salt,
            encryptionResult.iv,
            encryptionResult.authTag
        )

        // Create checksum of all components for validation
        val dataToHash = components.joinToString(FORMAT_SEPARATOR)
        val checksum = generateChecksum(dataToHash)

        // Return complete unified format
        return "$dataToHash$FORMAT_SEPARATOR$checksum"
    }

    /**
     * Parses unified encrypted data format back to components.
     * Validates format, version, and checksum.
     */
    fun parseUnifiedFormat(unifiedData: String): UnifiedFormatResult {
        try {
            val parts = unifiedData.split(FORMAT_SEPARATOR)

            // Validate basic structure
            if (parts.size != 7) {
                return UnifiedFormatResult.Error("Invalid format: expected 7 components, got ${parts.size}")
            }

            val header = parts[0]
            val version = parts[1]
            val encryptedContent = parts[2]
            val salt = parts[3]
            val iv = parts[4]
            val authTag = parts[5]
            val checksum = parts[6]

            // Validate header
            if (header != FORMAT_HEADER) {
                return UnifiedFormatResult.Error("Invalid format: unrecognized header '$header'")
            }

            // Validate version (currently only v2 supported)
            if (version != FORMAT_VERSION) {
                return UnifiedFormatResult.Error("Unsupported version: '$version'. Current version: $FORMAT_VERSION")
            }

            // Validate checksum
            val dataToVerify = parts.take(6).joinToString(FORMAT_SEPARATOR)
            val expectedChecksum = generateChecksum(dataToVerify)
            if (checksum != expectedChecksum) {
                return UnifiedFormatResult.Error("Data integrity check failed: checksum mismatch")
            }

            // Validate Base64 encoding of components
            if (!isValidBase64(encryptedContent) || !isValidBase64(salt) ||
                !isValidBase64(iv) || !isValidBase64(authTag)
            ) {
                return UnifiedFormatResult.Error("Invalid format: corrupted encryption components")
            }

            return UnifiedFormatResult.Success(
                EncryptionComponents(
                    encryptedContent = encryptedContent,
                    salt = salt,
                    iv = iv,
                    authTag = authTag
                )
            )

        } catch (e: Exception) {
            return UnifiedFormatResult.Error("Failed to parse unified format: ${e.message}")
        }
    }

    /**
     * Convenience method: Encrypt and return unified format directly.
     * This is what most UI components will use.
     */
    fun encryptToUnifiedFormat(plaintext: String, password: String): UnifiedEncryptionResult {
        return when (val encryptResult = encrypt(plaintext, password)) {
            is EncryptionResult.Success -> {
                val unifiedFormat = createUnifiedFormat(encryptResult)
                UnifiedEncryptionResult.Success(
                    unifiedData = unifiedFormat,
                    components = EncryptionComponents(
                        encryptedContent = encryptResult.encryptedContent,
                        salt = encryptResult.salt,
                        iv = encryptResult.iv,
                        authTag = encryptResult.authTag
                    )
                )
            }

            is EncryptionResult.Error -> {
                UnifiedEncryptionResult.Error(encryptResult.message)
            }
        }
    }

    /**
     * Convenience method: Parse unified format and decrypt directly.
     * This is what the decrypt fragment will use.
     */
    fun decryptFromUnifiedFormat(unifiedData: String, password: String): DecryptionResult {
        return when (val parseResult = parseUnifiedFormat(unifiedData)) {
            is UnifiedFormatResult.Success -> {
                val components = parseResult.components
                decrypt(
                    encryptedContent = components.encryptedContent,
                    salt = components.salt,
                    iv = components.iv,
                    authTag = components.authTag,
                    password = password
                )
            }

            is UnifiedFormatResult.Error -> {
                DecryptionResult.Error(parseResult.message)
            }
        }
    }

    /**
     * Generates SHA-256 checksum for data integrity verification.
     */
    private fun generateChecksum(data: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
            .take(8) // Use first 8 chars for compactness
    }

    /**
     * Decrypts ciphertext using AES-GCM with password-based key derivation.
     *
     * @param encryptedContent Base64 encoded ciphertext
     * @param salt Base64 encoded salt used for key derivation
     * @param iv Base64 encoded initialization vector
     * @param authTag Base64 encoded authentication tag
     * @param password Password for key derivation
     * @return DecryptionResult containing plaintext or error
     */
    fun decrypt(
        encryptedContent: String,
        salt: String,
        iv: String,
        authTag: String,
        password: String
    ): DecryptionResult {
        try {
            // Decode Base64 components
            val ciphertext = Base64.decode(encryptedContent, Base64.NO_WRAP)
            val saltBytes = Base64.decode(salt, Base64.NO_WRAP)
            val ivBytes = Base64.decode(iv, Base64.NO_WRAP)
            val authTagBytes = Base64.decode(authTag, Base64.NO_WRAP)

            // Derive key using the same password and salt
            val secretKey = deriveKeyFromPassword(password, saltBytes)

            // Combine ciphertext and authentication tag for GCM
            val ciphertextWithTag = ciphertext + authTagBytes

            // Initialize cipher for decryption
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, ivBytes)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

            // Decrypt and verify authentication tag
            val plaintextBytes = cipher.doFinal(ciphertextWithTag)
            val plaintext = String(plaintextBytes, Charsets.UTF_8)

            return DecryptionResult.Success(plaintext)

        } catch (e: Exception) {
            return DecryptionResult.Error("Decryption failed: ${e.message}")
        }
    }

    /**
     * Derives an AES key from password using PBKDF2.
     *
     * Phase 2: Uses password-based key derivation as interim solution.
     * Phase 3: Will be replaced with FIDO2 hardware-backed key derivation.
     */
    private fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM)
        val spec: KeySpec = PBEKeySpec(
            password.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            AES_KEY_LENGTH
        )
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, AES_ALGORITHM)
    }

    /**
     * Validates input for encryption operations.
     */
    fun validateEncryptionInput(plaintext: String, password: String): ValidationResult {
        return when {
            plaintext.isBlank() -> ValidationResult.Error("Plaintext cannot be empty")
            plaintext.length > 10000 -> ValidationResult.Error("Text too long (max 10,000 characters)")
            password.isBlank() -> ValidationResult.Error("Password cannot be empty")
            password.length < 8 -> ValidationResult.Error("Password must be at least 8 characters")
            else -> ValidationResult.Success
        }
    }

    /**
     * Validates input for decryption operations.
     */
    fun validateDecryptionInput(
        encryptedContent: String,
        salt: String,
        iv: String,
        authTag: String,
        password: String
    ): ValidationResult {
        return when {
            encryptedContent.isBlank() -> ValidationResult.Error("Encrypted content cannot be empty")
            salt.isBlank() -> ValidationResult.Error("Salt cannot be empty")
            iv.isBlank() -> ValidationResult.Error("IV cannot be empty")
            authTag.isBlank() -> ValidationResult.Error("Authentication tag cannot be empty")
            password.isBlank() -> ValidationResult.Error("Password cannot be empty")
            !isValidBase64(encryptedContent) -> ValidationResult.Error("Invalid encrypted content format")
            !isValidBase64(salt) -> ValidationResult.Error("Invalid salt format")
            !isValidBase64(iv) -> ValidationResult.Error("Invalid IV format")
            !isValidBase64(authTag) -> ValidationResult.Error("Invalid authentication tag format")
            else -> ValidationResult.Success
        }
    }

    /**
     * Validates if a string is valid Base64.
     */
    private fun isValidBase64(input: String): Boolean {
        return try {
            Base64.decode(input, Base64.NO_WRAP)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Generates a secure random password for testing purposes.
     * TODO: Remove in production, users should provide their own passwords.
     */
    fun generateSecurePassword(length: Int = 16): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        return (1..length)
            .map { chars.random(secureRandom.asKotlinRandom()) }
            .joinToString("")
    }
}

/**
 * Result classes for encryption operations.
 */
sealed class EncryptionResult {
    data class Success(
        val encryptedContent: String,
        val salt: String,
        val iv: String,
        val authTag: String
    ) : EncryptionResult()

    data class Error(val message: String) : EncryptionResult()
}

sealed class DecryptionResult {
    data class Success(val plaintext: String) : DecryptionResult()
    data class Error(val message: String) : DecryptionResult()
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

/**
 * New result classes for unified format operations.
 */
sealed class UnifiedEncryptionResult {
    data class Success(
        val unifiedData: String,
        val components: EncryptionComponents
    ) : UnifiedEncryptionResult()

    data class Error(val message: String) : UnifiedEncryptionResult()
}

sealed class UnifiedFormatResult {
    data class Success(val components: EncryptionComponents) : UnifiedFormatResult()
    data class Error(val message: String) : UnifiedFormatResult()
}

/**
 * Data class for holding encryption components.
 */
data class EncryptionComponents(
    val encryptedContent: String,
    val salt: String,
    val iv: String,
    val authTag: String
)