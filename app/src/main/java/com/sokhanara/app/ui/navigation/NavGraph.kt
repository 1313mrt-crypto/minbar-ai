package com.sokhanara.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sokhanara.app.ui.screens.home.HomeScreen

/**
 * Navigation Graph
 * گراف اصلی ناوبری برنامه
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.Create.route)
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route)
                },
                onNavigateToProjects = {
                    navController.navigate(Screen.Projects.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Create Screen - موقتاً خالی
        composable(route = Screen.Create.route) {
            // CreateSpeechScreen - بعداً پیاده‌سازی می‌شه
        }
        
        // Preview Screen - موقتاً خالی
        composable(
            route = Screen.Preview.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) {
            // PreviewScreen - بعداً پیاده‌سازی می‌شه
        }
        
        // Library Screen - موقتاً خالی
        composable(route = Screen.Library.route) {
            // LibraryScreen - بعداً پیاده‌سازی می‌شه
        }
        
        // Projects Screen - موقتاً خالی
        composable(route = Screen.Projects.route) {
            // ProjectsScreen - بعداً پیاده‌سازی می‌شه
        }
        
        // Settings Screen - موقتاً خالی
        composable(route = Screen.Settings.route) {
            // SettingsScreen - بعداً پیاده‌سازی می‌شه
        }