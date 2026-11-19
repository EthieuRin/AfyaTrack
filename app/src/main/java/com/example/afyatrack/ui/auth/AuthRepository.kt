
package com.example.afyatrack.ui.auth

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class AuthRepository(context: Context) {

    private val dbHelper = AuthDatabaseHelper(context)

    fun register(name: String, email: String, password: String): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return false
        }

        val db = dbHelper.writableDatabase

        val checkCursor = db.rawQuery(
            "SELECT id FROM users WHERE email = ?",
            arrayOf(email.trim().lowercase())
        )

        val emailExists = checkCursor.moveToFirst()
        checkCursor.close()

        if (emailExists) return false

        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val saltString = Base64.encodeToString(salt, Base64.NO_WRAP)
        val passwordHash = hashPassword(password, salt)

        val values = ContentValues().apply {
            put("name", name.trim())
            put("email", email.trim().lowercase())
            put("password_hash", passwordHash)
            put("salt", saltString)
        }

        return db.insert("users", null, values) != -1L
    }

    fun login(email: String, password: String): Boolean {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT password_hash, salt FROM users WHERE email = ?",
            arrayOf(email.trim().lowercase())
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            return false
        }

        val savedHash = cursor.getString(0)
        val savedSalt = cursor.getString(1)
        cursor.close()

        val salt = Base64.decode(savedSalt, Base64.NO_WRAP)
        val enteredHash = hashPassword(password, salt)

        return MessageDigest.isEqual(
            savedHash.toByteArray(),
            enteredHash.toByteArray()
        )
    }

    private fun hashPassword(password: String, salt: ByteArray): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            120000,
            256
        )

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = factory.generateSecret(spec).encoded

        spec.clearPassword()

        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    private class AuthDatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, "AfyaTrackUsers.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(
            db: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
        ) {
            // Preserve existing user accounts.
        }
    }
}
