package de.unihannover.se.tauben2.view.fragments

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

class CounterFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

        val datePickerDialog = context?.let {
            DatePickerDialog(it, this,
                    selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH))
        }
        val timePickerDialog = context?.let {
            TimePickerDialog(it, this,
                    selectedDate.get(Calendar.HOUR_OF_DAY),
                    selectedDate.get(Calendar.MINUTE), true)
        }

        view.counter_value.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 9999))

        // OnClickListeners:
        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition(null)
        }

        view.plus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?: 0) + 1
            counter_value.setText(value.toString())
        }

        view.minus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?: 0) - 1
            counter_value.setText(value.toString())
        }

        view.changedate_button.setOnClickListener {
            timePickerDialog?.show()
            datePickerDialog?.show()
        }

        view.resetdate_button.setOnClickListener {
            setCurrentTimestamp()
        }

        view.infoButtonCounter.setOnClickListener {
            //Pop up for more info
            val alertDialogBuilder = AlertDialog.Builder(
                    context)

            alertDialogBuilder.setTitle("Tauben zählen")

            alertDialogBuilder
                    .setMessage(R.string.tauben_zählen_info)

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }

        view.send_count_button.setOnClickListener {

            if (mapsFragment.getSelectedPosition() == null) {
                // Error MSG: No location selected
                setSnackBar(view, "Bitte Position auf der Karte eintragen.")
            } else {
                if (counter_value.text.toString().toInt() == 0) {
                    // Warning MSG: Counter at 0
                    setSnackBar(view, "Bitte Taubenanzahl eintragen.")
                } else {
                    Log.d("COUNTINFO", selectedDate.toString())
                    Log.d("COUNTINFO", mapsFragment.getSelectedPosition()!!.toString())
                    Log.d("COUNTINFO", counter_value.text.toString())

                    mapsFragment.getSelectedPosition()?.run {
                        val count = counter_value.text.toString().toInt()
                        sendMarker(latitude, longitude, selectedDate.timeInMillis / 1000, count)
                    }

                    // Reset Page
                    counter_value.setText("0")
                    setCurrentTimestamp()
                    // Success MSG
                    setSnackBar(view, "Taubenanzahl erfolgreich eigetragen.")
                }
            }
        }

        mCurrentMapObserver = LoadingObserver(successObserver = mapsFragment)

        loadCounters()

        return view
    }

    private fun sendMarker(latitude: Double, longitude: Double, timestamp: Long, count: Int) {
        val vm = getViewModel(PopulationMarkerViewModel::class.java)
        vm?.let {

            if (mSelectedMarkerID != null) {
                it.postCounterValue(CounterValue(count, mSelectedMarkerID ?: return, timestamp))
                mSelectedMarkerID = null
            } else
            // TODO implement drawing radius, adding description text in ui
                it.postNewMarker(PopulationMarker(latitude, longitude, "Placeholder", -1, 400.0,
                        listOf(CounterValue(count, -1, timestamp))))

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
        setCurrentTimestamp()
    }

    private fun refreshTextView() {
        view?.current_timestamp_value?.text =
                SimpleDateFormat("dd.MM.yy, HH:mm", Locale.GERMANY).format(selectedDate.timeInMillis)
    }

    private fun setCurrentTimestamp() {
        selectedDate = Calendar.getInstance()
        refreshTextView()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        selectedDate.set(year, month, day)
        refreshTextView()
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
        selectedDate.set(Calendar.MINUTE, minute)
        refreshTextView()
    }
}