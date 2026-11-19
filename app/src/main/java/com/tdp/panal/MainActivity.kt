package com.tdp.panal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.tdp.panal.data.local.AppDatabase
import com.tdp.panal.data.repository.AuthRepository
import com.tdp.panal.navigation.AppNavigation
import com.tdp.panal.ui.theme.PanalTheme
import com.tdp.panal.ui.viewmodel.AuthViewModel

/* ============================================================
   MainActivity.kt
   Única Activity de la app, como recomendamos desde el inicio.
   Aquí se crea la base Room y el AuthViewModel una sola vez, y
   el contenido ya NO es LoginScreen directo: es AppNavigation,
   que decide qué pantalla se ve (login, registro, panel...).
   Si tu MainActivity actual ya crea la base o el tema, conserva
   eso y solo cambia el contenido por AppNavigation(...).
   ============================================================ */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /* Base de datos local (SQLite vía Room), un solo archivo. */
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mosaic.db"
        ).build()

        val authRepository = AuthRepository(database.userDao())
        val authViewModel = ViewModelProvider(
            this,
            AuthViewModel.Factory(authRepository)
        )[AuthViewModel::class.java]

        setContent {
            PanalTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}
