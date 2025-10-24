package com.uvg.rachapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.rachapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.loginUser(email, password)
                if (result.isSuccess && result.getOrNull() != null) {
                    _authState.value = AuthState.Success("Login exitoso")
                } else {
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error en login")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun register(email: String, password: String, name: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.registerUser(email, password, name, phone)
                if (result.isSuccess && result.getOrNull() != null) {
                    _authState.value = AuthState.Success("Registro exitoso")
                } else {
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error en registro")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun registerWithFacebook(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.registerWithFacebook(email, password)
                if (result.isSuccess && result.getOrNull() != null) {
                    _authState.value = AuthState.Success("Registro con Facebook exitoso")
                } else {
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error en registro Facebook")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun registerWithGmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.registerWithGmail(email, password)
                if (result.isSuccess && result.getOrNull() != null) {
                    _authState.value = AuthState.Success("Registro con Gmail exitoso")
                } else {
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error en registro Gmail")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}