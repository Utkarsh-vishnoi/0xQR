package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentHomeBinding
import io.github.utkarshvishnoi.zeroxqr.security.BiometricAvailability

/**
 * Home fragment displaying app overview and quick actions.
 *
 * Phase 1: Complete UI implementation with mock security status
 * and navigation to other screens.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSecurityStatus()
        setupQuickActions()
    }

    /**
     * Updates the security status display with current device capabilities.
     */
    private fun setupSecurityStatus() {
        val app = requireActivity().application as ZeroXQRApplication

        // Update keystore status
        val keystoreAvailable = app.keystoreManager.isHardwareBackedKeystoreAvailable()
        binding.tvKeystoreStatus.text = if (keystoreAvailable) {
            getString(R.string.security_available)
        } else {
            getString(R.string.security_not_available)
        }
        binding.tvKeystoreStatus.setTextColor(
            if (keystoreAvailable)
                requireContext().getColor(R.color.encryption_success)
            else
                requireContext().getColor(R.color.encryption_error)
        )

        // Update biometric status
        val biometricStatus = app.biometricManager.checkBiometricAvailability()
        binding.tvBiometricStatus.text = when (biometricStatus) {
            BiometricAvailability.AVAILABLE -> getString(R.string.security_available)
            else -> getString(R.string.security_not_available)
        }
        binding.tvBiometricStatus.setTextColor(
            if (biometricStatus == BiometricAvailability.AVAILABLE)
                requireContext().getColor(R.color.encryption_success)
            else
                requireContext().getColor(R.color.encryption_error)
        )
    }

    /**
     * Sets up click listeners for quick action cards.
     */
    private fun setupQuickActions() {
        binding.cardQuickEncrypt.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_encrypt)
        }

        binding.cardQuickDecrypt.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_decrypt)
        }

        binding.cardScanQr.setOnClickListener {
            // TODO: Navigate to QR scanner (will be implemented in Phase 4)
            // For now, show a placeholder message
            showFeatureComingSoon("QR Scanner - Coming in Phase 4")
        }

        binding.cardViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_history)
        }
    }

    /**
     * Shows a placeholder message for features coming in later phases.
     */
    private fun showFeatureComingSoon(feature: String) {
        // TODO: Replace with proper snackbar or dialog
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