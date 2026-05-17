package com.hafiz.nimmaguru.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hafiz.nimmaguru.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Gurus", Icons.Filled.Home)
    object Calendar : Screen("calendar", "Calendar", Icons.Filled.CalendarMonth)
    object MyProfile : Screen("my_profile", "My Profile", Icons.Filled.Person)
    object WallOfFame : Screen("wall_of_fame", "Wall of Fame", Icons.Filled.EmojiEvents)
}

val bottomNavScreens = listOf(Screen.Home, Screen.Calendar, Screen.MyProfile, Screen.WallOfFame)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val auth = Firebase.auth
    val startDestination = if (auth.currentUser != null) "home" else "login"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = bottomNavScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            composable("home") {
                HomeScreen(onGuruClick = { guruId ->
                    navController.navigate("guru_detail/$guruId")
                })
            }
            composable("guru_detail/{guruId}") { backStackEntry ->
                val guruId = backStackEntry.arguments?.getString("guruId") ?: ""
                GuruDetailScreen(guruId = guruId, onBack = { navController.popBackStack() })
            }
            composable("calendar") {
                CalendarScreen()
            }
            composable("my_profile") {
                MyProfileScreen(onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }
            composable("wall_of_fame") {
                WallOfFameScreen()
            }
        }
    }
}
