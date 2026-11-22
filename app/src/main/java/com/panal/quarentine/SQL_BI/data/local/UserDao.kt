package com.panal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/* ============================================================
   UserDao.kt
   Consultas SQL de usuarios. El SQL va limpio dentro de @Query
   y los comentarios quedan fuera, para que Room lo valide bien.
   ============================================================ */
@Dao
interface UserDao {

    /* Busca por la clave normalizada (mayúsculas sin espacios),
       igual que MakeUsernameKey en UserService.cs. */
    @Query("SELECT * FROM Users WHERE usernameKey = :usernameKey LIMIT 1")
    suspend fun findByUsernameKey(usernameKey: String): UserEntity?

    @Query("SELECT * FROM Users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): UserEntity?

    /* Cuenta las cuentas del dispositivo: sirve para saber si la
       que se está creando es la primera (será Administrador). */
    @Query("SELECT COUNT(*) FROM Users")
    suspend fun countUsers(): Int

    /* ABORT + índice único de usernameKey: si el nombre ya existe,
       Room lanza SQLiteConstraintException y el repositorio lo
       traduce a "usuario ya registrado". */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long
}
