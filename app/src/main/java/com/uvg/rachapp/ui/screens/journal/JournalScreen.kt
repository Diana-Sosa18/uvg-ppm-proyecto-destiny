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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.viewModels.JournalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.uvg.rachapp.data.repository.NotePreview


@Composable
fun JournalScreen( journalViewModel: JournalViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        journalViewModel.checkOrCreatePinOnFirstOpen()
    }

    val notes by journalViewModel.notesState.collectAsState()
    val titleInput = journalViewModel.titleInput
    val contentInput = journalViewModel.contentInput



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


            if (notes.isEmpty()) {
                EmptyStateView(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { note ->
                        NoteItemCard(
                            note = note,
                            onClick = {
                                journalViewModel.selectNoteForConfirm(note)
                            }
                        )
                    }
                }
            }


            Button(
                onClick = { journalViewModel.openAddNoteForm() },
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
            visible = journalViewModel.showAddNoteForm,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AddNoteForm(
                titleValue = titleInput,
                contentValue = contentInput,

                onTitleChange = { journalViewModel.onTitleChange(it) },
                onContentChange = { journalViewModel.onContentChange(it) },
                onSave = { journalViewModel.addNoteFromInputs() },
                onCancel = { journalViewModel.closeAddNoteForm() }
            )
        }

    }


    if (journalViewModel.showConfirmDialog) {
        ConfirmViewDialog(
            onConfirm = { journalViewModel.confirmViewSelectedNote() },
            onDismiss = { journalViewModel.dismissConfirm() }
        )
    }

    if (journalViewModel.showCreatePinDialog) {
        var pin by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { },
            title = { Text(text = stringResource(R.string.create_pin_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { pin = it },
                        label = { Text(stringResource(R.string.create_pin_label_pin)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        label = { Text(stringResource(R.string.create_pin_label_confirm)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (journalViewModel.pinErrorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = journalViewModel.pinErrorMessage!!, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pin.isBlank()) {
                        // no action here, VM will handle validation if needed
                    } else if (pin != confirm) {
                        // no action here, VM will handle validation if needed
                    } else {
                        journalViewModel.saveNewPin(pin)
                    }
                }) {
                    Text(text = stringResource(R.string.create_pin_save))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    journalViewModel.dismissCreatePinDialog()  })
                {
                    Text(text = stringResource(R.string.create_pin_cancel))
                }
            }
        )
    }


    if (journalViewModel.showEnterPinDialog) {
        var entered by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { journalViewModel.dismissEnterPinDialog()  },
            title = { Text(text = stringResource(R.string.enter_pin_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = entered,
                        onValueChange = { entered = it },
                        label = { Text(stringResource(R.string.enter_pin_label)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (journalViewModel.pinErrorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = journalViewModel.pinErrorMessage!!, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    journalViewModel.enterPinAndShow(entered)
                }) {
                    Text(text = stringResource(R.string.enter_pin_accept))
                }
            },
            dismissButton = {
                TextButton(onClick = { journalViewModel.dismissEnterPinDialog() }) {
                    Text(text = stringResource(R.string.enter_pin_cancel))
                }
            }
        )
    }

    if (journalViewModel.showNoteDetailDialog && journalViewModel.selectedNote != null) {
        NoteDetailDialog(
            note = journalViewModel.selectedNote!!,
            onDismiss = { journalViewModel.dismissNoteDetail() },
            onDelete = { note ->
                journalViewModel.deleteNote(note)
            }
        )
    }
}

@Composable
fun EmptyStateView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
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
                text = "Agregar nota",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titleValue,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Título") },
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
                placeholder = { Text("Escribe aquí tu nota...") },
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
                    Text("Cancelar")
                }

                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E2A78)
                    )
                ) {
                    Text("Guardar")
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
    onDismiss: () -> Unit,
    onDelete: (NotePreview) -> Unit
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
            Row {
                TextButton(onClick = {
                    onDelete(note)
                }) {
                    Text("Eliminar", color = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cerrar", color = Color(0xFF1E2A78))
                }
            }
        }
    )
}


private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

