package com.example.afyatrack

import kotlinx.coroutines.flow.Flow

// The middleman. Screens and ViewModels talk to this, never to the DAO.
// Later it can also choose between the database and an API.
class MedicationRepository(private val dao: MedicationDao) {

    fun getAll(): Flow<List<Medication>> = dao.getAll()

    suspend fun insert(medication: Medication) = dao.insert(medication)

    suspend fun update(medication: Medication) = dao.update(medication)

    suspend fun delete(medication: Medication) = dao.delete(medication)

    suspend fun isDuplicate(name: String, time: String, excludeId: Int = 0): Boolean {
        return dao.countSame(name, time, excludeId) > 0
    }
}