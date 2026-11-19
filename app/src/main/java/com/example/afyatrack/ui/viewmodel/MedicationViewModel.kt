package com.example.afyatrack.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.afyatrack.AppDatabase
import com.example.afyatrack.Medication
import com.example.afyatrack.MedicationRepository
import kotlinx.coroutines.launch

class MedicationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicationRepository

    val medications: LiveData<List<Medication>>

    init {
        val dao = AppDatabase.getInstance(application).medicationDao()
        repository = MedicationRepository(dao)
        medications = repository.getAll().asLiveData()
    }

    fun add(medication: Medication, onResult: (String?) -> Unit = {}) {
        if (medication.name.isBlank()) {
            onResult("Medication name cannot be empty")
            return
        }
        if (medication.dosage.isBlank()) {
            onResult("Dosage cannot be empty")
            return
        }
        if (medication.time.isBlank()) {
            onResult("Time cannot be empty")
            return
        }

        viewModelScope.launch {
            if (repository.isDuplicate(medication.name, medication.time)) {
                onResult("A medication with this name and time already exists")
            } else {
                repository.insert(medication)
                onResult(null)
            }
        }
    }

    fun update(medication: Medication, onResult: (String?) -> Unit = {}) {
        if (medication.name.isBlank()) {
            onResult("Medication name cannot be empty")
            return
        }
        if (medication.dosage.isBlank()) {
            onResult("Dosage cannot be empty")
            return
        }
        if (medication.time.isBlank()) {
            onResult("Time cannot be empty")
            return
        }

        viewModelScope.launch {
            if (repository.isDuplicate(medication.name, medication.time, excludeId = medication.id)) {
                onResult("A medication with this name and time already exists")
            } else {
                repository.update(medication)
                onResult(null)
            }
        }
    }

    fun delete(medication: Medication) {
        viewModelScope.launch {
            repository.delete(medication)
        }
    }
}
