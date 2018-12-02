package de.unihannover.se.tauben2.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import com.leinardi.android.speeddial.SpeedDialActionItem
import kotlinx.android.synthetic.main.fragment_cases.view.*

class CasesFragment : Fragment() {

    companion object : Singleton<CasesFragment>() {
        override fun newInstance()= CasesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_cases, container, false)
        val recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as CasesRecyclerFragment
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        getViewModel(CaseViewModel::class.java)?.cases?.let {
            val allCases = it//.filter { case -> !case.isClosed }
            allCases.observe(this, LoadingObserver(recyclerFragment){ message ->
                Toast.makeText(this.context, "Couldn't load events: $message", Toast.LENGTH_LONG).show()
            })

            allCases.observe(this, mapsFragment)
        }

        initFab(view, recyclerFragment, mapsFragment)

        return view
    }

    // Filter - Floating Action Button
    private fun initFab (view : View, recyclerFragment : CasesRecyclerFragment, mapsFragment : MapViewFragment) {

        val speedDialView = view.speedDial

        // Add Filter Buttons
        // my cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.cases_filter_my, R.drawable.ic_assignment_ind_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.Gray, null))
                        // TODO: y u no work...
                        //.setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.Green, null))
                        .setLabel("My Cases")
                        .create()
        )
        // closed cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.cases_filter_closed, R.drawable.ic_assignment_turned_in_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.Gray, null))
                        .setLabel("Closed Cases")
                        .create()
        )
        // open cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.cases_filter_open, R.drawable.ic_assignment_late_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.Gray, null))
                        .setLabel("Open Cases")
                        .create()
        )
        // all cases
        speedDialView.addActionItem(
                SpeedDialActionItem.Builder(R.id.cases_filter_all, R.drawable.ic_assignment_white_24dp)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.Gray, null))
                        .setLabel("All Cases")
                        .create()
        )

        // onClickListener
        speedDialView.setOnActionSelectedListener { speedDialActionItem ->
            when (speedDialActionItem.id) {
                R.id.cases_filter_my -> {
                    Log.d("FilterItems", "myButtonClicked")
                    false
                }
                R.id.cases_filter_closed -> {
                    Log.d("FilterItems", "closedButtonClicked")
                    false
                }
                R.id.cases_filter_open -> {
                    Log.d("FilterItems", "openButtonClicked")
                    false
                }
                R.id.cases_filter_all -> {
                    Log.d("FilterItems", "allButtonClicked")
                    false // true to keep the Speed Dial open
                }
                else -> false
            }
        }
    }
}