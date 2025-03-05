package com.ivy.exchangeRates

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import com.ivy.MainCoroutineExtension
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.exchangeRates.data.RateUi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
internal class RatesStateFlowTest {

    private lateinit var ratesStateFlow: RatesStateFlow
    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var ratesDao: RatesDaoFake

    @BeforeEach
    fun setUp() {
        baseCurrencyFlow = mockk()
        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        ratesDao = RatesDaoFake()
        ratesStateFlow = RatesStateFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            ratesDao = ratesDao
        )
    }

    @Test
    fun `Test rates state flow emissions`() = runTest {
        ratesStateFlow().test {
            awaitItem() // Initial emission, ignore

            val rates1 = awaitItem()

            val automaticRate = RateUi("EUR", "USD",1.3)
            assertThat(rates1.automatic).contains(automaticRate)
            assertThat(rates1.manual).doesNotContain(automaticRate)

            val manualRate = RateUi("EUR", "AUD",1.6)
            assertThat(rates1.manual).contains(manualRate)
            assertThat(rates1.automatic).doesNotContain(manualRate)

            ratesDao.rates.value += Rate(rate = 1.5, currency = "CAD")

            val rates2 = awaitItem()

            val automaticRate2 = RateUi("EUR", "CAD",1.5)
            assertThat(rates2.automatic).contains(automaticRate2)
            assertThat(rates2.manual).doesNotContain(automaticRate2)
        }
    }
}