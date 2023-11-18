package pl.maifu.posilki

import java.time.LocalDate

object Constants {
    const val USER_AGENT: String =
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36"
    const val URL: String = "http://mzzprc.pl/?page_id=609"
    const val PATH: String = "/data/data/pl.maifu.posilki/posilek.pdf"
    const val timeout: Int = 10000
    val datyRozpoczecia: List<LocalDate> = listOf(
        LocalDate.of(2023, 2, 6),
        LocalDate.of(2023, 2, 12),
        LocalDate.of(2023, 2, 16),
        LocalDate.of(2023, 2, 18)
    )
}

enum class Screens(val route: String) {
    HOME("home"),
    SCHEDULE("work"),
    SETTINGS("settings"),
    SCHEDULELIST("saved"),
    IMPORTEXPORT("importexport")
}