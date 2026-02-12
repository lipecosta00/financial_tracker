package com.example.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth

data class MonthlyTrendPoint(
    val month: YearMonth,
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal
)

data class CategoryBreakdownItem(
    val category: String,
    val totalExpense: BigDecimal
)

data class TopExpenseItem(
    val description: String,
    val amount: BigDecimal,
    val createdAt: Instant
)

data class DashboardInsights(
    val selectedMonth: YearMonth,
    val summary: MonthlySummary,
    val trend: List<MonthlyTrendPoint>,
    val categoryBreakdown: List<CategoryBreakdownItem>,
    val topExpenses: List<TopExpenseItem>
)
