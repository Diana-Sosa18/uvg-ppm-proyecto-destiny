package com.example.laboratorio4_login.presentation.login.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.laboratorio4_login.R
import com.example.laboratorio4_login.ROUTE_ARTICLES
import com.example.laboratorio4_login.ROUTE_HOME
import com.example.laboratorio4_login.ROUTE_JOURNAL
import com.example.laboratorio4_login.ROUTE_PROFILE
import com.example.laboratorio4_login.presentation.login.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, mainNavController: NavController) {

    val screenName = stringResource(id = R.string.title_profile_screen)

    // Ítems de barra de navegación
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
            TopAppBar(
                title = { Text(screenName) },
                actions = {
                    Button(
                        onClick = { navController.navigate(com.example.laboratorio4_login.ROUTE_LOGIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(id = R.string.logout),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            )
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
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
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
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -------- Avatar + nombre --------
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.user_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = { /* TODO: Navegar a editar perfil */ }) {
                Text(text = stringResource(id = R.string.edit_profile))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Progreso --------
            Text(
                text = stringResource(id = R.string.progress_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressCard(title = "Racha actual", value = "25 días")
                ProgressCard(title = "Racha más larga", value = "40 días")
            }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = 0.3f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // -------- Estadísticas --------
            Text(
                text = stringResource(id = R.string.stats_title),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(id = R.string.days_feeding))
            Text(text = stringResource(id = R.string.articles_readed))
            Text(text = stringResource(id = R.string.notes_created))

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ProgressCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(70.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}