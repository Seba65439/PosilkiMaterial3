package pl.maifu.posilki

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import pl.maifu.posilki.data.GetData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.TimeUnit

data class Posilek(
    val data: Date, val opis: String, var state: Boolean = false, var workday: Boolean = false
)

var posilki = mutableStateListOf<Posilek>()
    private set
var posilkiWorkDays = mutableStateListOf<Posilek>()
    private set
var index: Int? = null
var currentDate: Date = Date.from(Instant.now())

fun menu(flag: Boolean = true) {
    CoroutineScope(IO).launch {
        val readed = Paper.book().read<SnapshotStateList<Posilek>>("menu")
        if (!readed.isNullOrEmpty() && flag) {
            val month: Int = readed[0].data.month
            val monthNow: Int = Date.from(Instant.now()).month
            val year: Int = readed[0].data.year
            val yearNow: Int = Date.from(Instant.now()).year
            if (month == monthNow && year == yearNow) {
                posilki.clear()
                posilki.addAll(readed)
            } else {
                posilki.clear()
                posilki.addAll(GetData().getMenu().toMutableStateList())
                Paper.book().write("menu", posilki)
            }
        } else {
            posilki.clear()
            posilki.addAll(GetData().getMenu().toMutableStateList())
            Paper.book().write("menu", posilki)
        }

        val data: Date = Date.from(Instant.now())
        data.time = data.time - TimeUnit.HOURS.toMillis(22)
        posilki.forEach {
            if (it.data.after(data)) {
                if (index == null) {
                    index = posilki.indexOf(it)
                    currentDate = it.data
                }
                it.state = true
            }
        }
        posilki.workday()
        posilkiWorkDays = posilki.filter { it.workday }.toMutableStateList()

    }

}

fun Collection<Posilek>.workday() {
    this.forEach { it.workday = false }
    this.forEach {
        val first: LocalDate = Constants.datyRozpoczecia[readFirstDay()]
        val cycle = listOf(1, 1, 0, 0, 0, 0, 0, 0)
        val date: LocalDate =
            Instant.ofEpochMilli(it.data.time).atZone(ZoneId.systemDefault()).toLocalDate()
        val days = ChronoUnit.DAYS.between(first, date)
        val index = days.mod(cycle.size)
        if (cycle[index] == 1) it.workday = true
    }
}

fun updatePosilkiWorkDays(list: SnapshotStateList<Posilek>) {
    posilkiWorkDays = list.filter { it.workday }.toMutableStateList()
}

fun readFirstDay(): Int {
    return Paper.book().read("date", 0) ?: 0
}

fun saveBrigade(brigade: Int) {
    Paper.book().write("date", brigade)
}

fun saveFontSize(fontSize: Int) {
    Paper.book().write("font", fontSize)
}

fun readFontSize(): Int {
    return Paper.book().read("font", 0) ?: 0
}

fun saveFilterIndex(index: Int) {
    Paper.book().write("index", index)
}

fun readFilterIndex(): Int {
    return Paper.book().read("index", 0) ?: 0
}


