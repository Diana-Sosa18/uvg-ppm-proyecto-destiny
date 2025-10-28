package com.uvg.rachapp.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.rachapp.R
import androidx.compose.ui.res.colorResource


@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.meteor))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre y correo
            Column {
                Text(
                    text = "User Name",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.galaxy)
                )
                Text(
                    text = "correo@gmail.com",
                    fontSize = 13.sp,
                    color = colorResource(id = R.color.galaxy)
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Progreso
        Text(
            text = "Progreso",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            color = colorResource(id = R.color.galaxy)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Valores de prueba
        val currentStreak = 10
        val longestStreak = 61
        val progress = currentStreak.toFloat() / longestStreak.toFloat()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProgressCard(title = "Racha actual", value = currentStreak.toString())
            ProgressCard(title = "Racha más larga", value = longestStreak.toString())
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Barra de progreso
        Box(
            modifier = Modifier
                .width(316.dp)
                .height(20.dp)
                .background(
                    color = colorResource(id = R.color.blue_gray),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .background(
                        color = colorResource(id = R.color.galaxy),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Estadísticas
        Text(
            text = "Estadísticas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start),
            color = colorResource(id = R.color.galaxy)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatsRow(number = "120", label = "días alimentando a Destiny")
            StatsRow(number = "12", label = "artículos leídos")
            StatsRow(number = "23", label = "notas creadas")
        }

        // Cerrar sesión
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.universe),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProgressCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(70.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.blue_gray)),
        elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title,
                fontSize = 13.sp)

            Spacer(modifier = Modifier.height(5.dp))

            Text(value, fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.universe))
        }
    }
}

@Composable
fun StatsRow(number: String, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = colorResource(id = R.color.universe),
            modifier = Modifier.alignByBaseline()
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.alignByBaseline()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(onLogout = {})
}
