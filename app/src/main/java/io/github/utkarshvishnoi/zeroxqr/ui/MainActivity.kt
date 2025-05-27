package io.github.utkarshvishnoi.zeroxqr.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.ActivityMainBinding

/**
 * Main entry point for the 0xQR application.
 *
 * This activity serves as the foundation for the UI that will be
 * fully implemented in Phase 1. For now, it demonstrates that
 * the security infrastructure is properly initialized.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var app: ZeroXQRApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get application instance for security managers
        app = application as ZeroXQRApplication

        // Verify that our security infrastructure is working
        verifySecuritySetup()
    }

    /**
     * Verifies that the security infrastructure has been properly
     * initialized and is ready for cryptographic operations.
     */
    private fun verifySecuritySetup() {
        val keystoreAvailable = app.keystoreManager.isHardwareBackedKeystoreAvailable()
        val biometricStatus = app.biometricManager.checkBiometricAvailability()

        // Update UI to show security status (placeholder for Phase 1 implementation)
        binding.statusText.text = buildString {
            appendLine("0xQR Security Status")
            appendLine("Hardware Keystore: ${if (keystoreAvailable) "✓ Available" else "✗ Not Available"}")
            appendLine("Biometric Auth: ${biometricStatus.name}")
            appendLine()
            appendLine("Ready for Phase 1 UI Implementation")
        }
    }
}