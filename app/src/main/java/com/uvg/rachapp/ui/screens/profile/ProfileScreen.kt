package com.uvg.rachapp.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.viewModels.ColorViewModel
import com.uvg.rachapp.ui.viewModels.ProfileViewModel
import com.uvg.rachapp.viewModels.AuthViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val userEmail by profileViewModel.userEmail.collectAsState()
    val userName by profileViewModel.userName.collectAsState()
    val currentStreak by profileViewModel.currentStreak.collectAsState()
    val longestStreak by profileViewModel.longestStreak.collectAsState()
    val daysFed by profileViewModel.daysFed.collectAsState()
    val notesCount by profileViewModel.notesCount.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    val colorViewModel: ColorViewModel = hiltViewModel()
    val context = LocalContext.current
    val showLogoutDialog by profileViewModel.showLogoutDialog.collectAsState()
    val authViewModel: AuthViewModel = hiltViewModel()
    var showInfo by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila superior con título y botón de ajustes
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 3.dp)
        ) {
            // Título superior
            Text(
                text = "Mi Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.galaxy),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp)
            )

            // Botón de ajustes
            IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(colorResource(id = R.color.galaxy), CircleShape)
                    .padding(4.dp)
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = "Configuración",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
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
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.galaxy)
                )
                Text(
                    text = userEmail,
                    fontSize = 16.sp,
                    color = colorResource(id = R.color.galaxy)
                )
            }
        }


        Spacer(modifier = Modifier.height(28.dp))

        // Progreso
        Text(
            text = "Progreso",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start),
            color = colorResource(id = R.color.galaxy)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressCard(
                title = "Racha actual",
                value = currentStreak.toString(),
                showInfoButton = true,
                onInfoClick = { showInfo = true }
            )

            Spacer(modifier = Modifier.width(8.dp))

            ProgressCard(
                title = "Racha más larga",
                value = longestStreak.toString()
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        // Barra de progreso
        val progress = if (longestStreak > 0) currentStreak.toFloat() / longestStreak else 0f
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

        Spacer(modifier = Modifier.height(28.dp))

        // Estadísticas
        Text(
            text = "Estadísticas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start),
            color = colorResource(id = R.color.galaxy)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatsRow(number = daysFed.toString(), label = "días alimentando a Destiny")
            StatsRow(number = "12", label = "artículos leídos")
            StatsRow(number = notesCount.toString(), label = "notas creadas")
        }


        Spacer(modifier = Modifier.height(32.dp))

        // Botón de Cerrar sesión
        Button(
            onClick = {
                profileViewModel.onLogoutClick()
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(horizontal = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.meteor),
                contentColor = colorResource(id = R.color.universe)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Cerrar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

    // Diálogo de confirmación
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { profileViewModel.onDialogDismiss() },
                title = {
                    Text(
                        "Confirmar para cerrar sesión",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = { Text("¿Seguro que quiere cerrar sesión?") },
                confirmButton = {
                    TextButton(onClick = {
                        authViewModel.logout()
                        profileViewModel.onDialogDismiss()
                        onLogout()
                    }) {
                        Text(
                            text = stringResource(R.string.journal_confirm_yes),
                            color = Color(0xFF757575)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { profileViewModel.onDialogDismiss() }) {
                        Text(
                            text = stringResource(R.string.journal_confirm_no),
                            color = Color(0xFF1E2A78)
                        )
                    }
                }
            )
        }
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = {
                    Text(text = "Color de racha de calendario", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column { // Primer grupo de colores (naranja)
                        // Row 1: naranja pair
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // scheme 1 = naranja
                                    colorViewModel.setSelection(1)
                                    Toast.makeText(context, "Color de racha actualizado", Toast.LENGTH_SHORT).show()
                                    showSettingsDialog = false
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(color = colorResource(id = R.color.vivid_orange))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(color = colorResource(id = R.color.light_orange))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre los grupos

                        // Segundo grupo de colores (turquesa)
                        // Row 2: turquesa pair
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // scheme 2 = turquesa
                                    colorViewModel.setSelection(2)
                                    Toast.makeText(context, "Color de racha actualizado", Toast.LENGTH_SHORT).show()
                                    showSettingsDialog = false
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(color = colorResource(id = R.color.teal_200))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(color = colorResource(id = R.color.teal_700))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp)) // Espacio entre los grupos

                        // Tercer grupo de colores (morado)
                        // Row 3: purple pair
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // scheme 3 = morado
                                    colorViewModel.setSelection(3)
                                    Toast.makeText(context, "Color de racha actualizado", Toast.LENGTH_SHORT).show()
                                    showSettingsDialog = false
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(color = colorResource(id = R.color.purple_200))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .background(color = colorResource(id = R.color.dark_magenta))
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }


        if (showInfo) {
            AlertDialog(
                onDismissRequest = { showInfo = false },
                text = { Text(profileViewModel.getStreakHelpMessage()) },
                confirmButton = {
                    TextButton(onClick = { showInfo = false }) {
                        Text(
                            "Entendido",
                            color = Color(0xFF1E2A78))
                    }
                }
            )
        }



    }
}

@Composable
fun ProgressCard(
    title: String,
    value: String,
    showInfoButton: Boolean = false,
    onInfoClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(5.dp, colorResource(id = R.color.venus))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(5.dp))

            // Icono a la par del número
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.universe)
                )

                if (showInfoButton) {
                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "Información",
                            tint = colorResource(id = R.color.venus)
                        )
                    }
                }
            }
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
