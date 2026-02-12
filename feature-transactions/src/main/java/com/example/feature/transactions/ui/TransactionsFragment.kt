package com.example.feature.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feature.transactions.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionsFragment : Fragment() {

    private val viewModel: TransactionsViewModel by viewModel()
    private val adapter = TransactionsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_transactions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = view.findViewById<RecyclerView>(R.id.transactionList)
        val summary = view.findViewById<TextView>(R.id.monthSummary)
        val addFab = view.findViewById<FloatingActionButton>(R.id.addTransactionFab)

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                val summaryText = "Income: ${state.summary.totalIncome} | Expense: ${state.summary.totalExpense} | Balance: ${state.summary.balance}"
                summary.text = summaryText
                adapter.submitList(state.transactions)
            }
        }

        viewModel.transactionsLiveData.observe(viewLifecycleOwner) {
            // LiveData is intentionally exposed for legacy-compatible observers.
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        addFab.setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_transaction, null)
        val description = dialogView.findViewById<EditText>(R.id.inputDescription)
        val amount = dialogView.findViewById<EditText>(R.id.inputAmount)
        val incomeCheckbox = dialogView.findViewById<CheckBox>(R.id.inputIncome)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_transaction)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                viewModel.addTransaction(
                    description = description.text.toString(),
                    amount = amount.text.toString(),
                    isIncome = incomeCheckbox.isChecked
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
