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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.rachapp.ui.screens.authentication.components.TransparentTextField
import com.uvg.rachapp.viewModels.AuthViewModel
import com.uvg.rachapp.viewModels.AuthState

@Composable
fun GmailRegisterScreen(navController: NavHostController) {
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }


    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    fun isGmailEmail(email: String): Boolean {
        return email.contains("@gmail.com") || email.contains("@googlemail.com")
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (emailValue.value.isEmpty()) {
            emailError = "El email es requerido"
            isValid = false
        } else if (!isValidEmail(emailValue.value)) {
            emailError = "Ingresa un email válido"
            isValid = false
        } else if (!isGmailEmail(emailValue.value)) {
            emailError = "Debe ser un email de Gmail (@gmail.com)"
            isValid = false
        } else {
            emailError = ""
        }

        if (passwordValue.value.isEmpty()) {
            passwordError = "La contraseña es requerida"
            isValid = false
        } else if (passwordValue.value.length < 6) {
            passwordError = "Mínimo 6 caracteres"
            isValid = false
        } else {
            passwordError = ""
        }

        return isValid
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.popBackStack() // Volver al registro principal
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Registro con Gmail",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFDB4437)
            )
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "G",
            style = MaterialTheme.typography.displayLarge,
            color = Color(0xFFDB4437)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TransparentTextField(
            textFieldValue = emailValue,
            textLabel = "Email de Gmail",
            keyboardType = KeyboardType.Email,
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            imeAction = ImeAction.Next
        )

        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TransparentTextField(
            textFieldValue = passwordValue,
            textLabel = "Contraseña de Gmail",
            keyboardType = KeyboardType.Password,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (validateForm()) {
                        authViewModel.registerWithGmail(emailValue.value, passwordValue.value)
                    }
                }
            ),
            imeAction = ImeAction.Done,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(Icons.Default.Lock, contentDescription = "Toggle Password")
                }
            }
        )

        if (passwordError.isNotEmpty()) {
            Text(
                text = passwordError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                if (validateForm()) {
                    authViewModel.registerWithGmail(emailValue.value, passwordValue.value)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437))
        ) {
            Text(
                text = if (authState is AuthState.Loading) "Registrando..." else "Registrarse con Gmail",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Este registro usa la autenticación de Google",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}