package io.github.utkarshvishnoi.zeroxqr.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.databinding.ViewSecurityIndicatorBinding
import io.github.utkarshvishnoi.zeroxqr.security.PasswordStrength
import io.github.utkarshvishnoi.zeroxqr.security.PasswordStrengthResult
import io.github.utkarshvishnoi.zeroxqr.security.SecurityLevel

/**
 * Custom view for displaying encryption strength indicators.
 *
 * Phase 2: Visual feedback for password strength and encryption security.
 * Provides real-time indicators and recommendations.
 */
class SecurityIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSecurityIndicatorBinding =
        ViewSecurityIndicatorBinding.inflate(LayoutInflater.from(context), this, true)

    /**
     * Updates the security indicator with password strength analysis.
     */
    fun updateSecurityIndicator(result: PasswordStrengthResult) {
        updateStrengthBar(result)
        updateSecurityBadge(result)
        updateMetrics(result)
        updateRecommendations(result)
    }

    /**
     * Updates the visual strength progress bar.
     */
    private fun updateStrengthBar(result: PasswordStrengthResult) {
        binding.progressStrength.progress = result.score

        val (color, textColor) = when (result.strength) {
            PasswordStrength.VERY_WEAK -> Pair(
                R.color.strength_very_weak,
                R.color.strength_text_weak
            )

            PasswordStrength.WEAK -> Pair(R.color.strength_weak, R.color.strength_text_weak)
            PasswordStrength.FAIR -> Pair(R.color.strength_fair, R.color.strength_text_medium)
            PasswordStrength.GOOD -> Pair(R.color.strength_good, R.color.strength_text_strong)
            PasswordStrength.STRONG -> Pair(R.color.strength_strong, R.color.strength_text_strong)
            PasswordStrength.VERY_STRONG -> Pair(
                R.color.strength_very_strong,
                R.color.strength_text_strong
            )
        }

        binding.progressStrength.progressTintList = ContextCompat.getColorStateList(context, color)

        binding.tvStrengthLabel.text = when (result.strength) {
            PasswordStrength.VERY_WEAK -> "Very Weak"
            PasswordStrength.WEAK -> "Weak"
            PasswordStrength.FAIR -> "Fair"
            PasswordStrength.GOOD -> "Good"
            PasswordStrength.STRONG -> "Strong"
            PasswordStrength.VERY_STRONG -> "Very Strong"
        }

        binding.tvStrengthLabel.setTextColor(ContextCompat.getColor(context, textColor))
    }

    /**
     * Updates the security level badge.
     */
    private fun updateSecurityBadge(result: PasswordStrengthResult) {
        val (badgeText, badgeColor, icon) = when (result.securityLevel) {
            SecurityLevel.INSECURE -> Triple("INSECURE", R.color.security_insecure, "🚨")
            SecurityLevel.LOW -> Triple("LOW SECURITY", R.color.security_low, "⚠️")
            SecurityLevel.MEDIUM -> Triple("MEDIUM SECURITY", R.color.security_medium, "🛡️")
            SecurityLevel.HIGH -> Triple("HIGH SECURITY", R.color.security_high, "🔒")
            SecurityLevel.VERY_HIGH -> Triple(
                "VERY HIGH SECURITY",
                R.color.security_very_high,
                "🛡️"
            )
        }

        binding.tvSecurityBadge.text = "$icon $badgeText"
        binding.cardSecurityBadge.setCardBackgroundColor(
            ContextCompat.getColor(
                context,
                badgeColor
            )
        )
    }

    /**
     * Updates security metrics display.
     */
    private fun updateMetrics(result: PasswordStrengthResult) {
        binding.tvEntropyValue.text = "${result.entropy.toInt()} bits"
        binding.tvTimeToCrack.text = result.timeToCrack
        binding.tvSecurityScore.text = "${result.score}/100"

        // Update encryption info
        binding.tvEncryptionInfo.text = buildString {
            appendLine("🔐 AES-256-GCM Encryption")
            appendLine("🧂 PBKDF2 Key Derivation")
            appendLine("🎲 100,000 Iterations")
            appendLine("📱 Air-gapped Security")
        }
    }

    /**
     * Updates recommendations list.
     */
    private fun updateRecommendations(result: PasswordStrengthResult) {
        if (result.recommendations.isEmpty()) {
            binding.cardRecommendations.visibility = GONE
        } else {
            binding.cardRecommendations.visibility = VISIBLE
            binding.tvRecommendations.text = result.recommendations.joinToString("\n") { "• $it" }
        }
    }

    /**
     * Shows/hides the entire security indicator.
     */
    fun setVisible(visible: Boolean) {
        visibility = if (visible) VISIBLE else GONE
    }
}