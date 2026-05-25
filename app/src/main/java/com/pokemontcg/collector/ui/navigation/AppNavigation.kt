package com.pokemontcg.collector.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pokemontcg.collector.ui.carddetail.CardDetailScreen
import com.pokemontcg.collector.ui.collection.CollectionScreen
import com.pokemontcg.collector.ui.scanner.ScannerScreen
import com.pokemontcg.collector.ui.sets.SetsScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Collection : Screen("collection", "Collection", Icons.Default.CollectionsBookmark)
    data object Scanner : Screen("scanner", "Scanner", Icons.Default.PhotoCamera)
    data object Sets : Screen("sets", "Sets", Icons.Default.Style)
    data object CardDetail : Screen("card_detail/{cardId}", "Card Detail", Icons.Default.CollectionsBookmark) {
        fun createRoute(cardId: Long) = "card_detail/$cardId"
    }
}

val bottomNavScreens = listOf(Screen.Collection, Screen.Scanner, Screen.Sets)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Collection.route
        ) {
            composable(Screen.Collection.route) {
                CollectionScreen(
                    onCardClick = { cardId ->
                        navController.navigate(Screen.CardDetail.createRoute(cardId))
                    }
                )
            }
            composable(Screen.Scanner.route) {
                ScannerScreen()
            }
            composable(Screen.Sets.route) {
                SetsScreen()
            }
            composable(
                route = Screen.CardDetail.route,
                arguments = listOf(
                    navArgument("cardId") { type = NavType.LongType }
                )
            ) {
                CardDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
