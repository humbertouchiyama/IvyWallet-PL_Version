package com.ivy.exchangeRates

import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.core.persistence.algorithm.calc.RatesDao
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RatesDaoFake: RatesDao {

    val rates = MutableStateFlow(
        listOf(
            Rate(1.3, "USD"),
            Rate(1.9, "AUD")
        )
    )
    val overrideRates = MutableStateFlow(
        listOf(
            Rate(1.6, "AUD")
        )
    )

    override fun findAll(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return rates
    }

    override fun findAllOverrides(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return overrideRates
    }
}