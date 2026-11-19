
package com.example.afyatrack

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "AfyaTrack.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        val createTable = """
            CREATE TABLE wellness_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                age INTEGER,
                sleep REAL,
                water INTEGER,
                steps INTEGER,
                score INTEGER,
                date TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS wellness_records")
        onCreate(db)
    }

    fun saveRecord(
        name: String,
        age: Int,
        sleep: Double,
        water: Int,
        steps: Int,
        score: Int,
        date: String
    ): Boolean {

        val db = writableDatabase
        val values = ContentValues()

        values.put("name", name)
        values.put("age", age)
        values.put("sleep", sleep)
        values.put("water", water)
        values.put("steps", steps)
        values.put("score", score)
        values.put("date", date)

        val result = db.insert(
            "wellness_records",
            null,
            values
        )

        return result != -1L
    }

    fun getRecords(): List<String> {

        val records = mutableListOf<String>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM wellness_records ORDER BY id DESC",
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                )

                val sleep = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("sleep")
                )

                val water = cursor.getInt(
                    cursor.getColumnIndexOrThrow("water")
                )

                val steps = cursor.getInt(
                    cursor.getColumnIndexOrThrow("steps")
                )

                val score = cursor.getInt(
                    cursor.getColumnIndexOrThrow("score")
                )

                val date = cursor.getString(
                    cursor.getColumnIndexOrThrow("date")
                )

                records.add(
                    """
                    Date: $date
                    Name: $name
                    Sleep: $sleep hours
                    Water: $water glasses
                    Steps: $steps
                    Wellness Score: $score/100
                    """.trimIndent()
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        return records
    }
}
