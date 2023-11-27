package pl.maifu.posilki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.paperdb.Paper
import kotlinx.coroutines.launch
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
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val scope = rememberCoroutineScope()
                val openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }
                ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
                    ModalDrawerSheet {
                        Text("Menu", modifier = Modifier.padding(16.dp), fontSize = 25.sp)
                        Divider()
                        navController.currentDestination
                        Spacer(modifier = Modifier.padding(5.dp))
                        NavigationDrawerItem(modifier = Modifier.padding(4.dp),
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.LunchDining,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = "Posiłki") },
                            selected = Screens.HOME.route == navBackStackEntry?.destination?.route,
                            onClick = {
                                navController.navigate(Screens.HOME.route) {
                                    popUpTo(Screens.HOME.route)
                                    launchSingleTop = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            })
                        NavigationDrawerItem(modifier = Modifier.padding(4.dp),
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = "Grafik") },
                            selected = Screens.SCHEDULE.route == navBackStackEntry?.destination?.route,
                            onClick = {
                                navController.navigate(Screens.SCHEDULE.route) {
                                    popUpTo(Screens.HOME.route)
                                    launchSingleTop = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            })
                        NavigationDrawerItem(modifier = Modifier.padding(4.dp),
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.FormatListBulleted,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = "Lista") },
                            selected = Screens.SCHEDULELIST.route == navBackStackEntry?.destination?.route,
                            onClick = {
                                navController.navigate(Screens.SCHEDULELIST.route) {
                                    popUpTo(Screens.HOME.route)
                                    launchSingleTop = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            })
                        NavigationDrawerItem(modifier = Modifier.padding(4.dp),
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Settings, contentDescription = null
                                )
                            },
                            label = { Text(text = "Ustawienia") },
                            selected = Screens.SETTINGS.route == navBackStackEntry?.destination?.route,
                            onClick = {
                                navController.navigate(Screens.SETTINGS.route) {
                                    popUpTo(Screens.HOME.route)
                                    launchSingleTop = true
                                }
                                scope.launch {
                                    drawerState.close()
                                }
                            })
                    }
                }, gesturesEnabled = true) {
                    NavHost(navController = navController, startDestination = Screens.HOME.route) {
                        composable(
                            Screens.HOME.route
                        ) {
                            HomeScreen(navController = navController, openDrawer = {
                                openDrawer()
                            })
                        }
                        composable(
                            Screens.SCHEDULE.route
                        ) {
                            ScheduleScreen(navController = navController, openDrawer = {
                                openDrawer()
                            })
                        }
                        composable(
                            Screens.SETTINGS.route
                        ) {
                            SettingsScreen(navController = navController, vm = vm, openDrawer = {
                                openDrawer()
                            })
                        }
                        composable(Screens.SCHEDULELIST.route) {
                            SavedScheduleScreen(navController = navController, openDrawer = {
                                openDrawer()
                            })
                        }
                        composable(Screens.IMPORTEXPORT.route) {
                            ImportExportScreen(navController = navController, openDrawer = {
                                openDrawer()
                            })
                        }
                    }
                }

            }
        }
    }
}

