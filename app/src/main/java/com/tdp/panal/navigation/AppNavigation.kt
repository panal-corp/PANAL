package com.tdp.panal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tdp.panal.ui.screens.ComingSoonScreen
import com.tdp.panal.ui.screens.ChooseAccessScreen
import com.tdp.panal.ui.screens.ManageAccountsScreen
import com.tdp.panal.domain.AccessRoles
import com.tdp.panal.ui.screens.LoginScreen
import com.tdp.panal.ui.screens.MainScreen
import com.tdp.panal.ui.screens.RegisterScreen
import com.tdp.panal.ui.viewmodel.AuthViewModel

/* ============================================================
   AppNavigation.kt
   Vincula todas las páginas. Equivale a la pila NavigationPage
   de MAUI: cada pantalla se registra una vez con su ruta y los
   botones solo llaman a navigate(ruta).

   Reglas que ya replica de .NET:
   - Al entrar, Login se borra de la pila: Atrás no regresa
     (InsertPageBefore + PopAsync en LoginPage.xaml.cs).
   - Al salir, se regresa al Login con el usuario ya escrito
     y se borra el panel de la pila.
   - Al registrarse, vuelve al Login con el aviso
     "Cuenta creada. Escribe tu contraseña...".
   - Sin sesión no se puede estar en el panel: si la sesión
     es null, se regresa al Login automáticamente.
   ============================================================ */
object AppRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val CHOOSE = "choose"
    const val GENERAL = "general"
    const val PROFILE = "profile"
    const val WORK = "work"
    const val RECORDS = "records"
    const val REPORTS = "reports"
    const val USERS = "users"
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val session by authViewModel.session.collectAsState()

    /* Datos que el Login recibe al volver de otra pantalla. */
    var loginUsername by remember { mutableStateOf("") }
    var loginNotice by remember { mutableStateOf<String?>(null) }

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        composable(AppRoutes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                initialUsername = loginUsername,
                successMessage = loginNotice,
                onNavigateToRegister = {
                    loginNotice = null
                    navController.navigate(AppRoutes.REGISTER)
                },
                onLoginSuccess = {
                    loginNotice = null
                    /* Login sale de la pila: Atrás no vuelve aquí. */
                    if (authViewModel.pendingRole != null) {
                        navController.navigate(AppRoutes.CHOOSE)
                    } else {
                        navController.navigate(AppRoutes.MAIN) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onRegisterSuccess = { username ->
                    loginUsername = username
                    loginNotice = "Cuenta creada. Escribe tu contraseña para iniciar sesión."
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.CHOOSE) {
            if (authViewModel.pendingRole == null) {
                LaunchedEffect(Unit) { navController.popBackStack() }
            } else {
                ChooseAccessScreen(authViewModel,
                    onEnter = { navController.navigate(AppRoutes.MAIN) { popUpTo(AppRoutes.LOGIN) { inclusive = true } } },
                    onBack = { navController.popBackStack() })
            }
        }

        composable(AppRoutes.MAIN) {
            val currentSession = session
            if (currentSession == null) {
                /* Sin sesión no hay panel: de vuelta al acceso. */
                LaunchedEffect(Unit) {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(AppRoutes.MAIN) { inclusive = true }
                    }
                }
            } else {
                MainScreen(
                    session = currentSession,
                    onOpenGeneral = { navController.navigate(AppRoutes.GENERAL) },
                    onOpenProfile = { navController.navigate(AppRoutes.PROFILE) },
                    onOpenWork = { navController.navigate(AppRoutes.WORK) },
                    onOpenRecords = { navController.navigate(AppRoutes.RECORDS) },
                    onOpenReports = { navController.navigate(AppRoutes.REPORTS) },
                    onOpenUsers = { navController.navigate(AppRoutes.USERS) },
                    onLogout = {
                        /* Como en .NET: el Login conserva el usuario
                           escrito, pero no la contraseña. */
                        loginUsername = currentSession.username
                        loginNotice = null
                        authViewModel.logout()
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(AppRoutes.MAIN) { inclusive = true }
                        }
                    }
                )
            }
        }

        /* Secciones todavía en migración: cada una se reemplaza
           por su pantalla real sin tocar el menú principal. */
        composable(AppRoutes.GENERAL) {
            ComingSoonScreen(
                title = "Comprobantes y efectos",
                description = "Aquí va GeneralPage de .NET: comprobantes con su " +
                    "efecto en almacén y cuentas. Solo administración.",
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.PROFILE) {
            ComingSoonScreen(
                title = "Tu cuenta",
                description = "Aquí va ProfilePage de .NET: foto, preferencias " +
                    "e inicio automático.",
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.WORK) {
            ComingSoonScreen(
                title = "Panel de trabajo",
                description = "Aquí va WorkPage de .NET: ventas, abastecimiento " +
                    "y devoluciones según el rol, con el cliente operando " +
                    "solo a su propio nombre.",
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.RECORDS) {
            ComingSoonScreen(
                title = "Almacén y cuentas",
                description = "Aquí va RecordsPage de .NET: entradas, salidas " +
                    "y movimientos de dinero. Solo administración.",
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.REPORTS) {
            ComingSoonScreen(
                title = "Gráfica y tabulación",
                description = "Aquí va ReportsPage de .NET: ventas de 7 o 30 días " +
                    "en gráfica o tabla. Solo administración.",
                onBack = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.USERS) {
            val active = session
            if (active != null && AccessRoles.canManageUsers(active.role)) {
                ManageAccountsScreen(authViewModel, active.role) { navController.popBackStack() }
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}
