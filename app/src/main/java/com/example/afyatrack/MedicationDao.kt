package com.example.afyatrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO = Data Access Object. We only write the list of operations;
// Room writes the SQL code behind @Insert, @Update and @Delete.
@Dao
interface MedicationDao {

    @Insert                      // CREATE
    suspend fun insert(medication: Medication)

    @Query("SELECT * FROM medications ORDER BY time ASC")   // READ
    fun getAll(): Flow<List<Medication>>

    @Update                      // UPDATE (finds the row by id)
    suspend fun update(medication: Medication)

    @Delete                      // DELETE
    suspend fun delete(medication: Medication)

    // Used for validation: is this medicine already at this time?
    // excludeId lets an Edit ignore the row being edited.
    @Query("SELECT COUNT(*) FROM medications WHERE name = :name AND time = :time AND id != :excludeId")
    suspend fun countSame(name: String, time: String, excludeId: Int): Int
}