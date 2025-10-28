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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.uvg.rachapp.R
import com.uvg.rachapp.ui.screens.authentication.components.TransparentTextField
import com.uvg.rachapp.viewModels.AuthViewModel
import com.uvg.rachapp.viewModels.AuthState

@Composable
fun FacebookRegisterScreen(navController: NavHostController) {
    val context = LocalContext.current

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

    fun isValidPhone(phone: String): Boolean {
        return phone.length == 8 && phone.all { it.isDigit() }
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (emailValue.value.isEmpty()) {
            emailError = context.getString(R.string.error_email_phone_required)
            isValid = false
        } else if (!isValidEmail(emailValue.value) && !isValidPhone(emailValue.value)) {
            emailError = context.getString(R.string.error_email_phone_invalid)
            isValid = false
        } else {
            emailError = ""
        }

        if (passwordValue.value.isEmpty()) {
            passwordError = context.getString(R.string.error_password_required_facebook)
            isValid = false
        } else if (passwordValue.value.length < 6) {
            passwordError = context.getString(R.string.error_password_min_length_facebook)
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
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            Text(
                text = stringResource(R.string.facebook_registration_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1877F2)
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

        TransparentTextField(
            textFieldValue = emailValue,
            textLabel = stringResource(R.string.facebook_email_or_phone),
            keyboardType = KeyboardType.Text,
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
            textLabel = stringResource(R.string.facebook_password),
            keyboardType = KeyboardType.Password,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (validateForm()) {
                        authViewModel.registerWithFacebook(emailValue.value, passwordValue.value)
                    }
                }
            ),
            imeAction = ImeAction.Done,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = stringResource(R.string.toggle_password)
                    )
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
                    authViewModel.registerWithFacebook(emailValue.value, passwordValue.value)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
        ) {
            Text(
                text = if (authState is AuthState.Loading)
                    stringResource(R.string.registering_facebook)
                else
                    stringResource(R.string.register_with_facebook_button),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.facebook_terms),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}