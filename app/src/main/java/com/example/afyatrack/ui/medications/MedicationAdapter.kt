package com.example.afyatrack.ui.medications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.afyatrack.Medication
import com.example.afyatrack.R

class MedicationAdapter(
    private val onItemClick: (Medication) -> Unit,
    private val onDeleteClick: (Medication) -> Unit
) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    private var items: List<Medication> = emptyList()

    fun submitList(newList: List<Medication>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class MedicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMedName)
        val tvDetails: TextView = view.findViewById(R.id.tvMedDetails)
        val tvNotes: TextView = view.findViewById(R.id.tvMedNotes)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val med = items[position]
        holder.tvName.text = med.name
        holder.tvDetails.text = "${med.dosage} at ${med.time}"

        if (med.notes.isNotBlank()) {
            holder.tvNotes.text = med.notes
            holder.tvNotes.visibility = View.VISIBLE
        } else {
            holder.tvNotes.visibility = View.GONE
        }

        // Tap row to edit
        holder.itemView.setOnClickListener { onItemClick(med) }
        // Tap delete button for confirmation dialog
        holder.btnDelete.setOnClickListener { onDeleteClick(med) }
    }

    override fun getItemCount(): Int = items.size
}