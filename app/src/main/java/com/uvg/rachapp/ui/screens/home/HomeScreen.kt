package com.uvg.rachapp.ui.screens.home


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
import com.uvg.rachapp.PlaceholderScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.uvg.rachapp.R

@Preview(showBackground = true)
@Composable
fun AppScreenPreview() {
    AppScreen(onLogout = {})
}

@Composable
fun AppScreen(
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(2) } // Pantalla activa

    val tabItems = listOf(
        "Notas" to R.drawable.journal_con_fondo,
        "Noticias" to R.drawable.noticias_con_fondo,
        "Inicio" to R.drawable.home_con_fondo,
        "Perfil" to R.drawable.perfil_con_fondo
    )

    Scaffold(
        bottomBar = {
            NavigationBar (
                containerColor = Color(0xFF091f5B)
            ) {
                tabItems.forEachIndexed { index, (label, iconRes) ->
                    val isSelected = selectedTab == index

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(if (isSelected) 20.dp else 28.dp)
                                )
                                if (isSelected) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        },
                        alwaysShowLabel = false // desactiva el label por defecto
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                2 -> HomeScreen(onLogout)
                else -> PlaceholderScreen("Pantalla $selectedTab")
            }
        }
    }
}


// El contenido principal de la pantalla Home
@Composable
fun HomeScreen(onLogout: () -> Unit) {
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
                            delay(200)
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
                            delay(200)
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
