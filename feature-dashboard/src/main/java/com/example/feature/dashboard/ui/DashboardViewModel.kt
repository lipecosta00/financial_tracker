package com.example.feature.dashboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.DashboardInsights
import com.example.domain.model.MonthlySummary
import com.example.domain.model.TransactionFilters
import com.example.domain.usecase.CalculateDashboardInsightsUseCase
import com.example.domain.usecase.ObserveTransactionsUseCase
import com.example.domain.usecase.RefreshTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.YearMonth

data class DashboardUiState(
    val isLoading: Boolean = true,
    val selectedMonth: YearMonth = YearMonth.now(),
    val insights: DashboardInsights? = null,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val observeTransactions: ObserveTransactionsUseCase,
    private val refreshTransactions: RefreshTransactionsUseCase,
    private val calculateDashboardInsights: CalculateDashboardInsightsUseCase
) : ViewModel() {

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    private val _state = MutableStateFlow(
        DashboardUiState(
            isLoading = true,
            selectedMonth = selectedMonth.value,
            insights = null
        )
    )
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    init {
        observeDashboard()
        refresh()
    }

    fun previousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        selectedMonth.value = selectedMonth.value.plusMonths(1)
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching { refreshTransactions() }
                .onFailure {
                    _state.value = _state.value.copy(errorMessage = "Could not refresh dashboard data")
                }
        }
    }

    private fun observeDashboard() {
        viewModelScope.launch {
            combine(
                observeTransactions(TransactionFilters(month = null)),
                selectedMonth
            ) { transactions, month ->
                calculateDashboardInsights(transactions, month)
            }.collectLatest { insights ->
                _state.value = DashboardUiState(
                    isLoading = false,
                    selectedMonth = insights.selectedMonth,
                    insights = insights,
                    errorMessage = null
                )
            }
        }
    }
}
