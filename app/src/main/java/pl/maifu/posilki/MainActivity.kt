package pl.maifu.posilki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.paperdb.Paper
import pl.maifu.posilki.screens.HomeScreen
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
            PosilkiTheme {
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
                        SettingsScreen(navController = navController)
                    }
                    composable(Screens.SCHEDULELIST.route) {
                        SavedScheduleScreen(navController = navController)
                    }
                }
            }
        }
    }
}

