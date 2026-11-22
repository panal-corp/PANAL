package com.panal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.panal.ui.theme.PanalError
import com.panal.ui.theme.PanalErrorSoft
import com.panal.ui.theme.PanalGold
import com.panal.ui.theme.PanalInput
import com.panal.ui.theme.PanalOutline

/* ============================================================
   PanalFields.kt
   Campos y mensajes compartidos por Login y Registro.
   Equivale a los estilos FieldLabel / FieldBorder / FormEntry
   y al MessagePanel de .NET.
   ============================================================ */

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.padding(bottom = 7.dp)
    )
}

@Composable
fun PanalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    maxLength: Int? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            /* MaxLength del Entry de .NET: se recorta al escribir. */
            onValueChange(if (maxLength != null) newValue.take(maxLength) else newValue)
        },
        placeholder = { Text(placeholder) },
        singleLine = true,
        enabled = enabled,
        /* El nombre de usuario NO es un correo: teclado de texto,
           sin predicción, como en LoginPage.xaml. Tu primer panel
           usaba KeyboardType.Email y por eso salía el teclado de
           correo con @ y .com. */
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PanalPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    maxLength: Int? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            onValueChange(if (maxLength != null) newValue.take(maxLength) else newValue)
        },
        placeholder = { Text(placeholder) },
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions,
        trailingIcon = {
            TextButton(
                onClick = { visible = !visible },
                enabled = enabled
            ) {
                Text(if (visible) "Ocultar" else "Mostrar")
            }
        },
        shape = RoundedCornerShape(14.dp),
        colors = fieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = PanalInput,
    unfocusedContainerColor = PanalInput,
    disabledContainerColor = PanalInput,
    focusedBorderColor = PanalGold,
    unfocusedBorderColor = PanalOutline,
    disabledBorderColor = PanalOutline,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
)

/* Panel de mensaje dentro de la pantalla (MessagePanel en .NET).
   isError = true  -> aviso en rojo suave.
   isError = false -> confirmación en tono de marca. */
@Composable
fun MessagePanel(message: String, isError: Boolean, modifier: Modifier = Modifier) {
    val background = if (isError) PanalErrorSoft else MaterialTheme.colorScheme.primaryContainer
    val content = if (isError) PanalError else MaterialTheme.colorScheme.onPrimaryContainer
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(14.dp))
            .border(1.dp, content.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = if (isError) "⚠  $message" else "✓  $message",
            style = MaterialTheme.typography.bodyMedium,
            color = content
        )
    }
}

/* Encabezado de marca que comparten Login y Registro:
   símbolo de cuatro celdas + nombre + lema. */
@Composable
fun BrandHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        PanalMark(modifier = Modifier.padding(top = 20.dp).size(167.dp))
        Column {
            Text(
                text = "Panal",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 67.dp)
                // fontSize = 20.s.dp
            )
            Text(
                text = "Cada pieza en su lugar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
