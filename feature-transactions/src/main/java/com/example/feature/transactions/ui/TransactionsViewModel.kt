package com.example.feature.transactions.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CreateTransactionCommand
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.MonthlySummary
import com.example.domain.model.TransactionFilters
import com.example.domain.model.TransactionType
import com.example.domain.model.UpdateTransactionCommand
import com.example.domain.usecase.AddTransactionUseCase
import com.example.domain.usecase.CalculateMonthlySummaryUseCase
import com.example.domain.usecase.DeleteTransactionUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import com.example.domain.usecase.UpdateTransactionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.YearMonth

data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<FinancialTransaction> = emptyList(),
    val summary: MonthlySummary = MonthlySummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
    val filters: TransactionFilters = TransactionFilters()
)

class TransactionsViewModel(
    private val observeTransactions: ObserveTransactionsUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    private val addTransaction: AddTransactionUseCase,
    private val updateTransaction: UpdateTransactionUseCase,
    private val deleteTransaction: DeleteTransactionUseCase,
    private val calculateSummary: CalculateMonthlySummaryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionUiState(isLoading = true))
    val state: StateFlow<TransactionUiState> = _state.asStateFlow()
    private val filters = MutableStateFlow(TransactionFilters())

    val transactionsLiveData: LiveData<List<FinancialTransaction>> by lazy {
        state.map { it.transactions }.asLiveData()
    }

    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events.asSharedFlow()

    init {
        observeState()
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { refreshTransactions() }
                .onFailure { _events.emit("Failed to refresh transactions") }
        }
    }

    fun addTransaction(description: String, amount: String, isIncome: Boolean) {
        viewModelScope.launch {
            val parsedAmount = amount.toBigDecimalOrNull()
            if (description.isBlank() || parsedAmount == null || parsedAmount <= BigDecimal.ZERO) {
                _events.emit("Invalid input data")
                return@launch
            }

            runCatching {
                addTransaction(
                    CreateTransactionCommand(
                        description = description,
                        amount = parsedAmount,
                        type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
                    )
                )
            }.onSuccess {
                _events.emit("Transaction added")
            }.onFailure {
                _events.emit("Could not save transaction")
            }
        }
    }

    fun editTransaction(item: FinancialTransaction, description: String, amount: String, isIncome: Boolean) {
        viewModelScope.launch {
            val parsedAmount = amount.toBigDecimalOrNull()
            if (description.isBlank() || parsedAmount == null || parsedAmount <= BigDecimal.ZERO) {
                _events.emit("Invalid input data")
                return@launch
            }

            runCatching {
                updateTransaction(
                    UpdateTransactionCommand(
                        id = item.id,
                        description = description,
                        amount = parsedAmount,
                        type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                        createdAt = item.createdAt
                    )
                )
            }.onSuccess {
                _events.emit("Transaction updated")
            }.onFailure {
                _events.emit("Could not update transaction")
            }
        }
    }

    fun deleteTransaction(item: FinancialTransaction) {
        viewModelScope.launch {
            runCatching {
                deleteTransaction(item.id)
            }.onSuccess {
                _events.emit("Transaction deleted")
            }.onFailure {
                _events.emit("Could not delete transaction")
            }
        }
    }

    private fun observeState() {
        viewModelScope.launch {
            filters
                .flatMapLatest { observeTransactions(it) }
                .collectLatest { items ->
                _state.value = TransactionUiState(
                    isLoading = false,
                    transactions = items,
                    summary = calculateSummary(items),
                    filters = filters.value
                )
            }
        }
    }

    fun onSearchChanged(query: String) {
        filters.value = filters.value.copy(query = query)
    }

    fun onTypeFilterChanged(type: TransactionType?) {
        filters.value = filters.value.copy(type = type)
    }

    fun onMonthPrevious() {
        val currentMonth = filters.value.month ?: YearMonth.now()
        filters.value = filters.value.copy(month = currentMonth.minusMonths(1))
    }

    fun onMonthNext() {
        val currentMonth = filters.value.month ?: YearMonth.now()
        filters.value = filters.value.copy(month = currentMonth.plusMonths(1))
    }

    fun onMonthClear() {
        filters.value = filters.value.copy(month = null)
    }
}
