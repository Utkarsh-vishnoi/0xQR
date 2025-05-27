package io.github.utkarshvishnoi.zeroxqr.security

import kotlin.math.log2
import kotlin.math.pow

/**
 * Analyzes password strength and provides encryption security metrics.
 *
 * Phase 2: Comprehensive password strength analysis for user feedback.
 * Helps users understand the security level of their encryption.
 */
class PasswordStrengthAnalyzer {

    /**
     * Analyzes password strength and returns comprehensive metrics.
     */
    fun analyzePassword(password: String): PasswordStrengthResult {
        if (password.isEmpty()) {
            return PasswordStrengthResult(
                strength = PasswordStrength.VERY_WEAK,
                score = 0,
                entropy = 0.0,
                timeToCrack = "Instant",
                recommendations = listOf("Password cannot be empty"),
                securityLevel = SecurityLevel.INSECURE
            )
        }

        val metrics = calculatePasswordMetrics(password)
        val entropy = calculateEntropy(password, metrics)
        val strength = determineStrength(password, entropy, metrics)
        val timeToCrack = estimateTimeToCrack(entropy)
        val recommendations = generateRecommendations(password, metrics, strength)
        val securityLevel = determineSecurityLevel(strength, entropy)

        return PasswordStrengthResult(
            strength = strength,
            score = calculateScore(strength),
            entropy = entropy,
            timeToCrack = timeToCrack,
            recommendations = recommendations,
            securityLevel = securityLevel
        )
    }

    /**
     * Calculates various password metrics for analysis.
     */
    private fun calculatePasswordMetrics(password: String): PasswordMetrics {
        val length = password.length
        val hasLowercase = password.any { it.isLowerCase() }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigits = password.any { it.isDigit() }
        val hasSpecialChars = password.any { !it.isLetterOrDigit() }
        val uniqueChars = password.toSet().size
        val repeatedChars = length - uniqueChars
        val commonPatterns = detectCommonPatterns(password)

        return PasswordMetrics(
            length = length,
            hasLowercase = hasLowercase,
            hasUppercase = hasUppercase,
            hasDigits = hasDigits,
            hasSpecialChars = hasSpecialChars,
            uniqueChars = uniqueChars,
            repeatedChars = repeatedChars,
            commonPatterns = commonPatterns
        )
    }

    /**
     * Calculates password entropy (randomness) in bits.
     */
    private fun calculateEntropy(password: String, metrics: PasswordMetrics): Double {
        var charsetSize = 0

        if (metrics.hasLowercase) charsetSize += 26
        if (metrics.hasUppercase) charsetSize += 26
        if (metrics.hasDigits) charsetSize += 10
        if (metrics.hasSpecialChars) charsetSize += 32 // Common special characters

        if (charsetSize == 0) charsetSize = 1 // Avoid log(0)

        val baseEntropy = metrics.length * log2(charsetSize.toDouble())

        // Reduce entropy for repeated characters and patterns
        val repetitionPenalty = (metrics.repeatedChars.toDouble() / metrics.length) * 0.5
        val patternPenalty = metrics.commonPatterns.size * 0.1

        return maxOf(0.0, baseEntropy - (baseEntropy * (repetitionPenalty + patternPenalty)))
    }

    /**
     * Determines overall password strength based on multiple factors.
     */
    private fun determineStrength(
        password: String,
        entropy: Double,
        metrics: PasswordMetrics
    ): PasswordStrength {
        return when {
            password.length < 8 -> PasswordStrength.VERY_WEAK
            entropy < 30 -> PasswordStrength.WEAK
            entropy < 50 -> PasswordStrength.FAIR
            entropy < 70 -> PasswordStrength.GOOD
            entropy < 90 -> PasswordStrength.STRONG
            else -> PasswordStrength.VERY_STRONG
        }
    }

    /**
     * Estimates time to crack password using modern computing power.
     */
    private fun estimateTimeToCrack(entropy: Double): String {
        // Assume 10^12 attempts per second (modern GPU cracking)
        val attemptsPerSecond = 1e12
        val totalCombinations = 2.0.pow(entropy)
        val averageAttempts = totalCombinations / 2
        val secondsToCrack = averageAttempts / attemptsPerSecond

        return when {
            secondsToCrack < 1 -> "Instant"
            secondsToCrack < 60 -> "${secondsToCrack.toInt()} seconds"
            secondsToCrack < 3600 -> "${(secondsToCrack / 60).toInt()} minutes"
            secondsToCrack < 86400 -> "${(secondsToCrack / 3600).toInt()} hours"
            secondsToCrack < 31536000 -> "${(secondsToCrack / 86400).toInt()} days"
            secondsToCrack < 31536000000 -> "${(secondsToCrack / 31536000).toInt()} years"
            else -> "Centuries+"
        }
    }

