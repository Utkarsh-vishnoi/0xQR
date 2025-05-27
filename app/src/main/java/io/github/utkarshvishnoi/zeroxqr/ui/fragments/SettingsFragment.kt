package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentSettingsBinding
import io.github.utkarshvishnoi.zeroxqr.security.BiometricAvailability

/**
 * Fragment for app settings and configuration.
 *
 * Phase 1: Complete UI with mock settings functionality.
 * Phase 2+: Will integrate with real preferences and encryption settings.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSettings()
        setupClickListeners()
    }

    /**
     * Sets up initial settings state based on device capabilities.
     */
    private fun setupSettings() {
        val app = requireActivity().application as ZeroXQRApplication

        // Check biometric availability and set switch state
        val biometricAvailable = app.biometricManager.checkBiometricAvailability() == BiometricAvailability.AVAILABLE
        binding.switchBiometric.isEnabled = biometricAvailable
        binding.switchBiometric.isChecked = biometricAvailable

        // Set default values for other settings
        binding.switchAutoClear.isChecked = true
        binding.switchLocalStorage.isChecked = true
    }

    /**
     * Sets up click listeners for all interactive elements.
     */
    private fun setupClickListeners() {
        // Security settings
        binding.switchBiometric.setOnCheckedChangeListener { _, isChecked ->
            handleBiometricToggle(isChecked)
        }

        binding.switchAutoClear.setOnCheckedChangeListener { _, isChecked ->
            handleAutoClearToggle(isChecked)
        }

        // QR Code settings
        binding.tvErrorCorrectionValue.setOnClickListener {
            showErrorCorrectionDialog()
        }

        binding.tvChunkSizeValue.setOnClickListener {
            showChunkSizeDialog()
        }

        // Storage settings
        binding.switchLocalStorage.setOnCheckedChangeListener { _, isChecked ->
            handleLocalStorageToggle(isChecked)
        }

        binding.tvStorageLocationValue.setOnClickListener {
            showStorageLocationDialog()
        }

        // About section - placeholder for future implementation
        // TODO: Implement license viewer and security audit info
    }

    /**
     * Handles biometric authentication toggle.
     * TODO Phase 3: Integrate with real biometric setup
     */
    private fun handleBiometricToggle(enabled: Boolean) {
        if (enabled) {
            // TODO Phase 3: Trigger biometric enrollment/setup
            showFeatureComingSoon("Biometric setup - Coming in Phase 3")
        }
        showSettingChanged("Biometric authentication ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Handles auto-clear setting toggle.
     * TODO Phase 2: Store in SharedPreferences
     */
    private fun handleAutoClearToggle(enabled: Boolean) {
        // TODO Phase 2: Save to SharedPreferences
        showSettingChanged("Auto-clear ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Handles local storage toggle.
     * TODO Phase 2: Integrate with database settings
     */
    private fun handleLocalStorageToggle(enabled: Boolean) {
        // TODO Phase 2: Configure local database usage
        showSettingChanged("Local storage ${if (enabled) "enabled" else "disabled"}")
    }

    /**
     * Shows dialog for selecting QR error correction level.
     * TODO Phase 4: Implement real QR settings
     */
    private fun showErrorCorrectionDialog() {
        // TODO Phase 4: Show dialog with options (Low, Medium, Quartile, High)
        showFeatureComingSoon("QR Error Correction Settings - Coming in Phase 4")
    }

    /**
     * Shows dialog for selecting QR chunk size.
     * TODO Phase 4: Implement real QR chunking settings
     */
    private fun showChunkSizeDialog() {
        // TODO Phase 4: Show dialog with size options (1024, 2048, 4096 bytes)
        showFeatureComingSoon("QR Chunk Size Settings - Coming in Phase 4")
    }

    /**
     * Shows dialog for selecting storage location.
     * TODO Phase 2: Implement storage location selection
     */
    private fun showStorageLocationDialog() {
        // TODO Phase 2: Show dialog with options (Internal, SD Card)
        showFeatureComingSoon("Storage Location Settings - Coming in Phase 2")
    }

    /**
     * Shows setting changed confirmation.
     */
    private fun showSettingChanged(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    /**
     * Shows placeholder message for features coming in later phases.
     */
    private fun showFeatureComingSoon(feature: String) {
        android.widget.Toast.makeText(
            requireContext(),
            "$feature - UI ready, functionality coming soon!",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}