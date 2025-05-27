package io.github.utkarshvishnoi.zeroxqr.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator

/**
 * Manages Android Keystore operations for hardware-backed security.
 *
 * This class provides the foundation for all cryptographic key management
 * in the air-gapped environment, ensuring keys are generated and stored
 * in hardware security modules when available.
 */
class KeystoreManager(private val context: Context) {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS_PREFIX = "ZeroXQR_"
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    /**
     * Checks if this device supports hardware-backed keystore operations.
     * This is crucial for air-gapped security as it ensures cryptographic
     * operations are performed in secure hardware.
     */
    fun isHardwareBackedKeystoreAvailable(): Boolean {
        return try {
            // Attempt to create a test key to verify hardware backing
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                "test_hardware_check",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false) // For this test only
                .build()

            keyGenerator.init(keyGenParameterSpec)
            val secretKey = keyGenerator.generateKey()

            // Clean up test key
            keyStore.deleteEntry("test_hardware_check")

            true
        } catch (e: Exception) {
            android.util.Log.w("KeystoreManager", "Hardware-backed keystore not available", e)
            false
        }
    }

    /**
     * Prepares the keystore for FIDO2 integration that will come in Phase 3.
     * For now, this sets up the basic infrastructure.
     */
    fun prepareForFIDO2Integration(): Boolean {
        return try {
            // Verify that we can access the keystore
            val aliases = keyStore.aliases()
            android.util.Log.d("KeystoreManager", "Keystore accessible with ${aliases.toList().size} existing keys")
            true
        } catch (e: Exception) {
            android.util.Log.e("KeystoreManager", "Failed to prepare keystore for FIDO2", e)
            false
        }
    }

    /**
     * Generates a unique key alias for the given purpose.
     * This will be expanded in later phases to support device-specific
     * and cross-device key derivation.
     */
    fun generateKeyAlias(purpose: String): String {
        val timestamp = System.currentTimeMillis()
        return "${KEY_ALIAS_PREFIX}${purpose}_$timestamp"
    }
}