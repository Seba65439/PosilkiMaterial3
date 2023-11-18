package pl.maifu.posilki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.paperdb.Paper
import pl.maifu.posilki.screens.HomeScreen
import pl.maifu.posilki.screens.ImportExportScreen
import pl.maifu.posilki.screens.SavedScheduleScreen
import pl.maifu.posilki.screens.ScheduleScreen
import pl.maifu.posilki.screens.SettingsScreen
import pl.maifu.posilki.ui.theme.PosilkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this)
        menu()
        setContent {
            val vm: MainViewModel = viewModel()
            vm.readTheme()
            val theme = vm.theme.collectAsState()
            PosilkiTheme(theme.value) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screens.HOME.route) {
                    composable(
                        Screens.HOME.route
                    ) {
                        HomeScreen(navController = navController)
                    }
                    composable(
                        Screens.SCHEDULE.route
                    ) {
                        ScheduleScreen(navController = navController)
                    }
                    composable(
                        Screens.SETTINGS.route
                    ) {
                        SettingsScreen(navController = navController, vm = vm)
                    }
                    composable(Screens.SCHEDULELIST.route) {
                        SavedScheduleScreen(navController = navController)
                    }
                    composable(Screens.IMPORTEXPORT.route) {
                        ImportExportScreen(navController = navController)
                    }
                }
            }
        }
    }
}

