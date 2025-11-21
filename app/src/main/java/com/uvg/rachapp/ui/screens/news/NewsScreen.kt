package com.uvg.rachapp.ui.screens.news

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvg.rachapp.R
import com.uvg.rachapp.data.model.News
import com.uvg.rachapp.ui.viewModels.NewsViewModel

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel = hiltViewModel()
) {
    val backgroundColor = Color(0xFFEDF0F5)
    val cardColor = Color(0xFFD0D4D8)
    val titleColor = Color(0xFF091F5B)

    val uiState by newsViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${uiState.errorMessage}",
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { newsViewModel.retryLoadNews() }) {
                        Text("Reintentar")
                    }
                }
            }

            else -> {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.articulos_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = titleColor,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(uiState.newsList) { index, news ->
                                NewsCard(
                                    news = news,
                                    isExpanded = uiState.expandedIndex == index,
                                    onClick = { newsViewModel.onNewsClick(index) },
                                    cardColor = cardColor,
                                    titleColor = titleColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para mostrar noticia completa
    if (uiState.isDialogOpen && uiState.selectedNews != null) {
        NewsDialog(
            news = uiState.selectedNews!!,
            onDismiss = { newsViewModel.onCloseDialog() },
            titleColor = titleColor
        )
    }
}

@Composable
fun NewsCard(
    news: News,
    isExpanded: Boolean,
    onClick: () -> Unit,
    cardColor: Color,
    titleColor: Color
) {
    val cardHeight by animateDpAsState(
        targetValue = if (isExpanded) 300.dp else 120.dp,
        label = "cardHeight"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = cardColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = titleColor,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = news.shortDesc,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black
                )
            )

            // Mostrar imagen rectangular cuando se expande
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    val imageResId = getImageResource(news.imageResName)
                    if (imageResId != null) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = "Imagen de ${news.title}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsDialog(
    news: News,
    onDismiss: () -> Unit,
    titleColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = titleColor,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = news.fullText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Imagen cuadrada en el diálogo
                val imageResId = getImageResource(news.imageResName)
                if (imageResId != null) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Imagen de ${news.title}",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(R.string.cerrar))
            }
        }
    )
}

@Composable
private fun getImageResource(imageName: String): Int? {
    return when (imageName) {
        "news1" -> R.drawable.news1
        "news2" -> R.drawable.news2
        "news3" -> R.drawable.news3
        "news4" -> R.drawable.news4
        "news5" -> R.drawable.news5
        "news6" -> R.drawable.news6
        "news7" -> R.drawable.news7
        "news8" -> R.drawable.news8
        "news9" -> R.drawable.news9
        "news10" -> R.drawable.news10
        else -> null
    }
}
