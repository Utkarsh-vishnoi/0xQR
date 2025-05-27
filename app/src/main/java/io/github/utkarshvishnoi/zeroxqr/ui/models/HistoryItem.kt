package io.github.utkarshvishnoi.zeroxqr.ui.models

/**
 * Data model for encryption/decryption history items.
 *
 * Phase 1: Simple data class for mock functionality.
 * Phase 2: Will be replaced with Room entity.
 */
data class HistoryItem(
    val id: Long,
    val operationType: OperationType,
    val preview: String,
    val timestamp: Long,
    val fullData: String = "", // Will be used in Phase 2 for storing actual encrypted/decrypted content
) {
    enum class OperationType {
        ENCRYPT,
        DECRYPT
    }
}