package com.ivy.core.ui.time.picker.date

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.MainCoroutineRule
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import com.ivy.core.ui.time.picker.date.data.PickerYear
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class DatePickerViewModelTest: IvyAndroidTest() {

    private lateinit var viewModel: DatePickerViewModel

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = DatePickerViewModel(
            appContext = context,
            timeProvider = timeProvider
        )
    }

    @Test
    fun testPickingDate() = runTest {
        setDate(LocalDate.of(2023, 1, 1))
        viewModel.onEvent(DatePickerEvent.YearChange(PickerYear("2023", 2023)))
        viewModel.uiState.test {
            awaitItem() // ignore first emission
            viewModel.onEvent(DatePickerEvent.DayChange(PickerDay("30", 30)))

            val emission2 = awaitItem()

            assertThat(emission2.selected.dayOfMonth).isEqualTo(30)

            viewModel.onEvent(DatePickerEvent.MonthChange(PickerMonth("Feb", 2)))

            val emission3 = awaitItem()

            val timeProviderDate = timeProvider.dateNow()
            assertThat(emission3.selected).isEqualTo(
                LocalDate.of(timeProviderDate.year, 2, 28)
            )
        }
    }
}