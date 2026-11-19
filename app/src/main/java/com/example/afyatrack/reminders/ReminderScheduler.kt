
package com.example.afyatrack.reminders

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        medicationName: String,
        delayMinutes: Long
    ) {

        val inputData = Data.Builder()
            .putString("medicationName", medicationName)
            .build()

        val reminderRequest =
            OneTimeWorkRequestBuilder<MedicationReminderWorker>()
                .setInputData(inputData)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "medication_$medicationName",
            ExistingWorkPolicy.REPLACE,
            reminderRequest
        )
    }
}
