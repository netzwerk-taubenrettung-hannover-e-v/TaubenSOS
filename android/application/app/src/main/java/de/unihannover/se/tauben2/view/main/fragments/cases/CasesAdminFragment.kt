package de.unihannover.se.tauben2.view.main.fragments.cases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.leinardi.android.speeddial.SpeedDialActionItem
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.view.main.fragments.MapViewFragment
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment
import kotlinx.android.synthetic.main.fragment_cases.view.*

class CasesAdminFragment : CasesFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = super.onCreateView(inflater, container, savedInstanceState)

        view?.let {
            initFab(it, recyclerFragment, mapsFragment)
        }

        activity?.setTitle(R.string.all_cases)

        return view
    }

    // Filter - Floating Action Button
    private fun initFab (view : View, recyclerFragment : CasesRecyclerFragment, mapsFragment : MapViewFragment) {

        view.dialOverlay.visibility = View.VISIBLE

//        view.speedDial.

        view.speedDial.apply {

            visibility = View.VISIBLE

            // Add Filter Buttons
            // my cases
            addActionItem(
                    SpeedDialActionItem.Builder(R.id.cases_filter_my, R.drawable.ic_assignment_ind_white_24dp)
                            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.White, null))
                            // TODO: y u no work...
//                             .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.White, null))
                            .setLabel(getString(R.string.my_cases))
                            .create()
            )
            // closed cases
            addActionItem(
                    SpeedDialActionItem.Builder(R.id.cases_filter_closed, R.drawable.ic_assignment_turned_in_white_24dp)
                            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.White, null))
                            .setLabel(getString(R.string.closed_cases))
                            .create()
            )
            // open cases
            addActionItem(
                    SpeedDialActionItem.Builder(R.id.cases_filter_open, R.drawable.ic_assignment_late_white_24dp)
                            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.White, null))
                            .setLabel(getString(R.string.open_cases))
                            .create()
            )
            // all cases
            addActionItem(
                    SpeedDialActionItem.Builder(R.id.cases_filter_all, R.drawable.ic_assignment)
                            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.White, null))
                            .setLabel(getString(R.string.all_cases))
                            .create()
            )

            // onClickListener
            setOnActionSelectedListener { speedDialActionItem ->
                when (speedDialActionItem.id) {
                    R.id.cases_filter_my -> {
                        mFilter = Filter.MY
                    }
                    R.id.cases_filter_closed -> {
                        mFilter = Filter.CLOSED
                    }
                    R.id.cases_filter_open -> {
                        mFilter = Filter.OPEN
                    }
                    R.id.cases_filter_all -> {
                        mFilter = Filter.ALL
                    }
                }
                loadCases(mFilter)
                false

            }
        }
    }
}