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
            .assertTransactionsVisible()
    }
}
