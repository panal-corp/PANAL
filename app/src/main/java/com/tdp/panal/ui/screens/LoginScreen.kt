package com.tdp.panal.ui.screens

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdp.panal.data.repository.AuthRepository
import com.tdp.panal.ui.components.BrandHeader
import com.tdp.panal.ui.components.FieldLabel
import com.tdp.panal.ui.components.MessagePanel
import com.tdp.panal.ui.components.PanalPasswordField
import com.tdp.panal.ui.components.PanalTextField
import com.tdp.panal.ui.theme.PanalGoldText
import com.tdp.panal.ui.theme.PanalTheme
import com.tdp.panal.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/* Primera etapa: autenticación con usuario y contraseña. */
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    initialUsername: String = "",
    successMessage: String? = null,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    /* rememberSaveable: el usuario escrito sobrevive al ir y
       volver del registro, como la página de MAUI en la pila. */
    var username by rememberSaveable(initialUsername) { mutableStateOf(initialUsername) }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var notice by remember(successMessage) { mutableStateOf(successMessage) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }

    /* Al volver del registro, el usuario ya viene escrito
       (OnRegistrationCompleted en .NET). */
    LaunchedEffect(initialUsername) {
        if (initialUsername.isNotBlank()) username = initialUsername
    }

    fun attemptLogin() {
        if (isLoading) return
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Escribe tu usuario y tu contraseña para continuar."
            notice = null
            return
        }
        isLoading = true
        errorMessage = null
        notice = null
        scope.launch {
            try {
                val session = authViewModel.login(username, password)
                if (!session) {
                    errorMessage =
                        "El usuario o la contraseña son incorrectos."
                } else {
                    /* Las claves no se quedan escritas en pantalla. */
                    password = ""
                    onLoginSuccess()
                }
            } catch (exception: Exception) {
                errorMessage = "No fue posible iniciar sesión. Inténtalo de nuevo."
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 510.dp).fillMaxWidth()) {
            BrandHeader()

            Text(
                text = "TU TIENDA, EN ORDEN",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 40.dp, bottom = 10.dp)
            )
            Text(
                text = "Tu tienda,\nen orden.",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Inicia sesión para continuar en Panal.",
                style = MaterialTheme.typography.bodyLarge,
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
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ingresa tus datos para continuar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 5.dp)
                    )

                    FieldLabel("Nombre de usuario", Modifier.padding(top = 22.dp))
                    PanalTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "Escribe tu usuario",
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
                        placeholder = "Escribe tu contraseña",
                        enabled = !isLoading,
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            attemptLogin()
                        }),
                        modifier = Modifier.focusRequester(passwordFocus)
                    )

                    if (errorMessage != null) {
                        MessagePanel(
                            message = errorMessage!!,
                            isError = true,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else if (notice != null) {
                        MessagePanel(
                            message = notice!!,
                            isError = false,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    Button(
                        onClick = { attemptLogin() },
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
                        Text(if (isLoading) "Un momento..." else "Iniciar sesión  →")
                    }

                    TextButton(
                        onClick = {
                            password = ""
                            onNavigateToRegister()
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("¿No tienes cuenta?  Registrar cuenta")
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

@Preview(showBackground = true, backgroundColor = 0xFF0C0C0C)
@Composable
private fun LoginScreenPreview() {
    /* La vista previa no toca la base de datos: solo dibuja. */
    PanalTheme {
        Text("LoginScreen requiere AuthViewModel; usa la app para verla completa.")
    }
}
