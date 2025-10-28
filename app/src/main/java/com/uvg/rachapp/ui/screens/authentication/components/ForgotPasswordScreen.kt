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
import com.uvg.rachapp.ui.screens.authentication.RoundedButton
import kotlinx.coroutines.delay
import com.uvg.rachapp.R




@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    val emailValue = remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val errorEmailRequired = stringResource(R.string.error_email_required_forgot)
    val errorEmailInvalid = stringResource(R.string.error_email_invalid_forgot)

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return email.matches(emailRegex.toRegex())
    }


    fun validateForm(
        errorEmailRequired: String,
        errorEmailInvalid: String
    ): Boolean {
        return if (emailValue.value.isEmpty()) {
            emailError = errorEmailRequired
            false
        } else if (!isValidEmail(emailValue.value)) {
            emailError = errorEmailInvalid
            false
        } else {
            emailError = ""
            true
        }
    }

    fun sendRecoveryEmail() {
        isLoading = true
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(2000)
            isLoading = false
            showSuccessMessage = true
            delay(2500)
            //navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        if (validateForm(errorEmailRequired, errorEmailInvalid)) {
                            sendRecoveryEmail()
                        }
                    }
                ),
                imeAction = ImeAction.Done
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (showSuccessMessage) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.forgot_password_success),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        RoundedButton(
            text = if (isLoading) stringResource(R.string.forgot_password_sending) else stringResource(R.string.forgot_password_button),
            displayProgressBar = isLoading,
            onClick = {
                focusManager.clearFocus()
                if (validateForm(errorEmailRequired, errorEmailInvalid)) {
                    sendRecoveryEmail()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

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