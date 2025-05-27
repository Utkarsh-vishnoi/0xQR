package io.github.utkarshvishnoi.zeroxqr

import android.app.Application
import io.github.utkarshvishnoi.zeroxqr.security.KeystoreManager
import io.github.utkarshvishnoi.zeroxqr.security.BiometricManager

/**
 * Application class for 0xQR - initializes core security components
 * for air-gapped cryptographic operations.
 *
 * This class sets up the fundamental security infrastructure needed
 * for hardware-backed encryption without any network dependencies.
 */
class ZeroXQRApplication : Application() {

    // Core security managers - initialized once for app lifecycle
    lateinit var keystoreManager: KeystoreManager
        private set

    lateinit var biometricManager: BiometricManager
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize security infrastructure
        initializeSecurityInfrastructure()
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
    }
}