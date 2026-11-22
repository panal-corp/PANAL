package com.panal.domain

/* ============================================================
   SessionState.kt
   Sesión activa dentro de la app.
   En .NET estos datos viajan en el constructor de MainPage
   (username + role); en Kotlin viven en el AuthViewModel y
   las pantallas los leen de ahí.
   ============================================================ */
data class SessionState(
    val userId: Int,
    val username: String,
    val role: String
)
