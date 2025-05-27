package io.github.utkarshvishnoi.zeroxqr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.FragmentHistoryBinding
import io.github.utkarshvishnoi.zeroxqr.repository.RepositoryResult
import io.github.utkarshvishnoi.zeroxqr.ui.adapters.HistoryAdapter
import io.github.utkarshvishnoi.zeroxqr.ui.models.HistoryItem
import kotlinx.coroutines.launch

/**
 * Fragment for displaying encryption/decryption history.
 *
 * Phase 1: Complete UI with mock history data.
 * Phase 2: Real database integration with Room persistence.
 */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var app: ZeroXQRApplication
    private lateinit var historyAdapter: HistoryAdapter

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

        // Get application instance for repository access
        app = requireActivity().application as ZeroXQRApplication

        setupRecyclerView()
        setupClickListeners()
        observeHistoryData()
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
     * Phase 2: Observes real history data from Room database.
     * Replaces loadMockData from Phase 1.
     */
    private fun observeHistoryData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                app.encryptionRepository.getEncryptionHistory().collect { historyItems ->
                    updateUI(historyItems)
                }
            }
        }
    }

    /**
     * Updates the UI based on whether history items exist.
     */
    private fun updateUI(historyItems: List<HistoryItem>) {
        if (historyItems.isEmpty()) {
            binding.rvHistory.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            historyAdapter.submitList(historyItems)
        }
    }

    /**
     * Phase 2: Deletes a specific history item from database.
     * Replaces mock deletion from Phase 1.
     */
    private fun deleteHistoryItem(historyItem: HistoryItem) {
        lifecycleScope.launch {
            try {
                when (val result = app.encryptionRepository.deleteHistoryItem(historyItem.id)) {
                    is RepositoryResult.Success -> {
                        showSuccessMessage("Item deleted successfully")
                    }

                    is RepositoryResult.Error -> {
                        showErrorMessage("Failed to delete item: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                showErrorMessage("Failed to delete item: ${e.message}")
            }
        }
    }

    /**
     * Phase 2: Deletes all history items from database.
     * Replaces mock bulk deletion from Phase 1.
     */
    private fun deleteAllHistory() {
        lifecycleScope.launch {
            try {
                // Check if there are items to delete
                val count = app.encryptionRepository.getHistoryCount()
                if (count == 0) {
                    showErrorMessage("No items to delete")
                    return@launch
                }

                when (val result = app.encryptionRepository.deleteAllHistory()) {
                    is RepositoryResult.Success -> {
                        showSuccessMessage("All history deleted successfully")
                    }

                    is RepositoryResult.Error -> {
                        showErrorMessage("Failed to delete all history: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                showErrorMessage("Failed to delete all history: ${e.message}")
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}