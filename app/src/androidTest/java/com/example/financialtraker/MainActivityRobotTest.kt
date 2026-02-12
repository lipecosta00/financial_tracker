package com.example.financialtraker

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financialtraker.robot.TransactionsRobot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityRobotTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun shouldOpenTransactionsTabByDefault() {
        TransactionsRobot()
            .unlockIfNeeded()
            .assertTransactionsVisible()
    }

    @Test
    fun shouldEditTransactionFromList() {
        val originalDescription = "Robot Edit Source"
        val updatedDescription = "Robot Edit Updated"

        TransactionsRobot()
            .unlockIfNeeded()
            .addTransaction(description = originalDescription, amount = "10.00", isIncome = false)
            .assertTransactionVisible(originalDescription)
            .openEditByDescription(originalDescription)
            .submitEdit(description = updatedDescription, amount = "25.00")
            .assertTransactionVisible(updatedDescription)
            .assertTransactionNotVisible(originalDescription)
    }

    @Test
    fun shouldDeleteTransactionFromList() {
        val description = "Robot Delete Target"

        TransactionsRobot()
            .unlockIfNeeded()
            .addTransaction(description = description, amount = "12.00", isIncome = false)
            .assertTransactionVisible(description)
            .longPressByDescription(description)
            .confirmDelete()
            .assertTransactionNotVisible(description)
    }
}
