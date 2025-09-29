package com.example.laboratorio4_login.presentation.login.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.laboratorio4_login.R
import com.example.laboratorio4_login.presentation.login.BottomNavItem
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.draw.alpha


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, mainNavController: NavController) {
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
            HomeScreenContent()
        }
    }
}


// El contenido principal de la pantalla Home
@Composable
fun HomeScreenContent() {
    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título

        Text(
            text = stringResource(id = R.string.home_streak_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF002366)
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Calendario simulado en lo que termino el oficial (6x7 grid)
        CalendarView()

        Spacer(modifier = Modifier.height(16.dp))
        //Button(onClick = { /* Acción de cerrar sesión aquí si se requiere */ }) {
            //Text("Cerrar sesión")
        //}
        Spacer(modifier = Modifier.height(16.dp))
        // Botón + texto
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.buttoncroqueta),
                contentDescription = stringResource(id = R.string.home_button_desc),
                modifier = Modifier
                    .scale(scale)
                    .clickable {
                        scope.launch {
                            // animación simple al tocar
                            scale = 1.2f
                            kotlinx.coroutines.delay(200)
                            scale = 1f
                        }
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                stringResource(id = R.string.home_arrow),
                fontSize = 26.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(id = R.string.home_feed_destiny),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Imagen animada (1/3 de pantalla)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight(0.33f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.perrito),
                contentDescription = stringResource(id = R.string.home_dog_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        scope.launch {
                            // animación simple al tocar
                            scale = 1.2f
                            kotlinx.coroutines.delay(200)
                            scale = 1f
                        }
                    }
            )
        }
    }
}



// ---- Calendario fake ----
@Composable
fun CalendarView() {
    val PrimaryBlue = Color(0x091f5B)
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(20.dp))
        for (week in 0 until 2) { // 2 semanas de ejemplo
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (day in 1..4) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.LightGray, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${week * 4 + day}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.calendar_month),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xff091F5B))
    }
}
