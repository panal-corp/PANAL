package com.tdp.panal.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.tdp.panal.data.local.UserDao
import com.tdp.panal.data.local.UserEntity
import com.tdp.panal.data.security.PasswordHasher
import com.tdp.panal.domain.AccessRoles
import com.tdp.panal.domain.SessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val keyMutex = Mutex()
    private val registrationMutex = Mutex()

    companion object {
        const val MAX_USERNAME_LENGTH = 50
    }

    /* ---------- Registro (RegisterPage de .NET) ---------- */

    suspend fun register(
        username: String?,
        password: String?,
        confirmation: String?
    ): RegistrationResult {
        return registrationMutex.withLock {
            if (username.isNullOrBlank()) {
                return@withLock RegistrationResult(RegistrationStatus.MISSING_USERNAME)
            }
            val displayName = normalizeDisplayName(username)
            if (displayName.length > MAX_USERNAME_LENGTH) {
                return@withLock RegistrationResult(RegistrationStatus.USERNAME_TOO_LONG)
            }
            if (password.isNullOrBlank() || confirmation.isNullOrBlank()) {
                return@withLock RegistrationResult(RegistrationStatus.MISSING_PASSWORD)
            }
            if (password != confirmation) {
                return@withLock RegistrationResult(RegistrationStatus.PASSWORDS_DO_NOT_MATCH)
            }

            val usernameKey = makeUsernameKey(displayName)
            if (userDao.findByUsernameKey(usernameKey) != null) {
                return@withLock RegistrationResult(RegistrationStatus.USERNAME_TAKEN)
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

            try {
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

    }


    /* ---------- Acceso (LoginPage de .NET) ---------- */

    /* Devuelve la sesión si usuario + contraseña (+ llave de rol
       cuando la cuenta la tiene) son correctos; null si no. */
    // La primera etapa comprueba únicamente la contraseña; todavía no otorga privilegios.
    suspend fun authenticatePassword(username: String?, password: String?): SessionState? {
        if (username.isNullOrBlank() || password.isNullOrBlank()) return null
        val name = normalizeDisplayName(username)
        if (name.length > MAX_USERNAME_LENGTH) return null
        val user = userDao.findByUsernameKey(makeUsernameKey(name)) ?: return null
        val ok = withContext(Dispatchers.Default) {
            PasswordHasher.verify(password, user.passwordSalt, user.passwordIterations, user.passwordHash)
        }
        if (!ok || !AccessRoles.isValid(user.role)) return null
        return SessionState(user.id, user.username, user.role!!)
    }

    suspend fun verifyRoleKey(userId: Int, key: String): SessionState? {
        if (!key.matches(Regex("[0-9]{12}"))) return null
        val user = userDao.findById(userId) ?: return null
        if (user.role !in listOf(AccessRoles.SUPERIOR, AccessRoles.ADMINISTRATOR, AccessRoles.WORKER, AccessRoles.EMPLOYEE)) return null
        val hash = user.roleKeyHash ?: return null
        val salt = user.roleKeySalt ?: return null
        val ok = withContext(Dispatchers.Default) {
            PasswordHasher.verify(key, salt, PasswordHasher.ROLE_KEY_ITERATIONS, hash)
        }
        return if (ok) SessionState(user.id, user.username, user.role!!) else null
    }

    data class Account(val id: Int, val username: String, val role: String?, val hasKey: Boolean)

    private suspend fun authorized(actor: SessionState): Boolean {
        val user = userDao.findById(actor.userId) ?: return false
        return user.role == actor.role && actor.role in listOf(AccessRoles.ADMINISTRATOR, AccessRoles.SUPERIOR)
    }

    suspend fun accounts(actor: SessionState): List<Account> {
        require(authorized(actor))
        return userDao.listUsers().filter {
            it.role == AccessRoles.CUSTOMER || it.role == AccessRoles.WORKER ||
                (actor.role == AccessRoles.SUPERIOR && it.role == AccessRoles.ADMINISTRATOR)
        }.map { Account(it.id, it.username, it.role, it.roleKeyHash != null) }
    }

    // Asignar un rol es una operación separada de emitir una llave.
    suspend fun assignRole(actor: SessionState, id: Int, role: String) {
        require(authorized(actor) && actor.userId != id)
        val target = userDao.findById(id) ?: error("Cuenta inexistente")
        val permitted = if (actor.role == AccessRoles.SUPERIOR) listOf(AccessRoles.WORKER, AccessRoles.ADMINISTRATOR) else listOf(AccessRoles.WORKER)
        require((target.role == AccessRoles.CUSTOMER && role in permitted) ||
            (target.role in permitted && role == AccessRoles.CUSTOMER))
        if (target.role != role) userDao.setRole(id, role)
    }

    suspend fun issueKey(actor: SessionState, id: Int): String = keyMutex.withLock {
        require(authorized(actor) && actor.userId != id)
        val target = userDao.findById(id) ?: error("Cuenta inexistente")
        val permitted = if (actor.role == AccessRoles.SUPERIOR) listOf(AccessRoles.WORKER, AccessRoles.ADMINISTRATOR) else listOf(AccessRoles.WORKER)
        require(target.role in permitted && target.roleKeyHash == null)
        val random = java.security.SecureRandom()
        // Evita que dos cuentas activas compartan el mismo código, aun con salts diferentes.
        val existing = userDao.listUsers().filter { it.roleKeyHash != null && it.roleKeySalt != null }
        var key: String
        do {
            key = buildString { repeat(12) { append(random.nextInt(10)) } }
            val duplicate = withContext(Dispatchers.Default) {
                existing.any { PasswordHasher.verify(key, it.roleKeySalt!!, PasswordHasher.ROLE_KEY_ITERATIONS, it.roleKeyHash!!) }
            }
        } while (duplicate)
        val salt = PasswordHasher.newSalt()
        val hash = withContext(Dispatchers.Default) { PasswordHasher.hash(key, salt, PasswordHasher.ROLE_KEY_ITERATIONS) }
        userDao.setRoleKey(id, hash, salt)
        key
    }

    suspend fun revokeKey(actor: SessionState, id: Int) {
        require(authorized(actor) && actor.userId != id)
        val target = userDao.findById(id) ?: error("Cuenta inexistente")
        val permitted = if (actor.role == AccessRoles.SUPERIOR) listOf(AccessRoles.WORKER, AccessRoles.ADMINISTRATOR) else listOf(AccessRoles.WORKER)
        require(target.role in permitted)
        userDao.setRoleKey(id, null, null)
    }

    /* ---------- Normalización (MakeUsernameKey en .NET) ---------- */

    private fun normalizeDisplayName(username: String): String =
        Normalizer.normalize(username.trim(), Normalizer.Form.NFC)

    private fun makeUsernameKey(displayName: String): String =
        displayName.uppercase(Locale.ROOT)
}
