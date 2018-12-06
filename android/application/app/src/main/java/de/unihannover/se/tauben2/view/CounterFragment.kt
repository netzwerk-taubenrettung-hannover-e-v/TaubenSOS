package de.unihannover.se.tauben2.view

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
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
import kotlinx.android.synthetic.main.fragment_counter.*
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.text.SimpleDateFormat
import java.util.*

class CounterFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var selectedDate: Calendar = Calendar.getInstance()

    companion object : Singleton<CounterFragment>() {
        override fun newInstance() = CounterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        var datePickerDialog = DatePickerDialog(context, this, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        var timePickerDialog = TimePickerDialog(context, this, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE),true)

        view.counter_value.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 9999))

        // OnClickListeners:
        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition()
        }

        view.plus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?:0) + 1
            counter_value.setText(value.toString())
        }

        view.minus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?:0) - 1
            counter_value.setText(value.toString())
        }

        view.changedate_button.setOnClickListener {
            timePickerDialog?.show()
            datePickerDialog?.show()
        }

        view.resetdate_button.setOnClickListener {
            setCurrentTimestamp()
        }

        view.send_count_button.setOnClickListener {

            if (mapsFragment.getSelectedPosition() == null) {
                // Error MSG: No location selected
            } else {

                if (counter_value.text.toString().toInt() == 0) {
                    // Warning MSG: Counter at 0
                }

                Log.d("COUNTINFO", selectedDate.toString())
                Log.d("COUNTINFO", mapsFragment.getSelectedPosition()!!.toString())
                Log.d("COUNTINFO", counter_value.text.toString())

                // Reset Page
                counter_value.setText(0)
                setCurrentTimestamp()
                // Success MSG
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        setCurrentTimestamp()
    }

    private fun refreshTextView () {
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