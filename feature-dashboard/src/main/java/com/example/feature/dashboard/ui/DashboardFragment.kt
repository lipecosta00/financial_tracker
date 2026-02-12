package com.example.feature.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.state.collectAsState()
                DashboardScreen(
                    state = state,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth
                )
            }
        }
    }
}

@Composable
private fun DashboardScreen(
    state: DashboardUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineSmall
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onPreviousMonth) { Text("<") }
            Text(
                text = state.selectedMonth.toString(),
                modifier = Modifier.padding(top = 10.dp)
            )
            Button(onClick = onNextMonth) { Text(">") }
        }

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        state.insights?.let { insights ->
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Monthly summary")
                    Text("Income: ${insights.summary.totalIncome}")
                    Text("Expense: ${insights.summary.totalExpense}")
                    Text("Balance: ${insights.summary.balance}")
                }
            }

            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Trend (${insights.trend.size} months)")
                    insights.trend.forEach {
                        val maxValue = maxOf(
                            insights.trend.maxOfOrNull { point -> point.income } ?: BigDecimal.ONE,
                            insights.trend.maxOfOrNull { point -> point.expense } ?: BigDecimal.ONE
                        )
                        TrendBar(label = "${it.month}", value = it.expense, max = maxValue)
                    }
                }
            }

            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Categories (by description)")
                    if (insights.categoryBreakdown.isEmpty()) {
                        Text("No expense categories for selected month")
                    } else {
                        insights.categoryBreakdown.forEach {
                            Text("${it.category}: ${it.totalExpense}")
                        }
                    }
                }
            }

            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Top expenses")
                    if (insights.topExpenses.isEmpty()) {
                        Text("No expenses for selected month")
                    } else {
                        insights.topExpenses.forEach {
                            Text("${it.description}: ${it.amount}")
                        }
                    }
                }
            }
        }

        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun TrendBar(label: String, value: BigDecimal, max: BigDecimal) {
    val fraction = if (max <= BigDecimal.ZERO) 0f else (value.toFloat() / max.toFloat()).coerceIn(0f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label)
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}
