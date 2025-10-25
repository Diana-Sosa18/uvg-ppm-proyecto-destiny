package com.uvg.rachapp.ui.screens.authentication.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.uvg.rachapp.ui.screens.authentication.RoundedButton
import com.uvg.rachapp.ui.screens.authentication.components.TransparentTextField
import com.uvg.rachapp.viewModels.AuthViewModel
import com.uvg.rachapp.viewModels.AuthState
import kotlinx.coroutines.delay

// Colores locales
private val FACEBOOK_COLOR = Color(0xFF1877F2)
private val GMAIL_COLOR = Color(0xFFDB4437)

@Composable
fun RegisterScreen(navController: NavHostController) {
    println("🟢 [UI] RegisterScreen composed")

    val nameValue = remember { mutableStateOf("") }
    val emailValue = remember { mutableStateOf("") }
    val phoneValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    val confirmPasswordValue = remember { mutableStateOf("") }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    println("🟢 [UI] Current authState: $authState")

    // Función para validar formato de email
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }

    // Función para validar teléfono
    fun isValidPhone(phone: String): Boolean {
        return phone.length == 8 && phone.all { it.isDigit() }
    }

    // Función de validación principal
    fun validateForm(): Boolean {
        var isValid = true

        if (nameValue.value.isEmpty()) {
            nameError = "El nombre es requerido"
            isValid = false
        } else if (nameValue.value.length < 2) {
            nameError = "Mínimo 2 caracteres"
            isValid = false
        } else {
            nameError = ""
        }

        if (emailValue.value.isEmpty()) {
            emailError = "El email es requerido"
            isValid = false
        } else if (!isValidEmail(emailValue.value)) {
            emailError = "Ingresa un email válido"
            isValid = false
        } else {
            emailError = ""
        }

        if (phoneValue.value.isEmpty()) {
            phoneError = "El teléfono es requerido"
            isValid = false
        } else if (!isValidPhone(phoneValue.value)) {
            phoneError = "Debe tener 8 dígitos"
            isValid = false
        } else {
            phoneError = ""
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

        if (confirmPasswordValue.value.isEmpty()) {
            confirmPasswordError = "Confirma tu contraseña"
            isValid = false
        } else if (passwordValue.value != confirmPasswordValue.value) {
            confirmPasswordError = "Las contraseñas no coinciden"
            isValid = false
        } else {
            confirmPasswordError = ""
        }

        return isValid
    }

    // Manejar éxito del registro
    LaunchedEffect(authState) {
        println("🟢 [UI] LaunchedEffect triggered - authState: $authState")

        if (authState is AuthState.Success) {
            println("🟢 [UI] SUCCESS detected - navigating back in 1.5s")
            delay(1500)
            println("🟢 [UI] Navigating back to login")
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        println("🟢 [UI] Back button clicked")
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
                    text = "Crear una cuenta",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TransparentTextField(
                    textFieldValue = nameValue,
                    textLabel = "Nombre",
                    keyboardType = KeyboardType.Text,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    imeAction = ImeAction.Next
                )
                if (nameError.isNotEmpty()) {
                    Text(
                        text = nameError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                TransparentTextField(
                    textFieldValue = emailValue,
                    textLabel = "Email",
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
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                TransparentTextField(
                    textFieldValue = phoneValue,
                    textLabel = "Número de teléfono",
                    maxChar = 8,
                    keyboardType = KeyboardType.Phone,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    imeAction = ImeAction.Next
                )
                if (phoneError.isNotEmpty()) {
                    Text(
                        text = phoneError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                TransparentTextField(
                    textFieldValue = passwordValue,
                    textLabel = "Contraseña",
                    keyboardType = KeyboardType.Password,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    imeAction = ImeAction.Next,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisibility = !passwordVisibility
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = if (passwordVisibility) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
                )
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                TransparentTextField(
                    textFieldValue = confirmPasswordValue,
                    textLabel = "Confirmar Contraseña",
                    keyboardType = KeyboardType.Password,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (validateForm()) {
                                println("🟢 [UI] Register button clicked (from keyboard)")
                                authViewModel.register(
                                    emailValue.value,
                                    passwordValue.value,
                                    nameValue.value,
                                    phoneValue.value
                                )
                            }
                        }
                    ),
                    imeAction = ImeAction.Done,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                confirmPasswordVisibility = !confirmPasswordVisibility
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = if (confirmPasswordVisibility) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation()
                )
                if (confirmPasswordError.isNotEmpty()) {
                    Text(
                        text = confirmPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                RoundedButton(
                    text = if (authState is AuthState.Loading) "Registrando..." else "Registrarse",
                    displayProgressBar = authState is AuthState.Loading,
                    onClick = {
                        println("🟢 [UI] Register button clicked")
                        focusManager.clearFocus()
                        if (validateForm()) {
                            println("🟢 [UI] Form is valid - calling ViewModel")
                            authViewModel.register(
                                emailValue.value,
                                passwordValue.value,
                                nameValue.value,
                                phoneValue.value
                            )
                        } else {
                            println("🔴 [UI] Form validation failed")
                        }
                    }
                )

                ClickableText(
                    text = buildAnnotatedString {
                        append("¿Ya tienes una cuenta? ")

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Iniciar Sesión")
                        }
                    },
                    onClick = {
                        println("🟢 [UI] Login text clicked")
                        navController.navigate("login")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Divider(
                        modifier = Modifier.width(24.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )

                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "O",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black
                        )
                    )

                    Divider(
                        modifier = Modifier.width(24.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Registrarse con",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SocialButton(
                    text = "Registrarse con Facebook",
                    onClick = {
                        println("🟢 [UI] Facebook button clicked")
                        navController.navigate("facebook_register")
                    },
                    buttonColor = FACEBOOK_COLOR
                )

                SocialButton(
                    text = "Registrarse con Gmail",
                    onClick = {
                        println("🟢 [UI] Gmail button clicked")
                        navController.navigate("gmail_register")
                    },
                    buttonColor = GMAIL_COLOR
                )
            }
        }
    }
}

@Composable
fun SocialButton(
    text: String,
    onClick: () -> Unit,
    buttonColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}