package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.leinardi.android.speeddial.SpeedDialActionItem
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.fragment_cases.*
import kotlinx.android.synthetic.main.fragment_cases.view.*

class CasesFragment : Fragment() {

    enum class Filter {
        ALL, CLOSED, OPEN, MY
    }

    private lateinit var recyclerFragment : CasesRecyclerFragment
    private lateinit var mapsFragment : MapViewFragment

    private var mCurrentObservedData: LiveDataRes<List<Case>>? = null
    private lateinit var mCurrentObserver: LoadingObserver<List<Case>>
    private lateinit var mCurrentMapObserver: LoadingObserver<List<Case>>

    companion object : Singleton<CasesFragment>() {
        override fun newInstance()= CasesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_cases, container, false)
        recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as CasesRecyclerFragment
        mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        mCurrentObserver = LoadingObserver(successObserver = recyclerFragment)
        mCurrentMapObserver = LoadingObserver(successObserver = mapsFragment)

        loadCases(Filter.ALL)

        initFab(view, recyclerFragment, mapsFragment)

        return view
    }

    private fun loadCases (filter : Filter) {

        getViewModel(CaseViewModel::class.java)?.let { viewModel ->

            // Remove old Observers
            mCurrentObservedData?.removeObserver(mCurrentObserver)
            mCurrentObservedData?.removeObserver(mCurrentMapObserver)

            mCurrentObservedData = when (filter) {
               Filter.MY -> viewModel.cases.filter { case -> case.rescuer == App.mCurrentUser.username }
               Filter.CLOSED -> viewModel.cases.filter { case -> case.isClosed == true }
               Filter.OPEN -> viewModel.cases.filter { case -> case.isClosed == false}
               else -> viewModel.cases
            }

            mCurrentObservedData?.observe(this, mCurrentObserver)
            mCurrentObservedData?.observe(this, mCurrentMapObserver)
        }
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
                        // .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.Green, null))
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
                    val tv1 = text_currentCases as TextView
                    tv1.text = "My cases"
                    loadCases(Filter.MY)
                    false

                }
                R.id.cases_filter_closed -> {
                    val tv1 = text_currentCases as TextView
                    tv1.text = "Closed cases"
                    loadCases(Filter.CLOSED)
                    false
                }
                R.id.cases_filter_open -> {
                    val tv1 = text_currentCases as TextView
                    tv1.text = "Open cases"
                    loadCases(Filter.OPEN)
                    false
                }
                R.id.cases_filter_all -> {
                    val tv1 = text_currentCases as TextView
                    tv1.text = "All cases"
                    loadCases(Filter.ALL)
                    false // true to keep the Speed Dial open
                }
                else -> false
            }
        }
    }
}