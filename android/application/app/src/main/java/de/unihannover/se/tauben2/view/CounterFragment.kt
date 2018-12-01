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
import com.google.android.gms.maps.model.LatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
import kotlinx.android.synthetic.main.fragment_counter.*
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*

class CounterFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var datePickerDialog : DatePickerDialog? = null
    var timePickerDialog : TimePickerDialog? = null

    companion object : Singleton<CounterFragment>() {
        override fun newInstance() = CounterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment
        val c = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(context, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        timePickerDialog = TimePickerDialog(context, this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),true)

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

                Log.d("COUNTINFO", view?.current_timestamp_value?.text.toString())
                Log.d("COUNTINFO", mapsFragment.getSelectedPosition()!!.toString())
                Log.d("COUNTINFO", counter_value.text.toString())
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        setCurrentTimestamp()
    }

    private fun setCurrentTimestamp() {
        view?.current_timestamp_value?.text =
                SimpleDateFormat("dd.MM.yyyy – HH:mm", Locale.GERMANY).format(System.currentTimeMillis())
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        view?.current_timestamp_value?.text = (view?.current_timestamp_value?.text)?.replaceRange(0, 10, String.format("%02d.%02d.%02d", p3, p2 + 1, p1))
        timePickerDialog?.show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        view?.current_timestamp_value?.text = (view?.current_timestamp_value?.text)?.replaceRange(13, 18, String.format("%02d:%02d", p1, p2))
    }

}