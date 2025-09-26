package com.example.laboratorio4_login.presentation.login.articles

import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.laboratorio4_login.R
import com.example.laboratorio4_login.ROUTE_ARTICLES
import com.example.laboratorio4_login.ROUTE_HOME
import com.example.laboratorio4_login.ROUTE_JOURNAL
import com.example.laboratorio4_login.ROUTE_PROFILE
import com.example.laboratorio4_login.presentation.login.BottomNavItem


data class Articulo(val titulo: Int, val descripcion: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(
    navController: NavController,
    mainNavController: NavController
) {
    val screenName = stringResource(id = R.string.title_articles_screen)

    // Colores
    val backgroundColor = Color(0xFFEDF0F5)
    val cardColor = Color(0xFFD0D4D8)
    val titleColor = Color(0xFF091F5B)

    val articulos = listOf(
        Articulo(R.string.articulo1_title, R.string.articulo1_desc),
        Articulo(R.string.articulo2_title, R.string.articulo2_desc),
        Articulo(R.string.articulo3_title, R.string.articulo3_desc),
        Articulo(R.string.articulo4_title, R.string.articulo4_desc),
    )

    val bottomNavItems = listOf(
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_journal),
            iconResId = R.drawable.iconjournal,
            route = ROUTE_JOURNAL
        ),
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_articles),
            iconResId = R.drawable.iconarticles,
            route = ROUTE_ARTICLES
        ),
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_home),
            iconResId = R.drawable.iconhome,
            route = ROUTE_HOME
        ),
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_profile),
            iconResId = R.drawable.iconprofile,
            route = ROUTE_PROFILE
        )
    )

    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(screenName) })
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                mainNavController.navigate(item.route) {
                                    popUpTo(mainNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = item.iconResId),
                                contentDescription = item.label,
                                modifier = Modifier
                                    .size(30.dp)
                                    .alpha(if (isSelected) 1f else 0.6f)
                            )
                        },
                        label = {
                            if (isSelected) {
                                Text(text = item.label, style = MaterialTheme.typography.labelSmall)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unselectedTextColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(articulos) { articulo ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = cardColor,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(articulo.titulo),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = titleColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = stringResource(articulo.descripcion),
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                            )
                        }
                    }
                }
            }
        }
    }
}