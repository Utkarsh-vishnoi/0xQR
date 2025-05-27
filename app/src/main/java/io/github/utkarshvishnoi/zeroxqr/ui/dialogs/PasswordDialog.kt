package io.github.utkarshvishnoi.zeroxqr.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.databinding.DialogPasswordBinding
import io.github.utkarshvishnoi.zeroxqr.security.PasswordStrength
import io.github.utkarshvishnoi.zeroxqr.security.PasswordStrengthAnalyzer

/**
 * Dialog for password input during encryption/decryption operations.
 *
 * Phase 2: Enhanced with real-time password strength analysis and visual feedback.
 * Phase 3: Will be replaced with FIDO2 biometric authentication.
 */
class PasswordDialog : DialogFragment() {

    private var _binding: DialogPasswordBinding? = null
    private val binding get() = _binding!!

    private var onPasswordEntered: ((String) -> Unit)? = null
    private var isForDecryption: Boolean = false
    private lateinit var passwordAnalyzer: PasswordStrengthAnalyzer
    private var alertDialog: AlertDialog? = null

    companion object {
        private const val ARG_IS_FOR_DECRYPTION = "is_for_decryption"

        fun newInstance(
            isForDecryption: Boolean = false,
            onPasswordEntered: (String) -> Unit
        ): PasswordDialog {
            return PasswordDialog().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_FOR_DECRYPTION, isForDecryption)
                }
                this.onPasswordEntered = onPasswordEntered
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isForDecryption = arguments?.getBoolean(ARG_IS_FOR_DECRYPTION) ?: false
        passwordAnalyzer = PasswordStrengthAnalyzer()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPasswordBinding.inflate(LayoutInflater.from(requireContext()))

        setupUI()
        setupPasswordStrengthAnalysis()

        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(if (isForDecryption) "Enter Decryption Password" else "Create Encryption Password")
            .setView(binding.root)
            .setPositiveButton("Continue") { _, _ ->
                val password = binding.etPassword.text.toString()
                onPasswordEntered?.invoke(password)
            }
            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
            .create()

        // Initially disable continue button for encryption
        if (!isForDecryption) {
            alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
        }

        return alertDialog!!
    }

    private fun setupUI() {
        binding.tvMessage.text = if (isForDecryption) {
            "Enter the password used to encrypt this data.\n\nPhase 3 will replace this with biometric authentication."
        } else {
            "Create a strong password for encryption.\nYou'll need this password for decryption.\n\nPhase 3 will replace this with biometric authentication."
        }

        // Auto-focus on password field
        binding.etPassword.requestFocus()

        // Show/hide security indicator based on operation type
        if (isForDecryption) {
            binding.securityIndicator.setVisible(false)
        } else {
            binding.securityIndicator.setVisible(true)
        }
    }

    /**
     * Sets up real-time password strength analysis for encryption.
     */
    private fun setupPasswordStrengthAnalysis() {
        if (isForDecryption) return // Skip analysis for decryption

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                analyzePasswordStrength(password)
            }
        })
    }

    /**
     * Analyzes password strength and updates UI indicators.
     */
    private fun analyzePasswordStrength(password: String) {
        val result = passwordAnalyzer.analyzePassword(password)

        // Update security indicator
        binding.securityIndicator.updateSecurityIndicator(result)

        // Enable/disable continue button based on minimum security requirements
        val minSecurityMet = result.strength != PasswordStrength.VERY_WEAK && password.length >= 8
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = minSecurityMet

        // Update input field styling based on strength
        updatePasswordFieldStyling(result.strength)
    }

    /**
     * Updates password field styling to reflect strength.
     */
    private fun updatePasswordFieldStyling(strength: PasswordStrength) {
        val colorRes = when (strength) {
            PasswordStrength.VERY_WEAK -> R.color.strength_very_weak
            PasswordStrength.WEAK -> R.color.strength_weak
            PasswordStrength.FAIR -> R.color.strength_fair
            PasswordStrength.GOOD -> R.color.strength_good
            PasswordStrength.STRONG -> R.color.strength_strong
            PasswordStrength.VERY_STRONG -> R.color.strength_very_strong
        }

        binding.textInputLayout.boxStrokeColor =
            androidx.core.content.ContextCompat.getColor(requireContext(), colorRes)
    }

    override fun onStart() {
        super.onStart()
        // Resize dialog to accommodate security indicator
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}