package com.uvg.rachapp.ui.screens.authentication.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.uvg.rachapp.ui.screens.authentication.components.TransparentTextField

@Composable
fun FacebookRegisterScreen(navController: NavHostController) {
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Registro con Facebook",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1877F2)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Formulario simple
        TransparentTextField(
            textFieldValue = emailValue,
            textLabel = "Email o teléfono",
            keyboardType = KeyboardType.Email,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        TransparentTextField(
            textFieldValue = passwordValue,
            textLabel = "Contraseña",
            keyboardType = KeyboardType.Password,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            imeAction = ImeAction.Done,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(Icons.Default.Lock, contentDescription = "Toggle Password")
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de registro
        Button(
            onClick = {
                // TODO: Lógica de registro con Facebook
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
        ) {
            Text("Registrarse con Facebook", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Al registrarte, aceptas nuestros Términos y Política de Privacidad",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}