package com.uvg.rachapp.ui.screens.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import java.time.format.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.viewModels.CalendarViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale


// El contenido principal de la pantalla Home
@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    val CalendarViewModel: CalendarViewModel = hiltViewModel()

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
        Calendar(viewModel = CalendarViewModel)

        Spacer(modifier = Modifier.height(16.dp))

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
                            // Añadir día de hoy a la base de datos
                            CalendarViewModel.markTodayAsCompleted()
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
                            // Borrar el día actual de la base de datos
                            CalendarViewModel.removeTodayFromStreak()
                        }
                    }
            )
        }
    }
}
//Calendario
//currentDate: fecha actual
//rangeDays: número total de días a mostrar (60 por defecto)
//Retorna una lista de listas, donde cada sublista representa un bloque de 8 días

fun generateCalendarBlocks(currentDate: LocalDate, rangeDays: Int = 60): List<List<LocalDate>> {
    // Calcular el rango de fechas centrado en la fecha actual. P.e. si rangeDays es 60, se mostrarán 30 días antes y 30 días después de la fecha actual.
    // ejemplo: si hoy es 15 de marzo, el rango será del 13 de febrero al 14 de abril.
    // start = 15 - 30 = -15 -> 13 de febrero
    // end = 15 + 30 = 45 -> 14 de abril
    // allDays = [13 de febrero, 14 de febrero, ..., 14 de abril]
    // allDays.chunked(8) = [[13 de febrero, 14 de febrero, ..., 20 de febrero], [21 de febrero, ..., 28 de febrero], ..., [8 de abril, 9 de abril, 10 de abril, 11 de abril, 12 de abril, 13 de abril, 14 de abril]]

    val start = currentDate.minusDays(rangeDays.toLong() / 2)
    //
    val end = currentDate.plusDays(rangeDays.toLong() / 2)

    val allDays = (0..rangeDays).map { start.plusDays(it.toLong()) }

    return allDays.chunked(8) // bloques de 8 días
}



@Composable
fun Calendar(viewModel: CalendarViewModel) {
    val uiState by viewModel.uiState
    val currentBlockDays = viewModel.getCurrentBlockDays()

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
    ){
        Text(
            text = if (uiState.currentStreak > 0) {
                "¡Llevas ${uiState.currentStreak} días! ^_^"
            } else {
                "Empieza una nueva racha"
            },
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF1E5BC6), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            IconButton(onClick = { viewModel.goToPreviousBlock() }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Anterior", tint = Color.Gray)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                currentBlockDays.forEach { date ->
                    item {
                        CalendarDay(
                            day = date.dayOfMonth.toString(),
                            isHighlighted = uiState.streakDays.contains(date),
                            isToday = date == LocalDate.now(),
                            isSelected = date == uiState.selectedDate,
                            onClick = { viewModel.selectDate(date) }
                        )
                    }
                }

            }

            IconButton(onClick = { viewModel.goToNextBlock() }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Siguiente", tint = Color.Gray)
            }
        }
        //Botón "volver al día actual", solo aparece cuando no se está en fecha



        if (uiState.selectedDate != LocalDate.now()){
            Button (
                onClick = {viewModel.selectDate(LocalDate.now())},
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
            )
            {
                Text("Volver al futuro XD")
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiState.selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es")).uppercase(),
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .background(Color(0xFF1E2A78), RoundedCornerShape(8.dp))
                .padding(vertical = 12.dp)
                .fillMaxWidth()
        )
        /*
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E2A78), RoundedCornerShape(8.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {

        }

         */
    }
}

@Composable
fun CalendarDay(
    day: String,
    isHighlighted: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    color = if (isHighlighted) Color(0xFFFF6F3C) else Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isToday) Color(0xFFFFD700) else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                color = if (isHighlighted) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isToday) {
            Text(
                text = "  HOY  ",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 11.dp)
                    .background(Color(0xFFFFD700))
            )
            /*
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 11.dp)
            ) {

            }

             */
        }

        if (isSelected){
            //Poner un placeholder al objeto de día para cuando este se encuentre seleccionado, indicando "seleccionado".
            Text(
                text = "SELECTED",
                color = Color.Black,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-11).dp)
                //.background(Color.Gray)
            )

        }


    }
}

/*

// ---- Calendario fake para visualización ----
// El contenido principal de la pantalla Home
@Preview(showBackground = true)
@Composable
fun AppScreenPreview() {
    PrevHomeScreen()
}

@Composable
fun HomeScreen() {
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
        CalendarPreview()

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


@Composable
fun CalendarPreview() {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
    ){
        Text(
            text = "¡Llevas 35 días! ^_^",
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF1E5BC6), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Anterior", tint = Color.Gray)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                val days = listOf("31", "1", "2", "3", "4", "5", "6", "7")
                days.forEach { day ->
                    item {
                        PreviewCalendarDay(
                            day = day,
                            isHighlighted = day in listOf("31", "1", "2"),
                            isToday = day == "2",
                            isSelected = day == "4" // Marcar el día 4 como seleccionado (placeholder
                        )
                    }
                }
            }

            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Siguiente", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E2A78), RoundedCornerShape(8.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OCTUBRE",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun PreviewCalendarDay(
    day: String,
    isHighlighted: Boolean,
    isToday: Boolean,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    color = if (isHighlighted) Color(0xFFFF6F3C) else Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isToday) Color(0xFFFFD700) else Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                color = if (isHighlighted) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isToday) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 11.dp)
            ) {
                Text(
                    text = "  HOY  ",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFFFD700))
                )
            }
        }

        if (isSelected){
            //Poner un placeholder al objeto de día para cuando este se encuentre seleccionado, indicando "seleccionado".
            Text(
                text = "SELECTED",
                color = Color.Black,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-11).dp)
                    //.background(Color.Gray)
            )

        }


    }
}


 */
