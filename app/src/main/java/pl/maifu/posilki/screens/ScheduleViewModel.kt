package pl.maifu.posilki.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.maifu.posilki.data.Schedule
import pl.maifu.posilki.data.calendarForMonth
import pl.maifu.posilki.readFirstDay
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {
    private var _calendarMonth = MutableStateFlow(listOf<Schedule>())
    val calendarMonth = _calendarMonth.asStateFlow()

    private val _date = MutableStateFlow(LocalDate.now())
    val date = _date.asStateFlow()

    private val _savedSchedule = mutableStateListOf<Schedule>()
    val savedSchedule = _savedSchedule
    fun scheduleCalendar() {
        _calendarMonth.value = calendarForMonth(_date.value)
        update()
    }

    fun minus() {
        _date.value = _date.value.minusMonths(1)
        readSchedule()
        scheduleCalendar()
    }

    fun plus() {
        _date.value = _date.value.plusMonths(1)
        scheduleCalendar()
    }

    fun deleteSchedule(schedule: Schedule) {
        val toRemove = _savedSchedule.filter { it.date == schedule.date }
        _savedSchedule.removeAll(toRemove)
        Paper.book().write("brygada${readFirstDay()}", _savedSchedule)
        update()
    }

    fun saveSchedule(schedule: Schedule) {
        val toRemove = _savedSchedule.filter { it.date == schedule.date }
        _savedSchedule.removeAll(toRemove)
        _savedSchedule.add(schedule)
        Paper.book().write("brygada${readFirstDay()}", _savedSchedule)
        update()
    }

    fun readSchedule() {
        val read = Paper.book().read<MutableList<Schedule>>("brygada${readFirstDay()}")
        _savedSchedule.clear()
        if (!read.isNullOrEmpty()) {
            _savedSchedule.addAll(read)
        }
    }

    fun update() {
        readSchedule()
        val updated = mutableListOf<Schedule>()
        _calendarMonth.value.forEach { element ->
            val edited = _savedSchedule.find { it.date == element.date }
            if (edited != null) {
                updated.add(edited)
            } else updated.add(element)
        }
        _calendarMonth.value = updated
    }
}