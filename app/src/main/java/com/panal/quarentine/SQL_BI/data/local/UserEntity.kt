package com.panal.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/* ============================================================
   UserEntity.kt
   Equivale a Models/UserRecord.cs de .NET (tabla "Users").

   Solo se guarda el hash PBKDF2 de la contraseña y de la llave
   de rol, nunca el texto original.
   - role == null  -> cuenta anterior a los roles = Administrador.
   - roleKeyHash   -> solo existe en cuentas de personal
                      (Trabajador / Empleado) creadas por administración.
   ============================================================ */
@Entity(
    tableName = "Users",
    indices = [Index(value = ["usernameKey"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val usernameKey: String,
    val passwordHash: ByteArray,
    val passwordSalt: ByteArray,
    val passwordIterations: Int,
    val role: String? = null,
    val roleKeyHash: ByteArray? = null,
    val roleKeySalt: ByteArray? = null
)
