package com.uvg.rachapp.ui.screens.news

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.viewModels.NewsViewModel

data class Articulo(val titulo: Int, val descripcion: Int, val descripcionCompleta: Int)

@Composable
fun NewsScreen(
    newsViewModel: NewsViewModel = viewModel()
) {
    val backgroundColor = Color(0xFFEDF0F5)   // Fondo general (igual que tu diseño)
    val cardColor = Color(0xFFD0D4D8)         // Fondo de cada artículo (igual)
    val titleColor = Color(0xFF091F5B)        // Azul para títulos y subtítulos (igual)

    val articulos = listOf(
        Articulo(R.string.articulo1_title, R.string.articulo1_desc, R.string.articulo1_full),
        Articulo(R.string.articulo2_title, R.string.articulo2_desc, R.string.articulo2_full),
        Articulo(R.string.articulo3_title, R.string.articulo3_desc, R.string.articulo3_full),
        Articulo(R.string.articulo4_title, R.string.articulo4_desc, R.string.articulo4_full)
    )

    val uiState by newsViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Título principal (igual que tu diseño original)
                Text(
                    text = stringResource(R.string.articulos_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = titleColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Lista de artículos (tarjetas)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(articulos) { index, articulo ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { newsViewModel.onOpenDialog(index) }, // aquí abrimos dialog
                            shape = RoundedCornerShape(12.dp),
                            color = cardColor,
                            shadowElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(articulo.titulo),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = titleColor,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Text(
                                    text = stringResource(articulo.descripcion),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog que muestra la noticia completa de la tarjeta seleccionada
    if (uiState.isDialogOpen && uiState.selectedIndex != null) {
        val selected = articulos[uiState.selectedIndex!!]
        AlertDialog(
            onDismissRequest = { newsViewModel.onCloseDialog() },
            title = {
                Text(
                    text = stringResource(selected.titulo),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = titleColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(selected.descripcionCompleta),
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                Button(onClick = { newsViewModel.onCloseDialog() }) {
                    Text(text = stringResource(R.string.cerrar))
                }
            }
        )
    }
}

