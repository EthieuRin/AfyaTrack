package com.example.afyatrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// One row in the "medications" table. A data class holds data.
// Serializable lets Safe Args send it to the Edit screen.
@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Room numbers each row for us
    val name: String,
    val dosage: String,
    val time: String,      // text like "08:00"
    val notes: String = ""
) : Serializable