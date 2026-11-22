package com.panal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.panal.data.repository.AuthRepository
import com.panal.data.repository.RegistrationStatus
import com.panal.ui.components.FieldLabel
import com.panal.ui.components.MessagePanel
import com.panal.ui.components.PanalPasswordField
import com.panal.ui.components.PanalTextField
import com.panal.ui.theme.PanalGoldText
import com.panal.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/* ============================================================
   RegisterScreen.kt
   Transcripción de RegisterPage.xaml / RegisterPage.xaml.cs.
   Registro público: la primera cuenta del dispositivo será
   Administrador y las siguientes Cliente (AuthRepository).
   Al crear la cuenta regresa al login con el usuario escrito
   y el aviso "Cuenta creada".
   ============================================================ */
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onRegisterSuccess: (username: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }
    val confirmationFocus = remember { FocusRequester() }

    fun attemptRegister() {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val result = authViewModel.register(username, password, confirmation)
                when (result.status) {
                    RegistrationStatus.SUCCESS -> {
                        password = ""
                        confirmation = ""
                        onRegisterSuccess(result.username ?: username.trim())
                    }
                    RegistrationStatus.MISSING_USERNAME ->
                        errorMessage = "Escribe un nombre de usuario."
                    RegistrationStatus.USERNAME_TOO_LONG ->
                        errorMessage = "El usuario puede tener hasta 50 caracteres."
                    RegistrationStatus.MISSING_PASSWORD ->
                        errorMessage = "Escribe y confirma tu contraseña."
                    RegistrationStatus.PASSWORDS_DO_NOT_MATCH ->
                        errorMessage = "Las contraseñas no coinciden. Inténtalo de nuevo."
                    RegistrationStatus.USERNAME_TAKEN ->
                        errorMessage = "Ese nombre de usuario ya está registrado."
                }
            } catch (exception: Exception) {
                errorMessage = "No fue posible guardar la cuenta. Inténtalo de nuevo."
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 510.dp).fillMaxWidth()) {
            TextButton(
                onClick = onBack,
                enabled = !isLoading,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("←  Volver al acceso")
            }

            Text(
                text = "PANAL  /  NUEVA CUENTA",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
            )
            Text(
                text = "Crea tu cuenta.",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Unos datos y todo estará listo para ti.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(top = 26.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "Tus datos",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "La primera cuenta del dispositivo será de administración; " +
                            "las siguientes serán de cliente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 5.dp)
                    )

                    FieldLabel("Nombre de usuario", Modifier.padding(top = 22.dp))
                    PanalTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Elige tu usuario",
                        enabled = !isLoading,
                        maxLength = AuthRepository.MAX_USERNAME_LENGTH,
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onNext = { passwordFocus.requestFocus() }
                        )
                    )

                    FieldLabel("Contraseña", Modifier.padding(top = 16.dp))
                    PanalPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Crea una contraseña",
                        enabled = !isLoading,
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onNext = { confirmationFocus.requestFocus() }
                        ),
                        modifier = Modifier.focusRequester(passwordFocus)
                    )

                    FieldLabel("Repetir contraseña", Modifier.padding(top = 16.dp))
                    PanalPasswordField(
                        value = confirmation,
                        onValueChange = { confirmation = it },
                        placeholder = "Repítela para confirmar",
                        enabled = !isLoading,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                attemptRegister()
                            }
                        ),
                        modifier = Modifier.focusRequester(confirmationFocus)
                    )
                    Text(
                        text = "Las dos contraseñas deben coincidir.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (errorMessage != null) {
                        MessagePanel(
                            message = errorMessage!!,
                            isError = true,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    Button(
                        onClick = { attemptRegister() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = PanalGoldText
                        ),
                        modifier = Modifier
                            .padding(top = 22.dp)
                            .fillMaxWidth()
                    ) {
                        Text(if (isLoading) "Un momento..." else "Crear cuenta  →")
                    }
                }
            }

            Text(
                text = "Tu cuenta se guarda en este dispositivo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = 22.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
