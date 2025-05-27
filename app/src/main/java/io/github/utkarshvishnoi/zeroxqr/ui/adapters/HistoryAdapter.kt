package io.github.utkarshvishnoi.zeroxqr.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.databinding.ItemHistoryBinding
import io.github.utkarshvishnoi.zeroxqr.ui.models.HistoryItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for displaying encryption/decryption history.
 *
 * Phase 1: Complete implementation with mock data support.
 * Phase 2: Will integrate with Room database entities.
 */
class HistoryAdapter(
    private val onDeleteClick: (HistoryItem) -> Unit
) : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(historyItem: HistoryItem) {
            with(binding) {
                // Set operation icon and type text
                when (historyItem.operationType) {
                    HistoryItem.OperationType.ENCRYPT -> {
                        ivOperationIcon.setImageResource(R.drawable.ic_lock)
                        tvOperationType.text = "Encrypted Text"
                    }
                    HistoryItem.OperationType.DECRYPT -> {
                        ivOperationIcon.setImageResource(R.drawable.ic_lock_open)
                        tvOperationType.text = "Decrypted Text"
                    }
                }

                // Set preview text
                tvPreview.text = historyItem.preview

                // Set formatted timestamp
                tvTimestamp.text = formatTimestamp(historyItem.timestamp)

                // Set delete click listener
                btnDeleteItem.setOnClickListener {
                    onDeleteClick(historyItem)
                }

                // Set item click listener for future expansion
                root.setOnClickListener {
                    // TODO Phase 2: Navigate to detail view or copy to clipboard
                }
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < 60 * 1000 -> "Just now"
                diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
                diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
                diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
                else -> {
                    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                    dateFormat.format(Date(timestamp))
                }
            }
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}