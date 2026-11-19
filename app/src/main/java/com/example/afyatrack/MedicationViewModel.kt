package com.example.afyatrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MedicationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        MedicationRepository(AppDatabase.getInstance(application).medicationDao())

    // The screen watches this. It updates by itself when data changes.
    val medications: LiveData<List<Medication>> = repository.getAll().asLiveData()

    // Checks the rules. Returns an error message, or null if everything is fine.
    private suspend fun validate(m: Medication): String? {
        if (m.name.isBlank()) return "Medicine name is required"
        if (m.dosage.isBlank()) return "Dosage is required"
        val timeRule = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        if (!timeRule.matches(m.time)) return "Time must be HH:MM, for example 08:00"
        if (repository.isDuplicate(m.name.trim(), m.time, m.id)) {
            return "This medicine is already scheduled at that time"
        }
        return null
    }

    // onResult(null) means saved. Otherwise it carries the error to show.
    fun add(medication: Medication, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val error = validate(medication)
            if (error == null) repository.insert(medication.copy(name = medication.name.trim()))
            onResult(error)
        }
    }

    fun update(medication: Medication, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            val error = validate(medication)
            if (error == null) repository.update(medication.copy(name = medication.name.trim()))
            onResult(error)
        }
    }

    fun delete(medication: Medication) {
        viewModelScope.launch { repository.delete(medication) }
    }
}