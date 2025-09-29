package com.uvg.rachapp.ui.screens.authentication.components


import androidx.annotation.DrawableRes


data class BottomNavItem(
    val label: String,
    @DrawableRes val iconResId: Int,

    val route: String
)