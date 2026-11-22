package com.panal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/* ============================================================
   AppDatabase.kt
   Base Room (SQLite) del prototipo Kotlin.
   El prototipo empieza vacío, así que se usa UNA sola base
   (panal.db), a diferencia de .NET que usa usuarios.db3 y
   tienda.db3. Las entidades de la tienda se agregarán aquí
   cuando se migre el punto de venta.
   ============================================================ */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
