package com.example.feature.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.TransactionType
import com.example.feature.transactions.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionsFragment : Fragment() {

    private val viewModel: TransactionsViewModel by viewModel()
    private val adapter = TransactionsAdapter(
        onItemClick = { showEditDialog(it) },
        onItemLongClick = { showDeleteDialog(it) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_transactions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = view.findViewById<RecyclerView>(R.id.transactionList)
        val summary = view.findViewById<TextView>(R.id.monthSummary)
        val filterMonth = view.findViewById<TextView>(R.id.filterMonthLabel)
        val btnAll = view.findViewById<TextView>(R.id.filterAll)
        val btnIncome = view.findViewById<TextView>(R.id.filterIncome)
        val btnExpense = view.findViewById<TextView>(R.id.filterExpense)
        val prevMonth = view.findViewById<ImageButton>(R.id.prevMonth)
        val nextMonth = view.findViewById<ImageButton>(R.id.nextMonth)
        val clearMonth = view.findViewById<ImageButton>(R.id.clearMonth)
        val search = view.findViewById<EditText>(R.id.searchInput)
        val addFab = view.findViewById<FloatingActionButton>(R.id.addTransactionFab)

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                val summaryText = "Income: ${state.summary.totalIncome} | Expense: ${state.summary.totalExpense} | Balance: ${state.summary.balance}"
                summary.text = summaryText
                filterMonth.text = state.filters.month?.toString() ?: getString(R.string.all_months)
                btnAll.alpha = if (state.filters.type == null) 1.0f else 0.5f
                btnIncome.alpha = if (state.filters.type == TransactionType.INCOME) 1.0f else 0.5f
                btnExpense.alpha = if (state.filters.type == TransactionType.EXPENSE) 1.0f else 0.5f
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

        search.doAfterTextChanged { viewModel.onSearchChanged(it?.toString().orEmpty()) }
        btnAll.setOnClickListener { viewModel.onTypeFilterChanged(null) }
        btnIncome.setOnClickListener { viewModel.onTypeFilterChanged(TransactionType.INCOME) }
        btnExpense.setOnClickListener { viewModel.onTypeFilterChanged(TransactionType.EXPENSE) }
        prevMonth.setOnClickListener { viewModel.onMonthPrevious() }
        nextMonth.setOnClickListener { viewModel.onMonthNext() }
        clearMonth.setOnClickListener { viewModel.onMonthClear() }
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

    private fun showEditDialog(item: com.example.domain.model.FinancialTransaction) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_transaction, null)
        val description = dialogView.findViewById<EditText>(R.id.inputDescription)
        val amount = dialogView.findViewById<EditText>(R.id.inputAmount)
        val incomeCheckbox = dialogView.findViewById<CheckBox>(R.id.inputIncome)

        description.setText(item.description)
        amount.setText(item.amount.toPlainString())
        incomeCheckbox.isChecked = item.type == com.example.domain.model.TransactionType.INCOME

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_transaction)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                viewModel.editTransaction(
                    item = item,
                    description = description.text.toString(),
                    amount = amount.text.toString(),
                    isIncome = incomeCheckbox.isChecked
                )
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteDialog(item: com.example.domain.model.FinancialTransaction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_transaction)
            .setMessage(getString(R.string.delete_transaction_confirmation, item.description))
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteTransaction(item)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
