package com.example.domain.usecase

import com.example.core.money.MoneyFormatter
import com.example.domain.model.MonthlySummary
import com.example.domain.repository.TransactionRepository
import java.math.BigDecimal

class ObserveTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke() = repository.observeTransactions()
}

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(command: com.example.domain.model.CreateTransactionCommand) {
        repository.addTransaction(command)
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
