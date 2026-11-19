package com.example.afyatrack.ui.medications

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afyatrack.Medication
import com.example.afyatrack.ui.viewmodel.MedicationViewModel
import com.example.afyatrack.R

class MedicationListFragment : Fragment(R.layout.fragment_medication_list) {

    private val viewModel: MedicationViewModel by activityViewModels()
    private lateinit var adapter: MedicationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvMedications = view.findViewById<RecyclerView>(R.id.rvMedications)
        val tvEmptyState = view.findViewById<TextView>(R.id.tvEmptyState)
        val btnAdd = view.findViewById<Button>(R.id.btnAddMedication)

        adapter = MedicationAdapter(
            onItemClick = { medication ->
                openAddEditScreen(medication)
            },
            onDeleteClick = { medication ->
                confirmDelete(medication)
            }
        )

        rvMedications.layoutManager = LinearLayoutManager(requireContext())
        rvMedications.adapter = adapter

        // Observe medications list and toggle empty state
        viewModel.medications.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                tvEmptyState.visibility = View.VISIBLE
                rvMedications.visibility = View.GONE
            } else {
                tvEmptyState.visibility = View.GONE
                rvMedications.visibility = View.VISIBLE
            }
        }

        btnAdd.setOnClickListener {
            openAddEditScreen(null)
        }
    }

    private fun confirmDelete(medication: Medication) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Medication")
            .setMessage("Are you sure you want to delete ${medication.name} (${medication.time})?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(medication)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAddEditScreen(medication: Medication?) {
        val fragment = AddEditMedicationFragment.newInstance(medication)
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}