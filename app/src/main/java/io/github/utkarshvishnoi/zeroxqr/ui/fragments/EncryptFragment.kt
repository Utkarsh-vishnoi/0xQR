package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentEncryptBinding
import io.github.utkarshvishnoi.zeroxqr.repository.RepositoryResult
import io.github.utkarshvishnoi.zeroxqr.ui.dialogs.PasswordDialog
import kotlinx.coroutines.launch

/**
 * Fragment for text encryption functionality.
 *
 * Phase 1: Complete UI with mock encryption functionality.
 * Phase 2: Real AES-GCM encryption with password-based key derivation.
 * Phase 3: Will enhance with FIDO2 hardware-backed security.
 */
class EncryptFragment : Fragment() {

    private var _binding: FragmentEncryptBinding? = null
    private val binding get() = _binding!!

    private lateinit var app: ZeroXQRApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEncryptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get application instance for encryption services
        app = requireActivity().application as ZeroXQRApplication

        setupClickListeners()
    }

    /**
     * Sets up click listeners for all interactive elements.
     */
    private fun setupClickListeners() {
        binding.btnEncrypt.setOnClickListener {
            initiateEncryption()
        }

        binding.btnClear.setOnClickListener {
            clearInput()
        }

        binding.btnGenerateQr.setOnClickListener {
            generateQRCode()
        }

        binding.btnSave.setOnClickListener {
            // Data is automatically saved during encryption in Phase 2
            showSuccessMessage("Data is automatically saved during encryption")
        }
    }

    /**
     * Phase 2: Initiates real encryption process with password dialog.
     * Replaces mock encryption from Phase 1.
     */
    private fun initiateEncryption() {
        val inputText = binding.etInputText.text.toString().trim()

        if (inputText.isEmpty()) {
            binding.textInputLayout.error = "Please enter text to encrypt"
            return
        }

        binding.textInputLayout.error = null

        // Show password dialog for encryption
        val passwordDialog = PasswordDialog.newInstance(
            isForDecryption = false
        ) { password ->
            performRealEncryption(inputText, password)
        }

        passwordDialog.show(parentFragmentManager, "password_dialog")
    }

    /**
     * Phase 2: Performs real AES-GCM encryption using EncryptionRepository.
     * Now returns user-friendly unified format instead of separate components.
     */
    private fun performRealEncryption(plaintext: String, password: String) {
        // Show progress
        binding.progressIndicator.visibility = View.VISIBLE
        binding.btnEncrypt.isEnabled = false

        lifecycleScope.launch {
            try {
                when (val result = app.encryptionRepository.encryptAndSave(plaintext, password)) {
                    is RepositoryResult.Success -> {
                        // Show the unified format result
                        binding.tvEncryptedResult.text = formatUnifiedDataForDisplay(
                            result.data.unifiedFormat,
                            result.data.preview
                        )
                        binding.cardResult.visibility = View.VISIBLE

                        showSuccessMessage("Text encrypted and saved successfully!")
                    }

                    is RepositoryResult.Error -> {
                        showErrorMessage("Encryption failed: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                showErrorMessage("Encryption failed: ${e.message}")
            } finally {
                // Hide progress
                binding.progressIndicator.visibility = View.GONE
                binding.btnEncrypt.isEnabled = true
            }
        }
    }

    /**
     * Phase 2: Formats unified encrypted data for user-friendly display.
     * Shows the actual unified format that can be copied or used in QR codes.
     */
    private fun formatUnifiedDataForDisplay(unifiedFormat: String, preview: String): String {
        return buildString {
            appendLine("🔒 ENCRYPTED DATA")
            appendLine("Original: ${preview}")
            appendLine()
            appendLine("📋 ENCRYPTED FORMAT (Copy this):")
            appendLine("════════════════════════════════")
            appendLine(unifiedFormat)
            appendLine("════════════════════════════════")
            appendLine()
            appendLine("✅ This format can be:")
            appendLine("• Copied and pasted for decryption")
            appendLine("• Converted to QR code (Phase 4)")
            appendLine("• Shared safely (password protected)")
            appendLine()
            appendLine("🛡️ Security: AES-256-GCM encryption")
            appendLine("💾 Status: Saved to local storage")
        }
    }

    /**
     * Clears all input and result fields.
     */
    private fun clearInput() {
        binding.etInputText.text?.clear()
        binding.cardResult.visibility = View.GONE
        binding.textInputLayout.error = null
    }

    /**
     * Generates QR code from encrypted data.
     * TODO Phase 4: Implement real QR code generation
     */
    private fun generateQRCode() {
        val encryptedData = binding.tvEncryptedResult.text.toString()

        if (encryptedData.isEmpty()) {
            showErrorMessage("No encrypted data to convert to QR code")
            return
        }

        // TODO Phase 4: Navigate to QR code display screen
        showFeatureComingSoon("QR Code Generation - Coming in Phase 4")
    }

    /**
     * Shows success message to user.
     */
    private fun showSuccessMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    /**
     * Shows error message to user.
     */
    private fun showErrorMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG)
            .show()
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