
package com.example.afyatrack.ui.dashboard

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.afyatrack.wellness.WellnessRepository
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private val repository = WellnessRepository()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val tipText = TextView(requireContext()).apply {
            text = "Loading your daily wellness tip..."
            textSize = 20f
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        viewLifecycleOwnerLiveData.observe(this) { owner ->
            owner.lifecycleScope.launch {
                tipText.text = "Loading your daily wellness tip..."

                val tip = repository.getWellnessTip()

                if (isAdded) {
                    tipText.text = "DAILY WELLNESS TIP\n\n$tip"
                }
            }
        }

        return tipText
    }
}
