package com.example.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.security.SecureTokenStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isPinCreated: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val secureTokenStore: SecureTokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(
        AuthUiState(
            isAuthenticated = secureTokenStore.readToken() != null,
            isPinCreated = secureTokenStore.readPinHash() != null
        )
    )
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun submitPin(pin: String) {
        viewModelScope.launch {
            if (!isPinValid(pin)) {
                _state.value = _state.value.copy(errorMessage = "PIN must have 4 digits")
                return@launch
            }

            val hash = hash(pin)
            val currentHash = secureTokenStore.readPinHash()

            if (currentHash == null) {
                secureTokenStore.savePinHash(hash)
                authorizeSession()
                _events.emit(AuthEvent.PinCreated)
                return@launch
            }

            if (currentHash == hash) {
                authorizeSession()
                _events.emit(AuthEvent.Authenticated)
            } else {
                _state.value = _state.value.copy(errorMessage = "Invalid PIN")
            }
        }
    }

    fun onBiometricAuthenticated() {
        viewModelScope.launch {
            authorizeSession()
            _events.emit(AuthEvent.Authenticated)
        }
    }

    private fun authorizeSession() {
        secureTokenStore.saveToken("valid-session")
        _state.value = AuthUiState(isAuthenticated = true, isPinCreated = true)
    }

    private fun isPinValid(pin: String): Boolean = pin.length == 4 && pin.all { it.isDigit() }

    private fun hash(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

sealed interface AuthEvent {
    data object PinCreated : AuthEvent
    data object Authenticated : AuthEvent
}
