package com.uvg.rachapp.ui.screens.journal


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uvg.rachapp.R

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Este será el NOteEntity para la base recordaaar =======================
data class NotePreview(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
)

@Composable
fun JournalScreen() {

    var showAddNoteForm by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showNoteDetailDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<NotePreview?>(null) }
    var titleInput by remember { mutableStateOf("") }
    var contentInput by remember { mutableStateOf("") }

    var sampleNotes by remember {
        mutableStateOf(
            //Notas para probar el mensaje de Advertenca, porqque no se conecta a la base todavia
            listOf(
                NotePreview(
                    id = 1,
                    title = "Mi primer día",
                    content = "Hoy fue un día difícil pero lo logré. Me siento orgulloso de mí mismo.",
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                NotePreview(
                    id = 2,
                    title = "Reflexión semanal",
                    content = "Una semana completa sin recaer. Cada día es una victoria.",
                    timestamp = System.currentTimeMillis() - 172800000
                ),
                NotePreview(
                    id = 3,
                    title = "Momento de gratitud",
                    content = "Agradezco el apoyo de mi familia y amigos en este proceso.",
                    timestamp = System.currentTimeMillis() - 259200000
                )
            )
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF5F5F5),
                shadowElevation = 4.dp
            ) {

                Text(
                    text = stringResource(R.string.journal_title),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF002366)
                )
            }


            if (sampleNotes.isEmpty()) {
                EmptyStateView(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleNotes) { note ->
                        NoteItemCard(
                            note = note,
                            onClick = {
                                selectedNote = note
                                showConfirmDialog = true
                            }
                        )
                    }
                }
            }


            Button(
                onClick = { showAddNoteForm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E2A78)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.journal_add_note),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(R.string.journal_add_note),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }


        AnimatedVisibility(
            visible = showAddNoteForm,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AddNoteForm(
                titleValue = titleInput,
                contentValue = contentInput,
                onTitleChange = { titleInput = it },
                onContentChange = { contentInput = it },
                onSave = {
                    val newNote = NotePreview(
                        id = (sampleNotes.maxOfOrNull { it.id } ?: 0) + 1,
                        title = titleInput,
                        content = contentInput,
                        timestamp = System.currentTimeMillis()
                    )
                    sampleNotes = sampleNotes + newNote
                    titleInput = ""
                    contentInput = ""
                    showAddNoteForm = false
                },
                onCancel = {
                    titleInput = ""
                    contentInput = ""
                    showAddNoteForm = false
                }
            )
        }

    }


    if (showConfirmDialog) {
        ConfirmViewDialog(
            onConfirm = {
                showConfirmDialog = false
                showNoteDetailDialog = true
            },
            onDismiss = {
                showConfirmDialog = false
                selectedNote = null
            }
        )
    }


    if (showNoteDetailDialog && selectedNote != null) {
        NoteDetailDialog(
            note = selectedNote!!,
            onDismiss = {
                showNoteDetailDialog = false
                selectedNote = null
            }
        )
    }
}

@Composable
fun EmptyStateView(modifier: Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.journal_empty_state),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
fun NoteItemCard(
    note: NotePreview,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = note.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha
            Text(
                text = formatTimestamp(note.timestamp),
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )


        }
    }
}

@Composable
fun AddNoteForm(
    titleValue: String,
    contentValue: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color(0xFFF5F5F5),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.journal_add_note),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = titleValue,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.journal_note_title_hint)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))


            OutlinedTextField(
                value = contentValue,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text(stringResource(R.string.journal_note_content_hint)) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                maxLines = 8
            )

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9E9E9E)
                    )
                ) {
                    Text(stringResource(R.string.journal_cancel_button))
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E2A78)
                    )
                ) {
                    Text(stringResource(R.string.journal_save_button))
                }
            }
        }
    }
}

@Composable
fun ConfirmViewDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFF1E2A78)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.journal_confirm_title),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(stringResource(R.string.journal_confirm_message))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.journal_confirm_yes),
                    color = Color(0xFF1E2A78)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.journal_confirm_no),
                    color = Color(0xFF757575)
                )
            }
        }
    )
}

@Composable
fun NoteDetailDialog(
    note: NotePreview,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(note.timestamp),
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
            }
        },
        text = {
            Text(
                text = note.content,
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.journal_detail_close),
                    color = Color(0xFF1E2A78)
                )
            }
        }
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

