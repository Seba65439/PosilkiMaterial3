package pl.maifu.posilki.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.maifu.posilki.data.calendarForMonth
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {
    private val _calendarMonth = MutableStateFlow(listOf<LocalDate>())
    val calendarMonth = _calendarMonth.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date = _date.asStateFlow()
    fun scheduleCalendar() {
        _calendarMonth.value = calendarForMonth(_date.value)
    }

    fun minus() {
        _date.value = _date.value.minusMonths(1)
        scheduleCalendar()
    }

    fun plus() {
        _date.value = _date.value.plusMonths(1)
        scheduleCalendar()
    }
}