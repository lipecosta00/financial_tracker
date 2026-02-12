package com.example.domain.usecase

import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth

class CalculateDashboardInsightsUseCaseTest {

    private val useCase = CalculateDashboardInsightsUseCase(CalculateMonthlySummaryUseCase())

    @Test
    fun `should build summary trend and top expenses`() {
        val january = Instant.parse("2026-01-15T10:00:00Z")
        val february = Instant.parse("2026-02-10T10:00:00Z")

        val transactions = listOf(
            FinancialTransaction("1", "Salary", BigDecimal("3000.00"), TransactionType.INCOME, february),
            FinancialTransaction("2", "Rent", BigDecimal("1200.00"), TransactionType.EXPENSE, february),
            FinancialTransaction("3", "Food", BigDecimal("300.00"), TransactionType.EXPENSE, february),
            FinancialTransaction("4", "Salary", BigDecimal("2500.00"), TransactionType.INCOME, january)
        )

        val result = useCase(transactions, YearMonth.of(2026, 2), monthWindow = 2)

        assertThat(result.summary.balance).isEqualTo(BigDecimal("1500.00"))
        assertThat(result.trend).hasSize(2)
        assertThat(result.categoryBreakdown.first().category).isEqualTo("Rent")
        assertThat(result.topExpenses.first().description).isEqualTo("Rent")
    }
}
