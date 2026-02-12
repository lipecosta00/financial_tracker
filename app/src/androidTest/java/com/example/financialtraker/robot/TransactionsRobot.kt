package com.example.financialtraker.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.financialtraker.R

class TransactionsRobot {

    fun assertTransactionsVisible(): TransactionsRobot {
        onView(withId(R.id.btnTransactions)).check(matches(isDisplayed()))
        onView(withId(R.id.mainContainer)).check(matches(isDisplayed()))
        return this
    }
}
