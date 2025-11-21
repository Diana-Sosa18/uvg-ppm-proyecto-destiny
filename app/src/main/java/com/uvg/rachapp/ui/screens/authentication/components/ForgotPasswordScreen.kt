package com.uvg.rachapp.ui.screens.authentication.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.screens.authentication.RoundedButton
import com.uvg.rachapp.viewModels.AuthViewModel
import com.uvg.rachapp.viewModels.AuthState


@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val emailValue = rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Observar el estado de autenticación
    val authState by authViewModel.authState.collectAsState()

    // Strings de recursos
    val errorEmailRequired = stringResource(R.string.error_email_required_forgot)
    val errorEmailInvalid = stringResource(R.string.error_email_invalid_forgot)

    // Función de validación de email
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    // Función de validación del formulario
    fun validateForm(): Boolean {
        return when {
            emailValue.value.isEmpty() -> {
                emailError = errorEmailRequired
                false
            }
            !isValidEmail(emailValue.value) -> {
                emailError = errorEmailInvalid
                false
            }
            else -> {
                emailError = ""
                true
            }
        }
    }

    // Limpiar el estado cuando se sale de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con botón de regreso
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = stringResource(R.string.forgot_password_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = stringResource(R.string.forgot_password_description),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Gray,
                fontSize = 14.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de email
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TransparentTextField(
                textFieldValue = emailValue,
                textLabel = stringResource(R.string.forgot_password_email_label),
                keyboardType = KeyboardType.Email,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (validateForm()) {
                            authViewModel.sendPasswordResetEmail(emailValue.value.trim())
                        }
                    }
                ),
                imeAction = ImeAction.Done
            )

            // Mostrar error de email
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensajes según el estado
        when (val state = authState) {
            is AuthState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            is AuthState.Success -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is AuthState.Error -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> { /* Idle state */ }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.weight(1f))

        // Botón de enviar
        RoundedButton(
            text = if (authState is AuthState.Loading)
                stringResource(R.string.forgot_password_sending)
            else
                stringResource(R.string.forgot_password_button),
            displayProgressBar = authState is AuthState.Loading,
            onClick = {
                if (authState !is AuthState.Loading) {
                    focusManager.clearFocus()
                    if (validateForm()) {
                        authViewModel.sendPasswordResetEmail(emailValue.value.trim())
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver al login
        Text(
            text = stringResource(R.string.forgot_password_back_to_login),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            modifier = Modifier.clickable {
                navController.popBackStack()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}