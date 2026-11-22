package com.panal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.panal.data.local.InMemoryUserDao
import com.panal.data.repository.AuthRepository
import com.panal.ui.theme.PANALTheme
import com.panal.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userDao = InMemoryUserDao()
        val authRepository = AuthRepository(userDao)
        val authViewModel = ViewModelProvider(
            this,
            AuthViewModel.Factory(authRepository)
        )[AuthViewModel::class.java]

        setContent {
            PANALTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(authViewModel = authViewModel)
                }
            }
        }
    }
}
