package com.example.afyatrack.ui.medications

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.afyatrack.Medication
import com.example.afyatrack.ui.viewmodel.MedicationViewModel
import com.example.afyatrack.R

class AddEditMedicationFragment : Fragment(R.layout.fragment_add_edit_medication) {

    private val viewModel: MedicationViewModel by activityViewModels()
    private var editingMedication: Medication? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvFormTitle = view.findViewById<TextView>(R.id.tvFormTitle)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etDosage = view.findViewById<EditText>(R.id.etDosage)
        val etTime = view.findViewById<EditText>(R.id.etTime)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val tvErrorMessage = view.findViewById<TextView>(R.id.tvErrorMessage)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Retrieve Serializable Medication object when editing
        editingMedication = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_MEDICATION, Medication::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_MEDICATION) as? Medication
        }

        editingMedication?.let { med ->
            tvFormTitle.text = "Edit Medication"
            etName.setText(med.name)
            etDosage.setText(med.dosage)
            etTime.setText(med.time)
            etNotes.setText(med.notes)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val dosage = etDosage.text.toString().trim()
            val time = etTime.text.toString().trim()
            val notes = etNotes.text.toString().trim()

            val handleResult: (String?) -> Unit = { errorMsg ->
                if (errorMsg != null) {
                    tvErrorMessage.text = errorMsg
                    tvErrorMessage.visibility = View.VISIBLE
                } else {
                    tvErrorMessage.visibility = View.GONE
                    parentFragmentManager.popBackStack()
                }
            }

            val existing = editingMedication
            if (existing == null) {
                val newMed = Medication(name = name, dosage = dosage, time = time, notes = notes)
                viewModel.add(newMed, handleResult)
            } else {
                val updatedMed = existing.copy(name = name, dosage = dosage, time = time, notes = notes)
                viewModel.update(updatedMed, handleResult)
            }
        }
    }

    companion object {
        private const val ARG_MEDICATION = "medication"

        fun newInstance(medication: Medication?): AddEditMedicationFragment {
            return AddEditMedicationFragment().apply {
                arguments = Bundle().apply {
                    if (medication != null) putSerializable(ARG_MEDICATION, medication)
                }
            }
        }
    }
}