package com.ivy.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.IvyComposeRule
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.pure.util.formatShortened
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.main.Home
import kotlinx.coroutines.runBlocking

class HomeScreenRobot(
    private val composeRule: IvyComposeRule
) {

    fun navigateTo(navigator: Navigator): HomeScreenRobot {
        runBlocking {
            composeRule.awaitIdle()
            composeRule.runOnUiThread {
                navigator.navigate(Home.route) {
                    popUpTo(Home.route) {
                        inclusive = false
                    }
                }
            }
        }
        return this
    }

    fun openDateRangeSheet(timeProvider: TimeProvider): HomeScreenRobot {
        composeRule.onNodeWithText(text = timeProvider.dateNow().month.name, ignoreCase = true)
            .performClick()
        return this
    }

    fun selectMonth(monthName: String): HomeScreenRobot {
        composeRule
            .onNodeWithText(monthName)
            .performClick()
        return this
    }

    fun assertDateIsDisplayed(day: Int, month: String): HomeScreenRobot {
        val paddedDay = day.toString().padStart(2, '0')
        composeRule
            .onNodeWithText("${month.take(3)}. $paddedDay")
            .assertIsDisplayed()
        return this
    }

    fun clickDone(): HomeScreenRobot {
        composeRule.onNodeWithText("Done").performClick()
        return this
    }

    fun clickUpcoming(): HomeScreenRobot {
        composeRule.onNodeWithText("Upcoming").performClick()
        return this
    }

    fun clickOverdue(): HomeScreenRobot {
        composeRule.onNodeWithText("Overdue").performClick()
        return this
    }

    fun assertTransactionDoesNotExist(transactionName: String): HomeScreenRobot {
        composeRule.onNodeWithText(transactionName).assertDoesNotExist()
        return this
    }

    fun assertTransactionIsDisplayed(transactionName: String): HomeScreenRobot {
        composeRule.onNodeWithText(transactionName).assertIsDisplayed()
        return this
    }

    fun clickGet(): HomeScreenRobot {
        composeRule.onNodeWithText("Get").performClick()
        return this
    }

    fun assertBalanceIsDisplayed(transactionAmount: Double, currency: String): HomeScreenRobot {
        val formattedAmount = formatShortened(transactionAmount)

        composeRule
            .onAllNodes(
                hasText(formattedAmount) and hasAnySibling((hasText(currency))),
                useUnmergedTree = true
            )
            .onFirst()
            .assertIsDisplayed()
        return this
    }
}