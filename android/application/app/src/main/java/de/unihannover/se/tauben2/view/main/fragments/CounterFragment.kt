package de.unihannover.se.tauben2.view.main.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
import de.unihannover.se.tauben2.viewmodel.PopulationMarkerViewModel
import kotlinx.android.synthetic.main.fragment_counter.*
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.text.SimpleDateFormat
import java.util.*
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.Singleton

class CounterFragment : Fragment() {

    private var selectedDate: Calendar = Calendar.getInstance()

    private var mCurrentObservedData: LiveDataRes<List<PopulationMarker>>? = null
    private lateinit var mCurrentMapObserver: LoadingObserver<List<PopulationMarker>>

    var mSelectedMarkerID: Int? = null

    companion object : Singleton<CounterFragment>() {
        override fun newInstance() = CounterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment


        view.cancel_marker_button.hide()
        view.confirm_marker_button.hide()

        // OnClickListeners:
        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition(null)
            mapsFragment.focusSelectedPosition()
            mapsFragment.chooseRadius()
            view.confirm_marker_button.show()
            view.cancel_marker_button.show()
        }

        view.cancel_marker_button.setOnClickListener {
            mapsFragment.unfocusSelectedPosition()
            mapsFragment.removeCircle()
            mapsFragment.removeSelectedPosition()
            view.confirm_marker_button.hide()
            view.cancel_marker_button.hide()
        }

        view.confirm_marker_button.setOnClickListener {
            var position = mapsFragment.getSelectedPosition()
            var circle = mapsFragment.getCircle()
            position?.let { pos ->
                circle?.let { circle ->
                    sendMarker(pos.latitude, pos.longitude, circle.radius)
                }
            }
            mapsFragment.unfocusSelectedPosition()
            mapsFragment.removeCircle()
            mapsFragment.removeSelectedPosition()
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
                        listOf<CounterValue>()))

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

    override fun onStart() {
        super.onStart()
    }
}