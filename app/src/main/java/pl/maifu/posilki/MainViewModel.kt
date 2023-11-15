package pl.maifu.posilki

import androidx.lifecycle.ViewModel
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private var _theme = MutableStateFlow(0)
    val theme = _theme.asStateFlow()

    fun saveTheme(theme: Int) {
        Paper.book().write("theme", theme)
        _theme.value = theme
    }

    fun readTheme() {
        _theme.value = Paper.book().read("theme", 0) ?: 0
    }
}