    /**
     * Generates specific recommendations for password improvement.
     */
    private fun generateRecommendations(
        password: String,
        metrics: PasswordMetrics,
        strength: PasswordStrength
    ): List<String> {
        val recommendations = mutableListOf<String>()

        if (metrics.length < 12) {
            recommendations.add("Use at least 12 characters (current: ${metrics.length})")
        }

        if (!metrics.hasLowercase) {
            recommendations.add("Add lowercase letters (a-z)")
        }

        if (!metrics.hasUppercase) {
            recommendations.add("Add uppercase letters (A-Z)")
        }

        if (!metrics.hasDigits) {
            recommendations.add("Add numbers (0-9)")
        }

        if (!metrics.hasSpecialChars) {
            recommendations.add("Add special characters (!@#$%^&*)")
        }

        if (metrics.repeatedChars > metrics.length * 0.3) {
            recommendations.add("Reduce repeated characters")
        }

        if (metrics.commonPatterns.isNotEmpty()) {
            recommendations.add("Avoid common patterns: ${metrics.commonPatterns.joinToString(", ")}")
        }

        if (strength == PasswordStrength.VERY_STRONG) {
            recommendations.add("Excellent! This password provides strong security.")
        }

        return recommendations
    }

    /**
     * Determines overall encryption security level.
     */
    private fun determineSecurityLevel(strength: PasswordStrength, entropy: Double): SecurityLevel {
        return when {
            strength == PasswordStrength.VERY_WEAK || entropy < 25 -> SecurityLevel.INSECURE
            strength == PasswordStrength.WEAK || entropy < 40 -> SecurityLevel.LOW
            strength == PasswordStrength.FAIR || entropy < 60 -> SecurityLevel.MEDIUM
            strength == PasswordStrength.GOOD || entropy < 80 -> SecurityLevel.HIGH
            else -> SecurityLevel.VERY_HIGH
        }
    }

    /**
     * Converts strength enum to numerical score (0-100).
     */
    private fun calculateScore(strength: PasswordStrength): Int {
        return when (strength) {
            PasswordStrength.VERY_WEAK -> 10
            PasswordStrength.WEAK -> 25
            PasswordStrength.FAIR -> 50
            PasswordStrength.GOOD -> 75
            PasswordStrength.STRONG -> 90
            PasswordStrength.VERY_STRONG -> 100
        }
    }

    /**
     * Detects common password patterns that reduce security.
     */
    private fun detectCommonPatterns(password: String): List<String> {
        val patterns = mutableListOf<String>()
        val lower = password.lowercase()

        // Sequential characters
        if (containsSequentialChars(password)) {
            patterns.add("sequential chars")
        }

        // Repeated sequences
        if (containsRepeatedSequences(password)) {
            patterns.add("repeated sequences")
        }

        // Common words (simplified check)
        val commonWords = listOf("password", "123456", "qwerty", "admin", "login")
        for (word in commonWords) {
            if (lower.contains(word)) {
                patterns.add("common word: $word")
            }
        }

        // Keyboard patterns
        if (containsKeyboardPattern(lower)) {
            patterns.add("keyboard pattern")
        }

        return patterns
    }

    private fun containsSequentialChars(password: String): Boolean {
        for (i in 0 until password.length - 2) {
            val char1 = password[i].code
            val char2 = password[i + 1].code
            val char3 = password[i + 2].code

            if (char2 == char1 + 1 && char3 == char2 + 1) {
                return true
            }
        }
        return false
    }

    private fun containsRepeatedSequences(password: String): Boolean {
        for (length in 2..4) {
            for (i in 0..password.length - length * 2) {
                val sequence = password.substring(i, i + length)
                val nextSequence =
                    password.substring(i + length, minOf(i + length * 2, password.length))
                if (sequence == nextSequence) {
                    return true
                }
            }
        }
        return false
    }

    private fun containsKeyboardPattern(password: String): Boolean {
        val keyboardRows = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm", "1234567890")

        for (row in keyboardRows) {
            for (i in 0..row.length - 3) {
                val pattern = row.substring(i, i + 3)
                if (password.contains(pattern) || password.contains(pattern.reversed())) {
                    return true
                }
            }
        }
        return false
    }
}

/**
 * Data classes for password strength analysis results.
 */
data class PasswordStrengthResult(
    val strength: PasswordStrength,
    val score: Int, // 0-100
    val entropy: Double, // bits
    val timeToCrack: String,
    val recommendations: List<String>,
    val securityLevel: SecurityLevel
)

data class PasswordMetrics(
    val length: Int,
    val hasLowercase: Boolean,
    val hasUppercase: Boolean,
    val hasDigits: Boolean,
    val hasSpecialChars: Boolean,
    val uniqueChars: Int,
    val repeatedChars: Int,
    val commonPatterns: List<String>
)

enum class PasswordStrength {
    VERY_WEAK,
    WEAK,
    FAIR,
    GOOD,
    STRONG,
    VERY_STRONG
}

enum class SecurityLevel {
    INSECURE,
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH
}