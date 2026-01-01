package com.sokhanara.app.ui.navigation

/**
 * Navigation Routes
 * مسیرهای مختلف برنامه
 */
sealed class Screen(val route: String) {
    // Main Screens
    object Home : Screen("home")
    object Create : Screen("create")
    object Preview : Screen("preview/{projectId}") {
        fun createRoute(projectId: String) = "preview/$projectId"
    }
    object Library : Screen("library")
    object Projects : Screen("projects")
    object Settings : Screen("settings")
    
    // Feature Screens
    object Analytics : Screen("analytics/{projectId}") {
        fun createRoute(projectId: String) = "analytics/$projectId"
    }
    object Live : Screen("live/{projectId}") {
        fun createRoute(projectId: String) = "live/$projectId"
    }
    object Training : Screen("training")
    object Profile : Screen("profile")
    
    // Future Features
    object Collaboration : Screen("collaboration/{projectId}") {
        fun createRoute(projectId: String) = "collaboration/$projectId"
    }
    object Marketplace : Screen("marketplace")
    object Challenge : Screen("challenge")
    object Kids : Screen("kids")
}