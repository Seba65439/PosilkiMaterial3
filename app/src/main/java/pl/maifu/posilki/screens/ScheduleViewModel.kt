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

    fun holidayName(date: LocalDate): String {
        // stałe dla świąt stałych
        val NEW_YEAR = "Nowy Rok"
        val EPIPHANY = "Święto Trzech Króli"
        val LABOR_DAY = "Święto Pracy"
        val CONSTITUTION_DAY = "Święto Konstytucji 3 Maja"
        val ASSUMPTION = "Wniebowzięcie Najświętszej Maryi Panny"
        val ALL_SAINTS = "Wszystkich Świętych"
        val INDEPENDENCE_DAY = "Narodowe Święto Niepodległości"
        val CHRISTMAS_DAY = "Boże Narodzenie"
        val BOXING_DAY = "Drugi dzień Bożego Narodzenia"

        // stałe dla świąt ruchomych
        val EASTER = "Wielkanoc"
        val EASTER_MONDAY = "Poniedziałek Wielkanocny"
        val CORPUS_CHRISTI = "Boże Ciało"
        val PENTECOST = "Zielone Świątki"

        // pobranie roku, miesiąca i dnia z daty
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        // obliczenie daty Wielkanocy dla danego roku
        val easterDate = calculateEasterDate(year)
        val easterMonth = easterDate.monthValue
        val easterDay = easterDate.dayOfMonth

        // obliczenie daty Poniedzialku Wielkanocnego dla danego roku
        val easterMondayDate = easterDate.plusDays(1)
        val easterMondayMonth = easterMondayDate.monthValue
        val easterMondayDay = easterMondayDate.dayOfMonth

        // obliczenie daty Bożego Ciała dla danego roku
        val corpusChristiDate = easterDate.plusDays(60)
        val corpusChristiMonth = corpusChristiDate.monthValue
        val corpusChristiDay = corpusChristiDate.dayOfMonth

        // obliczenie daty Zielonych Świątek dla danego roku
        val pentecostDate = easterDate.plusDays(49)
        val pentecostMonth = pentecostDate.monthValue
        val pentecostDay = pentecostDate.dayOfMonth

        // sprawdzenie, czy data jest świętem stałym
        when {
            month == 1 && day == 1 -> return NEW_YEAR
            month == 1 && day == 6 -> return EPIPHANY
            month == 5 && day == 1 -> return LABOR_DAY
            month == 5 && day == 3 -> return CONSTITUTION_DAY
            month == 8 && day == 15 -> return ASSUMPTION
            month == 11 && day == 1 -> return ALL_SAINTS
            month == 11 && day == 11 -> return INDEPENDENCE_DAY
            month == 12 && day == 25 -> return CHRISTMAS_DAY
            month == 12 && day == 26 -> return BOXING_DAY
        }

        // sprawdzenie, czy data jest świętem ruchomym
        when {
            month == easterMonth && day == easterDay -> return EASTER
            month == easterMondayMonth && day == easterMondayDay -> return EASTER_MONDAY
            month == corpusChristiMonth && day == corpusChristiDay -> return CORPUS_CHRISTI
            month == pentecostMonth && day == pentecostDay -> return PENTECOST
        }

        // jeśli data nie jest żadnym świętem, zwróć pusty string
        return ""
    }

    fun calculateEasterDate(year: Int): LocalDate {
        val a = year.mod(19)
        val b = year / 100
        val c = year.mod(100)
        val d = b / 4
        val e = b.mod(4)
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15).mod(30)
        val i = c / 4
        val k = c.mod(4)
        val l = (32 + 2 * e + 2 * i - h - k).mod(7)
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114).mod(31)) + 1
        return LocalDate.of(year, month, day)
    }
}