package com.uvg.rachapp.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.uvg.rachapp.ui.navigation.Screen
import com.uvg.rachapp.ui.navigation.AppNavigation
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.screens.authentication.components.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onLogout: () -> Unit ) {
//Lo pongo así por si más adelante hay que modificar la lógica de cerrar sesión, apartandola de lo visual
    ProfileScreenContent(onLogout)
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
@Composable
fun ProfileScreenContent(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
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
