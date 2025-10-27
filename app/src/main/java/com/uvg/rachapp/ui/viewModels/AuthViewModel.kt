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
        println("🟢 [VM] login() called - email: $email")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            println("🟢 [VM] State changed to Loading")
            try {
                println("🟢 [VM] Calling authRepository.loginUser()")
                val result = authRepository.loginUser(email, password)
                println("🟢 [VM] Repository returned - isSuccess: ${result.isSuccess}")
                if (result.isSuccess && result.getOrNull() != null) {
                    println("🟢 [VM] Setting state to SUCCESS")
                    _authState.value = AuthState.Success("Login exitoso")
                } else {
                    println("🔴 [VM] Login failed - error: ${result.exceptionOrNull()?.message}")
                    _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error en login")
                }
            } catch (e: Exception) {
                println("🔴 [VM] Exception in login: ${e.message}")
                _authState.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun register(email: String, password: String, name: String, phone: String) {
        println("🟢 [VM] register() called - email: $email, name: $name")
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            println("🟢 [VM] State changed to Loading")
            try {
                println("🟢 [VM] Calling authRepository.registerUser()")
                val result = authRepository.registerUser(email, password, name, phone)
                println("🟢 [VM] Repository returned - isSuccess: ${result.isSuccess}")
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    println("🟢 [VM] User from result: $user")
                    if (user != null) {
                        println("🟢 [VM] Setting state to SUCCESS")
                        _authState.value = AuthState.Success("Registro exitoso")
                    } else {
                        println("🔴 [VM] User is null - setting ERROR")
                        _authState.value = AuthState.Error("Usuario creado pero no se pudo obtener")
                    }
                } else {
                    val error = result.exceptionOrNull()
                    println("🔴 [VM] Result failed - error: ${error?.message}")
                    _authState.value = AuthState.Error("Error: ${error?.message}")
                }
            } catch (e: Exception) {
                println("🔴 [VM] Exception in register: ${e.message}")
                _authState.value = AuthState.Error("Error: ${e.message ?: "Error desconocido"}")
            }
        }
    }

    fun registerWithFacebook(email: String, password: String) {
        println("🟢 [VM] registerWithFacebook() called")
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
        println("🟢 [VM] registerWithGmail() called")
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