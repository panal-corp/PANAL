package com.panal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/* ============================================================
   ComingSoonScreen.kt
   Pantalla temporal para las secciones que aún no se migran.
   Existe para que TODOS los botones del menú principal ya
   naveguen a algún lugar y la app no se rompa mientras se
   transcribe cada página. Cada una se reemplaza por su
   pantalla real (WorkScreen, RecordsScreen, etc.) sin tocar
   el menú: solo cambia el destino en AppNavigation.
   ============================================================ */
@Composable
fun ComingSoonScreen(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("←  Volver al panel")
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 24.dp)
            )
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.padding(top = 22.dp).fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(
                        text = "En migración",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}
