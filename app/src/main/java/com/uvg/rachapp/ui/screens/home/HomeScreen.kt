package com.uvg.rachapp.ui.screens.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.uvg.rachapp.R
import com.uvg.rachapp.ui.viewModels.CalendarViewModel
import com.uvg.rachapp.ui.screens.home.HomeScreenExpanded
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.compareTo
import kotlin.toString

// El contenido principal de la pantalla Home
@Composable
fun HomeScreen() {
    // state to toggle between compact and expanded views
    var expanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    val CalendarViewModel: CalendarViewModel = hiltViewModel()

    // If expanded, show the expanded screen and provide a collapse callback
    if (expanded) {
        HomeScreenExpanded(viewModel = CalendarViewModel, onCollapse = { expanded = false })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.home_streak_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF002366)
        )


        Spacer(modifier = Modifier.height(16.dp))


        Calendar(viewModel = CalendarViewModel, onToggleExpand = { expanded = true })

        Spacer(modifier = Modifier.height(32.dp))

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
                            // animación simple al tocar, después agrego la real
                            scale = 1.2f
                            delay(200)
                            scale = 1f

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
                            // animación simple al tocar, después agrego la real
                            scale = 1.2f
                            delay(200)
                            scale = 1f

                            CalendarViewModel.removeTodayFromStreak()
                        }
                    }
            )
        }
    }
}


@Composable
fun Calendar(viewModel: CalendarViewModel, onToggleExpand: () -> Unit = {}) {
    val uiState by viewModel.uiState
    val currentBlockDays = viewModel.getCurrentBlockDays()
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
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
            ) {
                IconButton(onClick = {
                    viewModel.goToPreviousBlock()
                    // scroll to corresponding item in LazyRow
                    /*
                    scope.launch {
                        listState.animateScrollToItem(center + viewModel.uiState.value.currentBlockIndex)
                    }

                     */
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Anterior",
                        tint = Color.Gray
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .weight(0.33f)
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

                IconButton(onClick = {
                    viewModel.goToNextBlock()
                    /*scope.launch {
                        listState.animateScrollToItem(center + viewModel.uiState.value.currentBlockIndex)

                    }
                     */
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Siguiente",
                        tint = Color.Gray
                    )
                }
            }

            if (uiState.selectedDate != LocalDate.now()) {
                Button(
                    onClick = { viewModel.selectDate(LocalDate.now()) },
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
                text = viewModel.getMonthTitleForBlock(),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .background(Color(0xFF1E2A78), RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()
            )
        }

        IconButton(
            onClick = { onToggleExpand() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color(0xFFA727B0), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Agrandar",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun CalendarDay(
    day: String,
    isHighlighted: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .size(60.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ){
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
        ){
            Text(
                text = day,
                color = if (isHighlighted) Color.White else Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isToday){
            Text(
                text = "  HOY  ",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(end = 5.dp)
                    .offset(y = 11.dp)
                    .background(Color(0xFFFFD700))
            )
        }

        if (isSelected){
            Text(
                text = "SELECTED",
                color = Color.Black,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 4.dp)
                    .offset(y = (-11).dp)
            )
        }
    }
}
