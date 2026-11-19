
package com.example.afyatrack

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "AfyaTrack.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {

        // Users table

        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password_salt TEXT NOT NULL,
                password_hash TEXT NOT NULL
            )
        """.trimIndent())

        // Wellness records table

        db.execSQL("""
            CREATE TABLE wellness_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                age INTEGER NOT NULL,
                sleep REAL NOT NULL,
                water INTEGER NOT NULL,
                steps INTEGER NOT NULL,
                score INTEGER NOT NULL,
                date TEXT NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password_salt TEXT NOT NULL,
                    password_hash TEXT NOT NULL
                )
            """.trimIndent())
        }
    }

    // Generate a random salt

    private fun generateSalt(): ByteArray {

        val salt = ByteArray(16)

        SecureRandom().nextBytes(salt)

        return salt
    }

    // Hash password using PBKDF2

    private fun hashPassword(
        password: String,
        salt: ByteArray
    ): ByteArray {

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            120000,
            256
        )

        return try {

            SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            ).generateSecret(spec).encoded

        } finally {
            spec.clearPassword()
        }
    }

    private fun toHex(bytes: ByteArray): String {

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    private fun fromHex(value: String): ByteArray {

        return value.chunked(2).map {
            it.toInt(16).toByte()
        }.toByteArray()
    }

    // REGISTER USER

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): Boolean {

        val db = writableDatabase

        val salt = generateSalt()

        val passwordHash = hashPassword(
            password,
            salt
        )

        val values = ContentValues().apply {

            put("full_name", fullName)
            put("email", email.lowercase())
            put("password_salt", toHex(salt))
            put("password_hash", toHex(passwordHash))

        }

        return try {

            db.insertOrThrow(
                "users",
                null,
                values
            ) != -1L

        } catch (e: Exception) {

            false

        }
    }

    // LOGIN USER

    fun loginUser(
        email: String,
        password: String
    ): String? {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT full_name, password_salt, password_hash
            FROM users
            WHERE email = ?
            """.trimIndent(),
            arrayOf(email.lowercase())
        )

        cursor.use {

            if (it.moveToFirst()) {

                val name = it.getString(0)

                val salt = fromHex(it.getString(1))

                val savedHash = fromHex(it.getString(2))

                val enteredHash = hashPassword(
                    password,
                    salt
                )

                val valid = MessageDigest.isEqual(
                    savedHash,
                    enteredHash
                )

                if (valid) {
                    return name
                }
            }
        }

        return null
    }

    // SAVE WELLNESS RECORD

    fun saveRecord(
        name: String,
        age: Int,
        sleep: Double,
        water: Int,
        steps: Int,
        score: Int,
        date: String
    ): Boolean {

        val values = ContentValues().apply {

            put("name", name)
            put("age", age)
            put("sleep", sleep)
            put("water", water)
            put("steps", steps)
            put("score", score)
            put("date", date)

        }

        val result = writableDatabase.insert(
            "wellness_records",
            null,
            values
        )

        return result != -1L
    }

    // RETRIEVE WELLNESS RECORDS

    fun getRecords(): List<String> {

        val records = mutableListOf<String>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM wellness_records ORDER BY id DESC",
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val name = it.getString(
                    it.getColumnIndexOrThrow("name")
                )

                val age = it.getInt(
                    it.getColumnIndexOrThrow("age")
                )

                val sleep = it.getDouble(
                    it.getColumnIndexOrThrow("sleep")
                )

                val water = it.getInt(
                    it.getColumnIndexOrThrow("water")
                )

                val steps = it.getInt(
                    it.getColumnIndexOrThrow("steps")
                )

                val score = it.getInt(
                    it.getColumnIndexOrThrow("score")
                )

                val date = it.getString(
                    it.getColumnIndexOrThrow("date")
                )

                records.add("""
                    DATE: $date

                    NAME: $name
                    AGE: $age years

                    SLEEP: $sleep hours
                    WATER: $water glasses
                    STEPS: $steps

                    WELLNESS SCORE: $score/100
                """.trimIndent())
            }
        }

        return records
    }

    // DELETE WELLNESS RECORD

    fun deleteRecord(id: Int): Boolean {

        val result = writableDatabase.delete(
            "wellness_records",
            "id = ?",
            arrayOf(id.toString())
        )

        return result > 0
    }
}