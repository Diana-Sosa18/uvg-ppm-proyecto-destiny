package com.uvg.rachapp.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")

    // Main
    object Journal : Screen("Journal")
    object News : Screen("News")
    object Home : Screen("Home")
    object Profile : Screen("Profile")
}