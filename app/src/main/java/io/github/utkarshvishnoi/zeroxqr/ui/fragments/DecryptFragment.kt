package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentDecryptBinding
import io.github.utkarshvishnoi.zeroxqr.repository.RepositoryResult
import io.github.utkarshvishnoi.zeroxqr.ui.dialogs.PasswordDialog
import kotlinx.coroutines.launch

/**
 * Fragment for text decryption functionality.
 *
 * Phase 1: Complete UI with mock decryption functionality.
 * Phase 2: Real AES-GCM decryption with password-based key derivation.
 * Phase 3: Will enhance with FIDO2 hardware-backed security.
 */
class DecryptFragment : Fragment() {

    private var _binding: FragmentDecryptBinding? = null
    private val binding get() = _binding!!

    private lateinit var app: ZeroXQRApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDecryptBinding.inflate(inflater, container, false)
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
        binding.btnDecrypt.setOnClickListener {
            initiateDecryption()
        }

        binding.btnClear.setOnClickListener {
            clearInput()
        }

        binding.btnScanQr.setOnClickListener {
            scanQRCode()
        }

        binding.btnCopyResult.setOnClickListener {
            copyResultToClipboard()
        }
    }

    /**
     * Phase 2: Initiates real decryption process with password dialog.
     * Now uses simplified unified format - much more user-friendly!
     */
    private fun initiateDecryption() {
        val inputText = binding.etEncryptedText.text.toString().trim()

        if (inputText.isEmpty()) {
            binding.textInputLayout.error = "Please enter encrypted data to decrypt"
            return
        }

        binding.textInputLayout.error = null

        // Show password dialog for decryption
        val passwordDialog = PasswordDialog.newInstance(
            isForDecryption = true
        ) { password ->
            performRealDecryption(inputText, password)
        }

        passwordDialog.show(parentFragmentManager, "password_dialog")
    }

    /**
     * Phase 2: Performs real AES-GCM decryption using unified format.
     * Much simpler than the old multi-component approach!
     */
    private fun performRealDecryption(unifiedData: String, password: String) {
        // Show progress
        binding.progressIndicator.visibility = View.VISIBLE
        binding.btnDecrypt.isEnabled = false

        lifecycleScope.launch {
            try {
                when (val result = app.encryptionRepository.decryptFromUnified(
                    unifiedData = unifiedData,
                    password = password,
                    saveToHistory = true
                )) {
                    is RepositoryResult.Success -> {
                        // Show results
                        binding.tvDecryptedResult.text = result.data
                        binding.cardResult.visibility = View.VISIBLE

                        showSuccessMessage("Text decrypted successfully!")
                    }

                    is RepositoryResult.Error -> {
                        showErrorMessage("Decryption failed: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                showErrorMessage("Decryption failed: ${e.message}")
            } finally {
                // Hide progress
                binding.progressIndicator.visibility = View.GONE
                binding.btnDecrypt.isEnabled = true
            }
        }
    }

    /**
     * Clears all input and result fields.
     */
    private fun clearInput() {
        binding.etEncryptedText.text?.clear()
        binding.cardResult.visibility = View.GONE
        binding.textInputLayout.error = null
    }

    /**
     * Initiates QR code scanning for encrypted data input.
     * TODO Phase 4: Implement real QR code scanning
     */
    private fun scanQRCode() {
        // TODO Phase 4: Open QR scanner and populate input field
        showFeatureComingSoon("QR Code Scanner - Coming in Phase 4")
    }

    /**
     * Copies the decrypted result to clipboard.
     */
    private fun copyResultToClipboard() {
        val decryptedText = binding.tvDecryptedResult.text.toString()

        if (decryptedText.isEmpty()) {
            showErrorMessage("No decrypted text to copy")
            return
        }

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Decrypted Text", decryptedText)
        clipboard.setPrimaryClip(clip)

        showSuccessMessage(getString(R.string.msg_copied_to_clipboard))
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