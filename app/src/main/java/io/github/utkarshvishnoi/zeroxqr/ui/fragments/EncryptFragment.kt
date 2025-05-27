package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentEncryptBinding

/**
 * Fragment for text encryption functionality.
 *
 * Phase 1: Complete UI with mock encryption functionality.
 * Phase 2: Will implement real AES-GCM encryption.
 */
class EncryptFragment : Fragment() {

    private var _binding: FragmentEncryptBinding? = null
    private val binding get() = _binding!!

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

        setupClickListeners()
    }

    /**
     * Sets up click listeners for all interactive elements.
     */
    private fun setupClickListeners() {
        binding.btnEncrypt.setOnClickListener {
            performMockEncryption()
        }

        binding.btnClear.setOnClickListener {
            clearInput()
        }

        binding.btnGenerateQr.setOnClickListener {
            generateQRCode()
        }

        binding.btnSave.setOnClickListener {
            saveEncryptedData()
        }
    }

    /**
     * Performs mock encryption with simulated processing time.
     * TODO Phase 2: Replace with real AES-GCM encryption
     */
    private fun performMockEncryption() {
        val inputText = binding.etInputText.text.toString().trim()

        if (inputText.isEmpty()) {
            binding.textInputLayout.error = "Please enter text to encrypt"
            return
        }

        binding.textInputLayout.error = null

        // Show progress
        binding.progressIndicator.visibility = View.VISIBLE
        binding.btnEncrypt.isEnabled = false

        lifecycleScope.launch {
            // Simulate encryption processing
            delay(1000)

            // Generate mock encrypted data
            val mockEncryptedData = generateMockEncryptedData(inputText)

            // Show results
            binding.tvEncryptedResult.text = mockEncryptedData
            binding.cardResult.visibility = View.VISIBLE

            // Hide progress
            binding.progressIndicator.visibility = View.GONE
            binding.btnEncrypt.isEnabled = true

            showSuccessMessage("Text encrypted successfully (mock)")
        }
    }

    /**
     * Generates mock encrypted data for Phase 1 demonstration.
     * TODO Phase 2: Replace with real encryption
     */
    private fun generateMockEncryptedData(inputText: String): String {
        val timestamp = System.currentTimeMillis()
        return "MOCK_AES256_GCM_ENCRYPTED_${inputText.length}CHARS_$timestamp"
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
     * Saves encrypted data to local storage.
     * TODO Phase 2: Implement with Room database
     */
    private fun saveEncryptedData() {
        val encryptedData = binding.tvEncryptedResult.text.toString()

        if (encryptedData.isEmpty()) {
            showErrorMessage("No encrypted data to save")
            return
        }

        // TODO Phase 2: Save to Room database
        showFeatureComingSoon("Local Storage - Coming in Phase 2")
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