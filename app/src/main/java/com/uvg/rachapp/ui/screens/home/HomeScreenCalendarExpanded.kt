package com.uvg.rachapp.ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import java.time.LocalDate
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.viewModels.CalendarViewModel
import kotlin.collections.get
import kotlin.toString

//import com.justcalendar.R
//import com.justcalendar.ui.viewModels.CalendarViewModel
@Composable
fun HomeCalendarScreenExpanded(
    viewModel: CalendarViewModel = hiltViewModel(),
    onCollapse: () -> Unit = {}
) {
    val uiState by viewModel.uiState
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

        // Calendario expandido mostrando mes completo (35 días)
        ExpandedCalendar(viewModel = viewModel, onCollapse = onCollapse)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Tu racha actual es ${uiState.currentStreak}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Tu mayor racha fue ${uiState.maxStreak}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
    }
}



@Composable
fun ExpandedCalendar (
    viewModel: CalendarViewModel,
    onCollapse: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
        //.clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Título del mes
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = viewModel.getMonthTitleForBlock(),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .background(Color(0xFF1E2A78), RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp)
                    .fillMaxWidth()

            )



            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val scope = rememberCoroutineScope()

                IconButton(onClick = {
                    viewModel.goToPreviousMonthBlock()
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Anterior",
                        tint = Color.Gray
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    // Fila con nombres de días alineada a las columnas de la grilla
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        val cellWidth = this.maxWidth / 7
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("L", "Ma", "Mi", "J", "V", "S", "D").forEach { day ->
                                Text(
                                    text = day,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .width(cellWidth)
                                        .wrapContentHeight(align = Alignment.CenterVertically)
                                        .background(color = Color(0xFF5CB1E5))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grilla de días
                    // show the 35-day grid for the current month block (new month-based API)
                    val days = viewModel.getCurrentMonthBlockDays()
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.height(200.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(days.size) { index ->
                            val date = days[index]
                            val isInStreak = viewModel.uiState.value.streakDays.contains(date)
                            // Use viewModel helpers to determine cell shape
                            val isSingle = viewModel.isSingleInStreak(date)
                            val isFirst = viewModel.isFirstInStreak(date)
                            val isLast = viewModel.isLastInStreak(date)


                            val isTodayCell = date == LocalDate.now()

                            val shape = when {
                                isSingle -> CircleShape
                                isFirst -> RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                isLast -> RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                else -> RectangleShape //RoundedCornerShape(0.dp)
                            }

                            //val shape = if (isTodayCell){ }  (isInStreak) RoundedCornerShape(4.dp) else RoundedCornerShape(0.dp)

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(0.dp)
                                    // only clip when we want rounded corners or circle; middle cells remain rectangular
                                    .clip(shape)
                                    .background(if (isInStreak) Color(0xFFFFA726) else Color.Transparent)
                                    .border(if (isInStreak) 1.dp else 0.dp, if (isInStreak) Color(0xFFFFA726) else Color.Transparent, shape),

                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isInStreak) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isInStreak) Color.White else Color.Black
                                    )
                                )

                                if (isTodayCell) {
                                    Text(
                                        text = "  HOY  ",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = 3.dp)
                                            .background(Color(0xFF0040FF), shape = CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
                IconButton(onClick = { viewModel.goToNextMonthBlock() }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Siguiente",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // button to go back to current month when not showing it (month-based index)
            if (viewModel.uiState.value.currentMonthBlockIndex != 0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = {
                        // set month block index to 0 (current month) and select today
                        viewModel.setMonthBlockIndex(0)
                        viewModel.selectDate(LocalDate.now())
                    }) {
                        Text("Volver al mes actual")
                    }
                }
            }

            Text(
                text = "Puedes desplazarte entre los meses moviendo el calendario hacia la derecha o izquierda.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(25.dp))
        }

        // Ícono sobrepuesto en la esquina inferior derecha para encoger
        IconButton(
            onClick = { onCollapse() },
            modifier = Modifier
                .height(24.dp)
                .align(Alignment.BottomEnd)
                .padding(0.dp)
                .background(Color(0xFFA727B0), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Encoger",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
    }

}