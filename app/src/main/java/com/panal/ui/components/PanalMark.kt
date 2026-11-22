package com.panal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.panal.R

/* ============================================================
   PanalMark.kt
   Símbolo Panal SIMPLIFICADO a partir del logo original:
   - 4 hexágonos iguales (el molde nuevo traía 6-7 con degradado).
   - Misma disposición diagonal del logo original.
   - Un solo color plano: #7D683D en la versión original y
     #E4B46F (dorado del molde) en la versión nocturna, que es
     la que se usa sobre los fondos negros de la app.
   El dibujo vive en res/drawable/ic_panal_simbolo*.xml;
   también está en SVG en panal-kotlin/simbolos/logo/.
   ============================================================ */
@Composable
fun PanalMark(
    modifier: Modifier = Modifier,
    midnight: Boolean = true
) {
    Image(
        painter = painterResource(
            id = if (midnight) R.drawable.ic_panal_simbolo_nocturno
            else R.drawable.ic_panal_simbolo
        ),
        contentDescription = "Símbolo de Panal",
        modifier = modifier
    )
}
