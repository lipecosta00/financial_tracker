package com.example.financialtraker.robot

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.util.TreeIterables
import com.example.financialtraker.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import java.util.concurrent.TimeoutException

class TransactionsRobot {

    fun unlockIfNeeded(pin: String = "1234"): TransactionsRobot {
        return try {
            onView(withHint("PIN (4 digits)")).perform(replaceText(pin), closeSoftKeyboard())
            onView(withText("Confirm PIN")).perform(click())
            waitUntilVisible(withId(R.id.btnTransactions))
            this
        } catch (_: NoMatchingViewException) {
            this
        }
    }

    fun assertTransactionsVisible(): TransactionsRobot {
        onView(withId(R.id.btnTransactions)).check(matches(isDisplayed()))
        onView(withId(R.id.mainContainer)).check(matches(isDisplayed()))
        return this
    }

    fun addTransaction(description: String, amount: String, isIncome: Boolean): TransactionsRobot {
        onView(withContentDescription(stringByName("add_transaction"))).perform(click())
        onView(withHint("Description")).perform(replaceText(description), closeSoftKeyboard())
        onView(withHint("Amount")).perform(replaceText(amount), closeSoftKeyboard())

        if (isIncome) {
            onView(withText("Income")).check(matches(isDisplayed()))
            try {
                onView(withText("Income")).check(matches(isChecked()))
            } catch (_: AssertionError) {
                onView(withText("Income")).perform(click())
            }
        }

        onView(withText(stringByName("save"))).perform(click())
        waitUntilVisible(withText(description))
        return this
    }

    fun openEditByDescription(description: String): TransactionsRobot {
        onView(withText(description)).perform(click())
        return this
    }

    fun submitEdit(description: String, amount: String): TransactionsRobot {
        onView(withHint("Description")).perform(replaceText(description), closeSoftKeyboard())
        onView(withHint("Amount")).perform(replaceText(amount), closeSoftKeyboard())
        onView(withText(stringByName("save"))).perform(click())
        waitUntilVisible(withText(description))
        return this
    }

    fun longPressByDescription(description: String): TransactionsRobot {
        onView(withText(description)).perform(longClick())
        return this
    }

    fun confirmDelete(): TransactionsRobot {
        onView(withText(stringByName("delete"))).perform(click())
        return this
    }

    fun assertTransactionVisible(description: String): TransactionsRobot {
        onView(withText(description)).check(matches(isDisplayed()))
        return this
    }

    fun assertTransactionNotVisible(description: String): TransactionsRobot {
        waitUntilNotVisible(withText(description))
        onView(withText(description)).check(doesNotExist())
        return this
    }

    private fun stringByName(name: String): String {
        val context =
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val id = context.resources.getIdentifier(name, "string", context.packageName)
        return context.getString(id)
    }

    private fun waitUntilVisible(matcher: Matcher<View>, timeoutMs: Long = 4_000L) {
        onView(isRoot()).perform(
            waitForMatch(
                allOf(
                    matcher,
                    isDisplayed()
                ), timeoutMs
            )
        )
    }

    private fun waitUntilNotVisible(matcher: Matcher<View>, timeoutMs: Long = 4_000L) {
        onView(isRoot()).perform(waitForNotMatch(matcher, timeoutMs))
    }

    private fun waitForMatch(matcher: Matcher<View>, timeoutMs: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()

            override fun getDescription(): String = "wait up to $timeoutMs ms for matcher to match"

            override fun perform(uiController: UiController, view: View) {
                val endTime = System.currentTimeMillis() + timeoutMs
                do {
                    if (TreeIterables.breadthFirstViewTraversal(view)
                            .any { matcher.matches(it) }
                    ) return
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withCause(TimeoutException("Matcher not satisfied in $timeoutMs ms"))
                    .build()
            }
        }
    }

    private fun waitForNotMatch(matcher: Matcher<View>, timeoutMs: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()

            override fun getDescription(): String =
                "wait up to $timeoutMs ms for matcher to disappear"

            override fun perform(uiController: UiController, view: View) {
                val endTime = System.currentTimeMillis() + timeoutMs
                do {
                    if (TreeIterables.breadthFirstViewTraversal(view)
                            .none { matcher.matches(it) }
                    ) return
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withCause(TimeoutException("Matcher still visible after $timeoutMs ms"))
                    .build()
            }
        }
    }
}
