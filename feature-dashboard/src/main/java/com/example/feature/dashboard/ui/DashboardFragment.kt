package com.example.feature.dashboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                DashboardScreen()
            }
        }
    }
}

@Composable
private fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dashboard (Jetpack Compose)",
            style = MaterialTheme.typography.headlineSmall
        )
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Monthly goal")
                Text("70% of savings target reached")
            }
        }
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fraud monitoring")
                Text("No suspicious activity found in this fake environment")
            }
        }
    }
}
