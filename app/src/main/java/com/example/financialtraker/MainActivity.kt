package com.example.financialtraker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.core.security.SecureTokenStore
import com.example.feature.auth.ui.AuthFragment
import com.example.feature.dashboard.ui.DashboardFragment
import com.example.feature.transactions.ui.TransactionsFragment
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val secureTokenStore: SecureTokenStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transactionsTab = findViewById<Button>(R.id.btnTransactions)
        val dashboardTab = findViewById<Button>(R.id.btnDashboard)
        val bottomBar = findViewById<LinearLayout>(R.id.bottomBar)

        supportFragmentManager.setFragmentResultListener(AuthFragment.RESULT_KEY, this) { _, bundle ->
            val authOk = bundle.getBoolean(AuthFragment.RESULT_AUTH_OK, false)
            if (authOk) {
                openMainContent(bottomBar)
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

        if (savedInstanceState == null) {
            if (secureTokenStore.readToken() == null) {
                bottomBar.visibility = View.GONE
                supportFragmentManager.commit {
                    replace(R.id.mainContainer, AuthFragment())
                }
            } else {
                openMainContent(bottomBar)
            }
        }
    }

    private fun openMainContent(bottomBar: LinearLayout) {
        bottomBar.visibility = View.VISIBLE
        supportFragmentManager.commit {
            replace(R.id.mainContainer, TransactionsFragment())
        }
    }
}
