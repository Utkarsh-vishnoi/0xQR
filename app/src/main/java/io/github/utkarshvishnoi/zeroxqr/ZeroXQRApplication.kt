package io.github.utkarshvishnoi.zeroxqr

import android.app.Application
import io.github.utkarshvishnoi.zeroxqr.data.database.ZeroXQRDatabase
import io.github.utkarshvishnoi.zeroxqr.encryption.AESEncryptionService
import io.github.utkarshvishnoi.zeroxqr.repository.EncryptionRepository
import io.github.utkarshvishnoi.zeroxqr.security.BiometricManager
import io.github.utkarshvishnoi.zeroxqr.security.KeystoreManager
import io.github.utkarshvishnoi.zeroxqr.security.PasswordStrengthAnalyzer

/**
 * Application class for 0xQR - initializes core security components
 * and Phase 2 encryption infrastructure for air-gapped cryptographic operations.
 *
 * Phase 2: Added real encryption service and database integration.
 * Phase 3: Will enhance with FIDO2 hardware-backed security.
 */
class ZeroXQRApplication : Application() {

    // Core security managers - initialized once for app lifecycle
    lateinit var keystoreManager: KeystoreManager
        private set

    lateinit var biometricManager: BiometricManager
        private set

    // Phase 2: Encryption infrastructure
    lateinit var database: ZeroXQRDatabase
        private set

    lateinit var encryptionService: AESEncryptionService
        private set

    lateinit var encryptionRepository: EncryptionRepository
        private set

    // Phase 2: Security analysis
    lateinit var passwordStrengthAnalyzer: PasswordStrengthAnalyzer
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize security infrastructure
        initializeSecurityInfrastructure()

        // Phase 2: Initialize encryption infrastructure
        initializeEncryptionInfrastructure()
    }

    /**
     * Sets up the core security components that will form the foundation
     * for all cryptographic operations in the application.
     */
    private fun initializeSecurityInfrastructure() {
        try {
            // Initialize Android Keystore for hardware-backed security
            keystoreManager = KeystoreManager(this)

            // Initialize biometric authentication manager
            biometricManager = BiometricManager(this)

            // Verify security capabilities on this device
            verifySecurityCapabilities()

        } catch (e: Exception) {
            // In a production app, we'd handle this more gracefully
            // For now, we'll log and continue
            android.util.Log.e("ZeroXQR", "Failed to initialize security infrastructure", e)
        }
    }

    /**
     * Phase 2: Initializes encryption service and database for real
     * AES-GCM operations with local storage.
     */
    private fun initializeEncryptionInfrastructure() {
        try {
            // Initialize Room database
            database = ZeroXQRDatabase.getDatabase(this)

            // Initialize AES encryption service
            encryptionService = AESEncryptionService()

            // Initialize password strength analyzer
            passwordStrengthAnalyzer = PasswordStrengthAnalyzer()

            // Initialize repository with database and encryption service
            encryptionRepository = EncryptionRepository(
                encryptedDataDao = database.encryptedDataDao(),
                encryptionService = encryptionService
            )

            android.util.Log.d(
                "ZeroXQR",
                "Phase 2 encryption infrastructure initialized successfully"
            )

        } catch (e: Exception) {
            android.util.Log.e("ZeroXQR", "Failed to initialize encryption infrastructure", e)
        }
    }

    /**
     * Verifies that this device has the necessary security capabilities
     * for air-gapped cryptographic operations.
     */
    private fun verifySecurityCapabilities() {
        // Check if device supports hardware-backed keystore
        val hasHardwareKeystore = keystoreManager.isHardwareBackedKeystoreAvailable()

        // Check biometric capabilities
        val biometricStatus = biometricManager.checkBiometricAvailability()

        // Log capabilities for debugging (remove in production)
        android.util.Log.d("ZeroXQR", "Hardware Keystore: $hasHardwareKeystore")
        android.util.Log.d("ZeroXQR", "Biometric Status: $biometricStatus")

        // Phase 2: Test encryption service
        try {
            val testResult = encryptionService.encrypt("test", "testpassword123")
            android.util.Log.d(
                "ZeroXQR",
                "Encryption service test: ${testResult is io.github.utkarshvishnoi.zeroxqr.encryption.EncryptionResult.Success}"
            )
        } catch (e: Exception) {
            android.util.Log.w("ZeroXQR", "Encryption service test failed", e)
        }
    }
}