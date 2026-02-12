package com.example.domain.usecase

import com.example.core.money.MoneyFormatter
import com.example.domain.model.CategoryBreakdownItem
import com.example.domain.model.DashboardInsights
import com.example.domain.model.MonthlyTrendPoint
import com.example.domain.model.MonthlySummary
import com.example.domain.model.TopExpenseItem
import com.example.domain.model.TransactionType
import com.example.domain.repository.TransactionRepository
import java.math.BigDecimal
import java.time.YearMonth

class ObserveTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(filters: com.example.domain.model.TransactionFilters) =
        repository.observeTransactions(filters)
}

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(command: com.example.domain.model.CreateTransactionCommand) {
        repository.addTransaction(command)
    }
}

class UpdateTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(command: com.example.domain.model.UpdateTransactionCommand) {
        repository.updateTransaction(command)
    }
}

class DeleteTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: String) {
        repository.deleteTransaction(transactionId)
    }
}

class RefreshTransactionsUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke() {
        repository.refresh()
    }
}

class CalculateMonthlySummaryUseCase {
    operator fun invoke(transactions: List<com.example.domain.model.FinancialTransaction>): MonthlySummary {
        val income = transactions
            .filter { it.type == com.example.domain.model.TransactionType.INCOME }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.amount }

        val expense = transactions
            .filter { it.type == com.example.domain.model.TransactionType.EXPENSE }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.amount }

        return MonthlySummary(
            totalIncome = MoneyFormatter.normalize(income),
            totalExpense = MoneyFormatter.normalize(expense),
            balance = MoneyFormatter.normalize(income - expense)
        )
    }
}

class CalculateDashboardInsightsUseCase(
    private val calculateMonthlySummaryUseCase: CalculateMonthlySummaryUseCase
) {
    operator fun invoke(
        transactions: List<com.example.domain.model.FinancialTransaction>,
        selectedMonth: YearMonth,
        monthWindow: Int = 6
    ): DashboardInsights {
        val selectedMonthTransactions = transactions.filter {
            YearMonth.from(it.createdAt.atZone(java.time.ZoneOffset.UTC)) == selectedMonth
        }

        val summary = calculateMonthlySummaryUseCase(selectedMonthTransactions)

        val trendMonths = (monthWindow - 1 downTo 0).map { offset -> selectedMonth.minusMonths(offset.toLong()) }
        val trend = trendMonths.map { month ->
            val monthTx = transactions.filter { YearMonth.from(it.createdAt.atZone(java.time.ZoneOffset.UTC)) == month }
            val monthSummary = calculateMonthlySummaryUseCase(monthTx)
            MonthlyTrendPoint(
                month = month,
                income = monthSummary.totalIncome,
                expense = monthSummary.totalExpense,
                balance = monthSummary.balance
            )
        }

        val categoryBreakdown = selectedMonthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.description.ifBlank { "Uncategorized" } }
            .map { (category, items) ->
                val total = items.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
                CategoryBreakdownItem(category, MoneyFormatter.normalize(total))
            }
            .sortedByDescending { it.totalExpense }
            .take(5)

        val topExpenses = selectedMonthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sortedByDescending { it.amount }
            .take(3)
            .map { TopExpenseItem(it.description, it.amount, it.createdAt) }

        return DashboardInsights(
            selectedMonth = selectedMonth,
            summary = summary,
            trend = trend,
            categoryBreakdown = categoryBreakdown,
            topExpenses = topExpenses
        )
    }
}
