
package com.example.afyatrack.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val textView = TextView(requireContext()).apply {
            text = "Welcome to AfyaTrack!"
            textSize = 24f
            setPadding(40, 40, 40, 40)
        }

        return textView
    }
}
