package com.ivy.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.test_data.saveAccountWithTransactions
import com.ivy.common.androidtest.test_data.transactionWithTime
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.transaction.TransactionType
import com.ivy.navigation.Navigator
import com.ivy.wallet.ui.RootActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class HomeScreenTest: IvyAndroidTest() {

    @get:Rule
    val composeRule = createAndroidComposeRule<RootActivity>()

    @Inject
    lateinit var navigator: Navigator

    @Test
    fun testSelectingDateRange() = runBlocking<Unit> {
        val date = LocalDate.of(2025, 2, 2)
        setDate(date)

        val transaction1 = transactionWithTime(Instant.parse("2025-02-24T09:00:00Z")).copy(
            title = "Transaction1"
        )
        val transaction2 = transactionWithTime(Instant.parse("2025-03-01T09:00:00Z")).copy(
            title = "Transaction2"
        )
        val transaction3 = transactionWithTime(Instant.parse("2025-03-05T09:00:00Z")).copy(
            title = "Transaction3"
        )
        db.saveAccountWithTransactions(
            transactions = listOf(transaction1, transaction2, transaction3)
        )

        HomeScreenRobot(composeRule)
            .navigateTo(navigator)
            .openDateRangeSheet(timeProvider)
            .selectMonth("March")
            .assertDateIsDisplayed(1, "March")
            .assertDateIsDisplayed(31, "March")
            .clickDone()
            .clickUpcoming()
            .assertTransactionDoesNotExist("Transaction1")
            .assertTransactionIsDisplayed("Transaction2")
            .assertTransactionIsDisplayed("Transaction3")
    }

    @Test
    fun testGetOverdueTransaction_turnsIntoNormalTransaction() = runBlocking<Unit> {
        val date = LocalDate.of(2025, 2, 25)
        setDate(date)

        val transactionOverdue = transactionWithTime(Instant.parse("2025-02-24T09:00:00Z")).copy(
            title = "Transaction Overdue",
            type = TransactionType.Income,
            timeType = TrnTimeType.Due,
        )

        db.saveAccountWithTransactions(
            transactions = listOf(transactionOverdue)
        )

        HomeScreenRobot(composeRule)
            .navigateTo(navigator)
            .clickOverdue()
            .clickGet()
            .assertTransactionIsDisplayed("Transaction Overdue")
            .assertBalanceIsDisplayed(transactionOverdue.amount, transactionOverdue.currency)

    }
}