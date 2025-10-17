package com.uvg.rachapp.ui.navigation


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.uvg.rachapp.R
import kotlinx.coroutines.launch


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(Screen.Home.route) }
    val currentRoute = selectedTab
    val tabItems = listOf(
        Screen.Journal to R.drawable.iconjournal,
        Screen.News to R.drawable.iconarticles,
        Screen.Home to R.drawable.iconhome,
        Screen.Profile to R.drawable.iconprofile
    )
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            selectedTab = backStackEntry.destination.route ?: Screen.Home.route
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF091f5B)) {
                tabItems.forEach { (screen, iconRes) ->
                    val isSelected = selectedTab == screen.route

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                selectedTab = screen.route
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                                val iconSize: Dp = if (isSelected) 30.dp else 24.dp
                                val iconAlpha = if (isSelected) 1f else 0.6f

                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = screen.route,
                                    modifier = Modifier
                                        .size(iconSize)
                                        .alpha(iconAlpha),
                                    contentScale = ContentScale.Fit
                                )
                        },
                        label = {
                            // Solo muestra la etiqueta si el ítem está seleccionado
                            if (isSelected) {
                                Text(
                                    text = screen.route,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            // Colores para el ítem seleccionado (activo)
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = Color.Transparent,

                            // Colores para el ítem no seleccionado (inactivo)
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unselectedTextColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = modifier.padding(padding),
            onLogout = onLogout
        )
    }
}