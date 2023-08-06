package pl.maifu.posilki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.paperdb.Paper
import pl.maifu.posilki.ui.screens.HomeScreen
import pl.maifu.posilki.ui.screens.InfoScreen
import pl.maifu.posilki.ui.theme.PosilkiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Paper.init(this)
        menu()
        setContent {
            PosilkiTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(onClick = {
                            navController.navigate(it)
                        })
                    }
                    composable("info") {
                        InfoScreen()
                    }
                }
            }
        }
    }
}

