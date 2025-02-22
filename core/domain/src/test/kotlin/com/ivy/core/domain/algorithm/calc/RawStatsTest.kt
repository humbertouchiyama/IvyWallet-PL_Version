package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Test
import java.time.Instant

class RawStatsTest {

    @Test
    fun `Test creating rawStats from list of transactions`() {
        val now = Instant.now()
        val fiveSecondsAgo = now.minusSeconds(5)
        val eightSecondsAgo = now.minusSeconds(8)
        val tenSecondsAgo = now.minusSeconds(10)
        val transactions = listOf(
            CalcTrn(
                amount = 20.0,
                currency = "USD",
                type = TransactionType.Income,
                time = tenSecondsAgo
            ),
            CalcTrn(
                amount = 40.0,
                currency = "USD",
                type = TransactionType.Income,
                time = eightSecondsAgo
            ),
            CalcTrn(
                amount = 25.0,
                currency = "BRL",
                type = TransactionType.Income,
                time = fiveSecondsAgo
            ),
            CalcTrn(
                amount = 23.5,
                currency = "EUR",
                type = TransactionType.Expense,
                time = now
            )
        )
        val rawStats = rawStats(transactions)

        assertThat(rawStats.incomesCount).isEqualTo(3)
        assertThat(rawStats.incomes).isEqualTo(
            mapOf(
                "USD" to 60.0,
                "BRL" to 25.0
            )
        )

        assertThat(rawStats.expensesCount).isEqualTo(1)
        assertThat(rawStats.expenses).isEqualTo(mapOf("EUR" to 23.5))

        assertThat(rawStats.newestTrnTime).isEqualTo(now)
    }
}