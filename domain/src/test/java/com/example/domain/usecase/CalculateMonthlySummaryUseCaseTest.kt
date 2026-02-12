package com.example.domain.usecase

import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class CalculateMonthlySummaryUseCaseTest {

    private val useCase = CalculateMonthlySummaryUseCase()

    @Test
    fun `should calculate income expense and balance`() {
        val transactions = listOf(
            FinancialTransaction("1", "Salary", BigDecimal("2000.00"), TransactionType.INCOME, Instant.now()),
            FinancialTransaction("2", "Food", BigDecimal("500.00"), TransactionType.EXPENSE, Instant.now())
        )

        val result = useCase(transactions)

        assertThat(result.totalIncome).isEqualTo(BigDecimal("2000.00"))
        assertThat(result.totalExpense).isEqualTo(BigDecimal("500.00"))
        assertThat(result.balance).isEqualTo(BigDecimal("1500.00"))
    }
}
