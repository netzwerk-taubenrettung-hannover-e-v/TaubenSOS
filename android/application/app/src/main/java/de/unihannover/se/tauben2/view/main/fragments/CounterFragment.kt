package de.unihannover.se.tauben2.view.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.multiLet
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.PopulationMarkerViewModel
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.util.*

class CounterFragment : BaseMainFragment(R.string.counter) {

    private var mCurrentObservedData: LiveDataRes<List<PopulationMarker>>? = null
    private lateinit var mCurrentMapObserver: LoadingObserver<List<PopulationMarker>>

    var mSelectedMarkerID: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        if(mapsFragment.circle == null){
            view.cancel_marker_button.hide()
            view.confirm_marker_button.hide()
        }

        // OnClickListeners:
        view.set_position_button.setOnClickListener {
            mapsFragment.apply {
                selectPosition(null)
                focusSelectedPosition()
                chooseRadius()
            }
            view.confirm_marker_button.show()
            view.cancel_marker_button.show()
        }

        view.cancel_marker_button.setOnClickListener {
            mapsFragment.apply {
                unfocusSelectedPosition()
                removeCircle()
                removeSelectedPosition()
            }
            view.confirm_marker_button.hide()
            view.cancel_marker_button.hide()
        }

        view.confirm_marker_button.setOnClickListener {

            mapsFragment.apply {

                multiLet(getSelectedPosition(), circle) { pos, circ ->
                    sendMarker(pos.latitude, pos.longitude, circ.radius)
                }

                unfocusSelectedPosition()
                removeCircle()
                removeSelectedPosition()
            }

            view.confirm_marker_button.hide()
            view.cancel_marker_button.hide()
        }

        mCurrentMapObserver = LoadingObserver(successObserver = mapsFragment)

        loadCounters()

        return view
    }

    private fun sendMarker(latitude: Double, longitude: Double, radius: Double) {
        val vm = getViewModel(PopulationMarkerViewModel::class.java)
        vm?.let {

            if (mSelectedMarkerID != null) {
                it.postCounterValue(CounterValue(23, mSelectedMarkerID ?: return, 456))
                mSelectedMarkerID = null
            } else
            // TODO implement drawing radius, adding description text in ui

            it.postNewMarker(PopulationMarker(latitude, longitude, "Placeholder", -1, radius,
                    listOf()))

        }
    }

    private fun loadCounters() {

        getViewModel(PopulationMarkerViewModel::class.java)?.let { viewModel ->

            // Remove old Observers
            mCurrentObservedData?.removeObserver(mCurrentMapObserver)

            mCurrentObservedData = viewModel.populationMarkers

            mCurrentObservedData?.observe(this, mCurrentMapObserver)
        }
    }
}