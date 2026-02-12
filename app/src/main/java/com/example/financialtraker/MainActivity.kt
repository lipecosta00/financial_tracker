package com.example.financialtraker

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.feature.dashboard.ui.DashboardFragment
import com.example.feature.transactions.ui.TransactionsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transactionsTab = findViewById<Button>(R.id.btnTransactions)
        val dashboardTab = findViewById<Button>(R.id.btnDashboard)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.mainContainer, TransactionsFragment())
            }
        }

        transactionsTab.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.mainContainer, TransactionsFragment())
            }
        }

        dashboardTab.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.mainContainer, DashboardFragment())
            }
        }
    }
}
