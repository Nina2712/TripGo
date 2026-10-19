package com.example.proyectofinal.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectofinal.data.AuthRepository
import com.example.proyectofinal.ui.model.AuthUiState
import com.facebook.AccessToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(
        AuthUiState(
            user = repository.getCurrentUser()
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (!validate(email, password)) return

        _uiState.value =
            _uiState.value.copy(
                loading = true,
                message = ""
            )

        repository.register(
            email,
            password
        ) { success, message, user ->

            _uiState.value =
                _uiState.value.copy(
                    loading = false,
                    message = message,
                    user = user
                )

            if (success) {
                onSuccess()
            }

        }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        if (!validate(email, password)) return

        _uiState.value =
            _uiState.value.copy(
                loading = true,
                message = ""
            )

        repository.login(
            email,
            password
        ) { success, message, user ->

            _uiState.value =
                _uiState.value.copy(
                    loading = false,
                    message = message,
                    user = user
                )

            if (success) {
                onSuccess()
            }

        }
    }

    fun loginFacebook(
        token: AccessToken,
        onSuccess: () -> Unit
    ) {
        _uiState.value =
            _uiState.value.copy(
                loading = true,
                message = ""
            )

        repository.loginWithFacebook(
            token
        ) { success, message, user ->

            _uiState.value =
                _uiState.value.copy(
                    loading = false,
                    message = message,
                    user = user
                )

            if (success) {
                onSuccess()
            }

        }
    }

    fun logout() {
        repository.logout()

        _uiState.value =
            AuthUiState(
                user = null,
                message = "Sesión cerrada",
                loading = false
            )
    }

    fun setMessage(message: String) {
        _uiState.value =
            _uiState.value.copy(
                message = message,
                loading = false
            )
    }

    private fun validate(
        email: String,
        password: String
    ): Boolean {

        if (email.isBlank()) {
            setMessage("Ingrese un correo")
            return false
        }

        if (password.isBlank()) {
            setMessage("Ingrese una contraseña")
            return false
        }

        if (password.length < 6) {
            setMessage("La contraseña debe tener mínimo 6 caracteres")
            return false
        }

        return true
    }
}
