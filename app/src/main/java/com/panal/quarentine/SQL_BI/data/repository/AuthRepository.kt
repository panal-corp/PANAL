package com.panal.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.panal.data.local.UserDao
import com.panal.data.local.UserEntity
import com.panal.data.security.PasswordHasher
import com.panal.domain.AccessRoles
import com.panal.domain.SessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale

/* ============================================================
   AuthRepository.kt
   Transcripción de la parte de acceso de UserService.cs +
   UserAccounts.cs de .NET:
   - Registro público: la primera cuenta del dispositivo es
     Administrador; las siguientes son Cliente.
   - Acceso: usuario + contraseña y, si la cuenta de personal
     tiene llave configurada, también la llave de rol
     (la "doble llave").
   El hash PBKDF2 es lento a propósito: se calcula fuera del
   hilo principal (Dispatchers.Default).
   ============================================================ */

enum class RegistrationStatus {
    SUCCESS,
    MISSING_USERNAME,
    USERNAME_TOO_LONG,
    MISSING_PASSWORD,
    PASSWORDS_DO_NOT_MATCH,
    USERNAME_TAKEN
}

data class RegistrationResult(
    val status: RegistrationStatus,
    val username: String? = null
)

class AuthRepository(private val userDao: UserDao) {

    companion object {
        const val MAX_USERNAME_LENGTH = 50
    }

    /* ---------- Registro (RegisterPage de .NET) ---------- */

    suspend fun register(
        username: String?,
        password: String?,
        confirmation: String?
    ): RegistrationResult {
        if (username.isNullOrBlank()) {
            return RegistrationResult(RegistrationStatus.MISSING_USERNAME)
        }
        val displayName = normalizeDisplayName(username)
        if (displayName.length > MAX_USERNAME_LENGTH) {
            return RegistrationResult(RegistrationStatus.USERNAME_TOO_LONG)
        }
        if (password.isNullOrBlank() || confirmation.isNullOrBlank()) {
            return RegistrationResult(RegistrationStatus.MISSING_PASSWORD)
        }
        if (password != confirmation) {
            return RegistrationResult(RegistrationStatus.PASSWORDS_DO_NOT_MATCH)
        }

        val usernameKey = makeUsernameKey(displayName)
        if (userDao.findByUsernameKey(usernameKey) != null) {
            return RegistrationResult(RegistrationStatus.USERNAME_TAKEN)
        }

        /* La primera cuenta del dispositivo administra;
           las que se registren después son de cliente. */
        val role = if (userDao.countUsers() == 0) {
            AccessRoles.ADMINISTRATOR
        } else {
            AccessRoles.CUSTOMER
        }

        val salt = PasswordHasher.newSalt()
        val hash = withContext(Dispatchers.Default) {
            PasswordHasher.hash(password, salt, PasswordHasher.PASSWORD_ITERATIONS)
        }

        return try {
            userDao.insert(
                UserEntity(
                    username = displayName,
                    usernameKey = usernameKey,
                    passwordHash = hash,
                    passwordSalt = salt,
                    passwordIterations = PasswordHasher.PASSWORD_ITERATIONS,
                    role = role
                )
            )
            RegistrationResult(RegistrationStatus.SUCCESS, displayName)
        } catch (exception: SQLiteConstraintException) {
            /* El índice único detectó un registro simultáneo. */
            RegistrationResult(RegistrationStatus.USERNAME_TAKEN)
        }
    }

    /* ---------- Acceso (LoginPage de .NET) ---------- */

    /* Devuelve la sesión si usuario + contraseña (+ llave de rol
       cuando la cuenta la tiene) son correctos; null si no. */
    suspend fun authenticate(
        username: String?,
        password: String?,
        roleKey: String?
    ): SessionState? {
        if (username.isNullOrBlank() || password.isNullOrBlank()) return null

        val displayName = normalizeDisplayName(username)
        if (displayName.length > MAX_USERNAME_LENGTH) return null

        val user = userDao.findByUsernameKey(makeUsernameKey(displayName)) ?: return null

        val passwordOk = withContext(Dispatchers.Default) {
            PasswordHasher.verify(
                password,
                user.passwordSalt,
                user.passwordIterations,
                user.passwordHash
            )
        }
        if (!passwordOk) return null

        /* Doble llave: solo se exige si la cuenta tiene una llave
           guardada (personal creado por administración). Las cuentas
           de administración, de cliente o anteriores entran solo
           con su contraseña, igual que en .NET. */
        val storedRoleKeyHash = user.roleKeyHash
        if (storedRoleKeyHash != null && storedRoleKeyHash.isNotEmpty()) {
            if (roleKey.isNullOrBlank()) return null
            val salt = user.roleKeySalt ?: ByteArray(0)
            val roleKeyOk = withContext(Dispatchers.Default) {
                PasswordHasher.verify(
                    roleKey.trim(),
                    salt,
                    PasswordHasher.ROLE_KEY_ITERATIONS,
                    storedRoleKeyHash
                )
            }
            if (!roleKeyOk) return null
        }

        return SessionState(
            userId = user.id,
            username = user.username,
            role = AccessRoles.normalize(user.role)
        )
    }

    /* ---------- Normalización (MakeUsernameKey en .NET) ---------- */

    private fun normalizeDisplayName(username: String): String =
        Normalizer.normalize(username.trim(), Normalizer.Form.NFC)

    private fun makeUsernameKey(displayName: String): String =
        displayName.uppercase(Locale.ROOT)
}
