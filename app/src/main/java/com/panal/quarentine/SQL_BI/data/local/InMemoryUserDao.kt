package com.panal.data.local

import android.database.sqlite.SQLiteConstraintException

class InMemoryUserDao : UserDao {
    private val users = mutableListOf<UserEntity>()
    private var idCounter = 1

    override suspend fun findByUsernameKey(usernameKey: String): UserEntity? {
        return users.find { it.usernameKey.equals(usernameKey, ignoreCase = true) }
    }

    override suspend fun findById(id: Int): UserEntity? {
        return users.find { it.id == id }
    }

    override suspend fun countUsers(): Int {
        return users.size
    }

    override suspend fun insert(user: UserEntity): Long {
        if (users.any { it.usernameKey.equals(user.usernameKey, ignoreCase = true) }) {
            throw SQLiteConstraintException("UNIQUE constraint failed: Users.usernameKey")
        }
        val id = idCounter++
        val newUser = user.copy(id = id)
        users.add(newUser)
        return id.toLong()
    }
}
