package com.tdp.panal.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/* ============================================================
   PasswordHasher.kt
   PBKDF2-HMAC-SHA256, igual que Rfc2898DeriveBytes.Pbkdf2
   en UserService.cs de .NET.
   - Contraseña: 600 000 iteraciones (HashIterations en .NET).
   - Llave de rol: 210 000 iteraciones (RoleKeyIterations).
   Requiere minSdk 26, donde Android ya trae PBKDF2WithHmacSHA256.
   ============================================================ */
object PasswordHasher {
    const val PASSWORD_ITERATIONS = 600_000
    const val ROLE_KEY_ITERATIONS = 210_000
    private const val SALT_LENGTH_BYTES = 16
    private const val HASH_LENGTH_BYTES = 32

    fun newSalt(): ByteArray = ByteArray(SALT_LENGTH_BYTES).also { salt ->
        SecureRandom().nextBytes(salt)
    }

    fun hash(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            HASH_LENGTH_BYTES * 8
        )
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            requireNotNull(factory.generateSecret(spec).encoded) {
                "PBKDF2 no devolvió material de clave."
            }
        } finally {
            /* Limpia la copia de la contraseña que guarda el spec. */
            spec.clearPassword()
        }
    }

    /* Comparación en tiempo constante, como
       CryptographicOperations.FixedTimeEquals en .NET. */
    fun verify(password: String, salt: ByteArray, iterations: Int, expected: ByteArray): Boolean {
        val candidate = hash(password, salt, iterations)
        return MessageDigest.isEqual(candidate, expected)
    }
}
