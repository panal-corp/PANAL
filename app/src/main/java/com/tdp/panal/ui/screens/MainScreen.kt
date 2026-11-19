package com.tdp.panal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import com.tdp.panal.domain.AccessRoles
import com.tdp.panal.domain.SessionState
import com.tdp.panal.ui.components.PanalMark
import com.tdp.panal.ui.theme.PanalGoldText
import com.tdp.panal.ui.theme.PanalOutline

/* ============================================================
   MainScreen.kt  — MENÚ PRINCIPAL
   Conversión de MainPage.xaml / MainPage.xaml.cs de .NET.

   Qué se transpuso tal cual:
   - Encabezado con marca y botón "Salir".
   - "TU PANEL" + "Hola, {usuario}." + "Nos alegra tenerte...".
   - Tarjeta de rol: "TU ACCESO", nombre del rol y su descripción
     (AccessRoles.Describe de .NET).
   - Las seis tarjetas en orden 01..06, MOSTRANDO SOLO las que
     el rol puede abrir (ApplyRole en MainPage.xaml.cs):
       01 Inicio general        -> solo Administrador
       02 Mi perfil             -> todos
       03 Panel de trabajo      -> todos (el texto cambia por rol)
       04 Almacén y cuentas     -> solo Administrador
       05 Reportes              -> solo Administrador
       06 Trabajadores          -> solo Administrador
   - Contador "NN SECCIONES" y pie "PANAL · CADA PIEZA...".
   El TutorialView de .NET queda como aviso al final: ese video
   todavía debe regrabarse con los roles y el nuevo diseño.
   ============================================================ */

private data class SectionItem(
    val number: String,
    val eyebrow: String,
    val title: String,
    val hint: String,
    val onOpen: () -> Unit
)

@Composable
fun MainScreen(
    session: SessionState,
    onOpenGeneral: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenWork: () -> Unit,
    onOpenRecords: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenUsers: () -> Unit,
    onLogout: () -> Unit
) {
    val role = session.role

    /* El texto de la tarjeta de trabajo depende del rol,
       igual que WorkCardHint en ApplyRole(). */
    val workHint = when {
        AccessRoles.canManageSupply(role) -> "Ventas, abastecimiento y devoluciones."
        AccessRoles.usesOwnCustomerName(role) -> "Tus compras y tus devoluciones."
        AccessRoles.canReceiveCustomerReturns(role) -> "Ventas y devoluciones de clientes."
        else -> "Registro de ventas del mostrador."
    }

    /* Solo se agregan a la lista las secciones visibles:
       así el contador y el orden salen solos. */
    val sections = buildList {
        if (AccessRoles.canSeeOverview(role)) {
            add(
                SectionItem(
                    number = "01", eyebrow = "INICIO GENERAL",
                    title = "Comprobantes y efectos",
                    hint = "Cada operación con su efecto en almacén y cuentas.",
                    onOpen = onOpenGeneral
                )
            )
        }
        add(
            SectionItem(
                number = "02", eyebrow = "MI PERFIL",
                title = "Tu cuenta",
                hint = "Foto, preferencias e inicio automático.",
                onOpen = onOpenProfile
            )
        )
        if (AccessRoles.canUsePointOfSale(role)) {
            add(
                SectionItem(
                    number = "03", eyebrow = "TIENDA",
                    title = "Panel de trabajo",
                    hint = workHint,
                    onOpen = onOpenWork
                )
            )
        }
        if (AccessRoles.canSeeRecords(role)) {
            add(
                SectionItem(
                    number = "04", eyebrow = "REGISTRO",
                    title = "Almacén y cuentas",
                    hint = "Entradas, salidas y movimientos de dinero.",
                    onOpen = onOpenRecords
                )
            )
        }
        if (AccessRoles.canSeeReports(role)) {
            add(
                SectionItem(
                    number = "05", eyebrow = "REPORTES",
                    title = "Gráfica y tabulación",
                    hint = "Ventas del periodo, ticket promedio y productos con más ingresos.",
                    onOpen = onOpenReports
                )
            )
        }
        if (AccessRoles.canManageUsers(role)) {
            add(
                SectionItem(
                    number = "06", eyebrow = "PERSONAL",
                    title = "Trabajadores registrados",
                    hint = "Crea cuentas de trabajador, empleado o cliente y asigna su llave.",
                    onOpen = onOpenUsers
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth()) {

            /* ---------- Encabezado: marca + Salir ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PanalMark(modifier = Modifier.size(42.dp))
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = "Panal",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Tu negocio, en orden.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                TextButton(onClick = onLogout) {
                    Text("Salir")
                }
            }

            /* ---------- Saludo ---------- */
            Text(
                text = "TU PANEL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 36.dp, bottom = 9.dp)
            )
            Text(
                text = "Hola, ${session.username}.",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Nos alegra tenerte de vuelta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            /* ---------- Tarjeta de rol ---------- */
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.padding(top = 22.dp).fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TU ACCESO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = role,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Text(
                            text = AccessRoles.describe(role),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    PanalMark(modifier = Modifier.size(58.dp))
                }
            }

            /* ---------- Título de secciones + contador ---------- */
            Row(
                modifier = Modifier
                    .padding(top = 28.dp, bottom = 14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explora tu espacio",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "%02d SECCIONES".format(sections.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            /* ---------- Tarjetas visibles según el rol ---------- */
            sections.forEach { section ->
                SectionCard(section = section)
            }

            /* El tutorial en video se migra después de regrabarlo
               con los cuatro roles y el diseño panal nocturno. */
            Text(
                text = "Tutorial en video: se agrega al migrar el tutorial nuevo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 20.dp)
            )

            Text(
                text = "PANAL  ·  CADA PIEZA EN SU LUGAR",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionCard(section: SectionItem) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${section.number}  /  ${section.eyebrow}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 5.dp)
                )
                Text(
                    text = section.hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Button(
                onClick = section.onOpen,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text("Abrir  →")
            }
        }
    }
}

/* Color de texto reutilizable para botones de oro, por si se
   cambia SectionCard a botón primario más adelante. */
@Suppress("unused")
private val GoldButtonText = PanalGoldText
