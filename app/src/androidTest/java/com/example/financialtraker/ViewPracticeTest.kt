package com.example.financialtraker

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financialtraker.robot.TransactionsRobot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewPracticeTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun shouldNavigateToDashboardWhenDashboardButtonIsClicked() {
        TransactionsRobot()
            .unlockIfNeeded()
            .assertTransactionsVisible()

        onView(withId(R.id.btnDashboard)).perform(click())

        onView(withText("Dashboard")).check(matches(isDisplayed()))
    }

    @Test
    fun shouldAddTransactionAndShowItOnScreen() {
        TransactionsRobot().unlockIfNeeded()
            .addTransaction("Practice UI Expense", "19.90", isIncome = false)
            .assertTransactionVisible("Practice UI Expense")

        onView(isRoot()).check(matches(isDisplayed()))
    }

}
