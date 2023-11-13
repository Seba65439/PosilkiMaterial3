package pl.maifu.posilki.data

import pl.maifu.posilki.Constants
import pl.maifu.posilki.readFirstDay
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

fun workShift(date: LocalDate): Int {
    val cycle = listOf(1, 1, 0, 2, 2, 0, 0, 0)
    val start = Constants.datyRozpoczecia[readFirstDay()]
    val days = ChronoUnit.DAYS.between(start, date)
    val index = days.mod(cycle.size)
    return cycle[index]
}

fun dateArray(start: LocalDate, end: LocalDate): Array<LocalDate> {
    val days = ChronoUnit.DAYS.between(start, end)
    val dates = arrayOfNulls<LocalDate>(days.toInt() + 1)
    for (i in 0..days.toInt()) {
        dates[i] = start.plusDays(i.toLong())
    }
    return dates.requireNoNulls()
}

fun calendarForMonth(date: LocalDate): List<Schedule> {
    val firstDay = date.with(TemporalAdjusters.firstDayOfMonth())
    val lastDay = date.with(TemporalAdjusters.lastDayOfMonth())
    val dateTab = dateArray(firstDay, lastDay).toMutableList()
    val calendar = mutableListOf<Schedule>()
    dateTab.forEach {
        val work = workShift(it)
        val type = when (work) {
            1 -> "1"
            2 -> "2"
            else -> "-"
        }
        calendar.add(Schedule(it, work, type, type, ""))
    }
    return calendar
}

data class Schedule(
    val date: LocalDate, val workSchift: Int, val type: String, val edited: String, val note: String
)