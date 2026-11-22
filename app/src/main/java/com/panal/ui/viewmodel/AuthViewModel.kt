package com.panal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panal.data.repository.AuthRepository
import com.panal.data.repository.RegistrationResult
import com.panal.domain.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/* ============================================================
   AuthViewModel.kt
   Guarda la sesión activa para que todas las pantallas sepan
   quién entró y con qué rol. En .NET ese dato viajaba en el
   constructor de cada página; aquí vive en un solo lugar.
   ============================================================ */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _session = MutableStateFlow<SessionState?>(null)
    val session: StateFlow<SessionState?> = _session.asStateFlow()

    suspend fun login(
        username: String,
        password: String,
        roleKey: String
    ): SessionState? {
        val authenticated = authRepository.authenticate(username, password, roleKey)
        if (authenticated != null) {
            _session.value = authenticated
        }
        return authenticated
    }

    suspend fun register(
        username: String,
        password: String,
        confirmation: String
    ): RegistrationResult = authRepository.register(username, password, confirmation)

    /* Cerrar sesión: la sesión se olvida y la navegación
       regresa al acceso (AppNavigation se encarga de la ruta). */
    fun logout() {
        _session.value = null
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                "ViewModel desconocido: ${modelClass.name}"
            }
            return AuthViewModel(authRepository) as T
        }
    }
}
