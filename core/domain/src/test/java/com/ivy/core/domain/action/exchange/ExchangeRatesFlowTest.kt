package com.ivy.core.domain.action.exchange

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.TestDispatchers
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExchangeRatesFlowTest {

    private lateinit var exchangeRatesFlow: ExchangeRatesFlow
    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var exchangeRateDao: ExchangeRateDaoFake
    private lateinit var exchangeRateOverrideDao: ExchangeRateOverrideDaoFake

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }
    
    @BeforeEach
    fun setUp() {
        exchangeRateDao = ExchangeRateDaoFake()
        exchangeRateOverrideDao = ExchangeRateOverrideDaoFake()

        baseCurrencyFlow = mockk<BaseCurrencyFlow>(relaxed = true)
        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)

        exchangeRatesFlow = ExchangeRatesFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            exchangeRateDao = exchangeRateDao,
            exchangeRateOverrideDao = exchangeRateOverrideDao,
            dispatchers = testDispatchers
        )
    }

    @Test
    fun `Test exchange rates flow emissions`() = runTest {
        val exchangeRates = listOf(
            exchangeRateEntity("USD", 1.1),
            exchangeRateEntity("GBP", 0.9)
        )
        val exchangeRateOverrides = listOf(
            exchangeRateOverrideEntity("GBP", 1.25)
        )
        exchangeRatesFlow().test {
            awaitItem() // Initial emission, ignore

            exchangeRateDao.save(exchangeRates)
            exchangeRateOverrideDao.save(exchangeRateOverrides)
            val rates1 = awaitItem()

            assertThat(rates1.rates).hasSize(2)
            assertThat(rates1.rates["USD"]).isEqualTo(1.1)
            assertThat(rates1.rates["GBP"]).isEqualTo(1.25)

            exchangeRateOverrideDao.save(emptyList())

            val rates2 = awaitItem()
            assertThat(rates2.rates).hasSize(2)
            assertThat(rates2.rates["USD"]).isEqualTo(1.1)
            assertThat(rates2.rates["GBP"]).isEqualTo(0.9)
        }
    }
}