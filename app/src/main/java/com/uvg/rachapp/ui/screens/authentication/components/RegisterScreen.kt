package com.uvg.rachapp.ui.screens.authentication.components

import com.uvg.rachapp.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
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
import com.uvg.rachapp.R
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
    val context = LocalContext.current

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
            nameError = context.getString(R.string.error_name_required)
            isValid = false
        } else if (nameValue.value.length < 2) {
            nameError = context.getString(R.string.error_name_min_length)
            isValid = false
        } else {
            nameError = ""
        }

        if (emailValue.value.isEmpty()) {
            emailError = context.getString(R.string.error_email_required)
            isValid = false
        } else if (!isValidEmail(emailValue.value)) {
            emailError = context.getString(R.string.error_email_invalid)
            isValid = false
        } else {
            emailError = ""
        }

        if (phoneValue.value.isEmpty()) {
            phoneError = context.getString(R.string.error_phone_required)
            isValid = false
        } else if (!isValidPhone(phoneValue.value)) {
            phoneError = context.getString(R.string.error_phone_length)
            isValid = false
        } else {
            phoneError = ""
        }

        if (passwordValue.value.isEmpty()) {
            passwordError = context.getString(R.string.error_password_required)
            isValid = false
        } else if (passwordValue.value.length < 6) {
            passwordError = context.getString(R.string.error_password_min_length)
            isValid = false
        } else {
            passwordError = ""
        }

        if (confirmPasswordValue.value.isEmpty()) {
            confirmPasswordError = context.getString(R.string.error_confirm_password_required)
            isValid = false
        } else if (passwordValue.value != confirmPasswordValue.value) {
            confirmPasswordError = context.getString(R.string.error_passwords_not_match)
            isValid = false
        } else {
            confirmPasswordError = ""
        }

        return isValid
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            delay(1500)
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { navController.popBackStack() }
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_icon),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = stringResource(R.string.create_account),
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
                    textLabel = stringResource(R.string.name),
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
                    textLabel = stringResource(R.string.email),
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
                    textLabel = stringResource(R.string.phone_number),
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
                    textLabel = stringResource(R.string.password),
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
                                contentDescription = if (passwordVisibility) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
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
                    textLabel = stringResource(R.string.confirm_password),
                    keyboardType = KeyboardType.Password,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (validateForm()) {
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
                                contentDescription = if (confirmPasswordVisibility) stringResource(R.string.hide_password) else stringResource(R.string.show_password)
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
                    text = if (authState is AuthState.Loading) stringResource(R.string.registering) else stringResource(R.string.register),
                    displayProgressBar = authState is AuthState.Loading,
                    onClick = {
                        focusManager.clearFocus()
                        if (validateForm()) {
                            authViewModel.register(
                                emailValue.value,
                                passwordValue.value,
                                nameValue.value,
                                phoneValue.value
                            )
                        }
                    }
                )

                ClickableText(
                    text = buildAnnotatedString {
                        append("${stringResource(R.string.already_have_account)} ")

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(stringResource(R.string.sign_in))
                        }
                    },
                    onClick = {
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
                        text = stringResource(R.string.or),
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
                    text = stringResource(R.string.register_with),
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
                    text = stringResource(R.string.register_with_facebook),
                    onClick = {
                        navController.navigate("facebook_register")
                    },
                    buttonColor = FACEBOOK_COLOR
                )

                SocialButton(
                    text = stringResource(R.string.register_with_gmail),
                    onClick = {
                        navController.navigate("gmail_register")
                    },
                    buttonColor = GMAIL_COLOR
                )
            }
        }
    }
}

