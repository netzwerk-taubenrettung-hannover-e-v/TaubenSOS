package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
import kotlinx.android.synthetic.main.fragment_counter.*
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.text.SimpleDateFormat
import java.util.*

class CounterFragment : Fragment() {

    companion object : Singleton<CounterFragment>() {
        override fun newInstance() = CounterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

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

        view.resetdate_button.setOnClickListener {
            setCurrentTimestamp()
        }

        view.send_count_button.setOnClickListener {

            if (mapsFragment.getSelectedPosition() == null) {
                // Error MSG: No location selected
            }
            if (counter_value.text.toString().toInt() == 0) {
                // Warning MSG: Counter at 0
            }

            Log.d("COUNTINFO", view?.current_timestamp_value?.text.toString())
            Log.d("COUNTINFO", mapsFragment.getSelectedPosition()!!.toString())
            Log.d("COUNTINFO", counter_value.text.toString())
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

}