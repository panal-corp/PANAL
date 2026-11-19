package com.tdp.panal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tdp.panal.data.repository.AuthRepository
import com.tdp.panal.data.repository.RegistrationResult
import com.tdp.panal.domain.SessionState
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

    private var pending: SessionState? = null
    val pendingRole: String? get() = pending?.role

    // Devuelve false si las credenciales son incorrectas; no crea sesión privilegiada.
    suspend fun login(username: String, password: String): Boolean {
        pending = null
        val authenticated = authRepository.authenticatePassword(username, password) ?: return false
        if (authenticated.role == com.tdp.panal.domain.AccessRoles.CUSTOMER) {
            _session.value = authenticated
        } else {
            pending = authenticated
        }
        return true
    }

    fun enterAsCustomer(): Boolean {
        val candidate = pending ?: return false
        _session.value = candidate.copy(role = com.tdp.panal.domain.AccessRoles.CUSTOMER)
        pending = null
        return true
    }

    suspend fun enterWithKey(key: String): Boolean {
        val candidate = pending ?: return false
        val authenticated = authRepository.verifyRoleKey(candidate.userId, key) ?: return false
        if (authenticated.role != candidate.role) return false
        _session.value = authenticated
        pending = null
        return true
    }

    fun cancelPending() { pending = null }
    suspend fun accounts() = authRepository.accounts(requireNotNull(_session.value))
    suspend fun assignRole(id: Int, role: String) = authRepository.assignRole(requireNotNull(_session.value), id, role)
    suspend fun issueKey(id: Int) = authRepository.issueKey(requireNotNull(_session.value), id)
    suspend fun revokeKey(id: Int) = authRepository.revokeKey(requireNotNull(_session.value), id)

    suspend fun register(
        username: String,
        password: String,
        confirmation: String
    ): RegistrationResult = authRepository.register(username, password, confirmation)

    /* Cerrar sesión: la sesión se olvida y la navegación
       regresa al acceso (AppNavigation se encarga de la ruta). */
    fun logout() {
        _session.value = null
        pending = null
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
