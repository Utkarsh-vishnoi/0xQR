package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentHistoryBinding
import io.github.utkarshvishnoi.zeroxqr.ui.adapters.HistoryAdapter
import io.github.utkarshvishnoi.zeroxqr.ui.models.HistoryItem

/**
 * Fragment for displaying encryption/decryption history.
 *
 * Phase 1: Complete UI with mock history data.
 * Phase 2: Will integrate with Room database for real persistence.
 */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter
    private val mockHistoryItems = mutableListOf<HistoryItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadMockData()
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { historyItem ->
            deleteHistoryItem(historyItem)
        }

        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Sets up click listeners for UI elements.
     */
    private fun setupClickListeners() {
        binding.btnDeleteAll.setOnClickListener {
            deleteAllHistory()
        }
    }

    /**
     * Loads mock history data for Phase 1 demonstration.
     * TODO Phase 2: Replace with real database queries
     */
    private fun loadMockData() {
        mockHistoryItems.addAll(
            listOf(
                HistoryItem(
                    id = 1,
                    operationType = HistoryItem.OperationType.ENCRYPT,
                    preview = "This is a sample encrypted message...",
                    timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000 // 2 hours ago
                ),
                HistoryItem(
                    id = 2,
                    operationType = HistoryItem.OperationType.DECRYPT,
                    preview = "Successfully decrypted confidential data...",
                    timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000 // 1 day ago
                ),
                HistoryItem(
                    id = 3,
                    operationType = HistoryItem.OperationType.ENCRYPT,
                    preview = "Another encrypted message for testing...",
                    timestamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 // 3 days ago
                )
            )
        )

        updateUI()
    }

    /**
     * Updates the UI based on whether history items exist.
     */
    private fun updateUI() {
        if (mockHistoryItems.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            historyAdapter.submitList(mockHistoryItems.toList())
        }
    }

    /**
     * Deletes a specific history item.
     * TODO Phase 2: Implement real database deletion
     */
    private fun deleteHistoryItem(historyItem: HistoryItem) {
        mockHistoryItems.remove(historyItem)
        updateUI()
        showSuccessMessage("Item deleted")
    }

    /**
     * Deletes all history items.
     * TODO Phase 2: Implement real database bulk deletion
     */
    private fun deleteAllHistory() {
        if (mockHistoryItems.isEmpty()) {
            showErrorMessage("No items to delete")
            return
        }

        mockHistoryItems.clear()
        updateUI()
        showSuccessMessage("All history deleted")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}