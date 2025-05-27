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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentDecryptBinding

/**
 * Fragment for text decryption functionality.
 *
 * Phase 1: Complete UI with mock decryption functionality.
 * Phase 2: Will implement real AES-GCM decryption.
 */
class DecryptFragment : Fragment() {

    private var _binding: FragmentDecryptBinding? = null
    private val binding get() = _binding!!

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

        setupClickListeners()
    }

    /**
     * Sets up click listeners for all interactive elements.
     */
    private fun setupClickListeners() {
        binding.btnDecrypt.setOnClickListener {
            performMockDecryption()
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
     * Performs mock decryption with simulated processing time.
     * TODO Phase 2: Replace with real AES-GCM decryption
     */
    private fun performMockDecryption() {
        val inputText = binding.etEncryptedText.text.toString().trim()

        if (inputText.isEmpty()) {
            binding.textInputLayout.error = "Please enter encrypted text to decrypt"
            return
        }

        binding.textInputLayout.error = null

        // Show progress
        binding.progressIndicator.visibility = View.VISIBLE
        binding.btnDecrypt.isEnabled = false

        lifecycleScope.launch {
            // Simulate decryption processing
            delay(1000)

            // Generate mock decrypted data
            val mockDecryptedData = generateMockDecryptedData(inputText)

            // Show results
            binding.tvDecryptedResult.text = mockDecryptedData
            binding.cardResult.visibility = View.VISIBLE

            // Hide progress
            binding.progressIndicator.visibility = View.GONE
            binding.btnDecrypt.isEnabled = true

            showSuccessMessage("Text decrypted successfully (mock)")
        }
    }

    /**
     * Generates mock decrypted data for Phase 1 demonstration.
     * TODO Phase 2: Replace with real decryption
     */
    private fun generateMockDecryptedData(inputText: String): String {
        return if (inputText.startsWith("MOCK_AES256_GCM_ENCRYPTED")) {
            getString(R.string.mock_decrypted_data)
        } else {
            "Mock decryption: This would be the decrypted content of the provided encrypted text."
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