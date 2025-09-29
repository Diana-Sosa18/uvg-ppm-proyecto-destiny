package com.example.laboratorio4_login.presentation.articulos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.laboratorio4_login.R
import com.example.laboratorio4_login.presentation.login.BottomNavItem
import com.example.laboratorio4_login.presentation.login.home.HomeScreenContent

data class Articulo(val titulo: Int, val descripcion: Int)

@Composable
fun newsScreen(navController: NavController, mainNavController: NavController) {
    val bottomNavItems = listOf(
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_journal),
            iconResId = R.drawable.iconjournal,
            route = com.example.laboratorio4_login.ROUTE_JOURNAL
        ),
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_articles),
            iconResId = R.drawable.iconarticles,
            route = com.example.laboratorio4_login.ROUTE_ARTICLES
        ),
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_home),
            iconResId = R.drawable.iconhome,
            route = com.example.laboratorio4_login.ROUTE_HOME
        ),
        BottomNavItem(
            label = stringResource(id = R.string.bottom_nav_profile),
            iconResId = R.drawable.iconprofile,
            route = com.example.laboratorio4_login.ROUTE_PROFILE
        )
    )
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(

        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primary
            ) {
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
                            Image(
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            newsScreenContent()
        }
    }
}
@Composable
fun newsScreenContent(){
    val backgroundColor = Color(0xFFEDF0F5)   // Fondo general
    val cardColor = Color(0xFFD0D4D8)         // Fondo de cada artículo
    val titleColor = Color(0xFF091F5B)        // Azul para títulos y subtítulos

    val articulos = listOf(
        Articulo(R.string.articulo1_title, R.string.articulo1_desc),
        Articulo(R.string.articulo2_title, R.string.articulo2_desc),
        Articulo(R.string.articulo3_title, R.string.articulo3_desc),
        Articulo(R.string.articulo4_title, R.string.articulo4_desc),
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Título principal
            Text(
                text = stringResource(R.string.articulos_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = titleColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de artículos
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
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
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
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}