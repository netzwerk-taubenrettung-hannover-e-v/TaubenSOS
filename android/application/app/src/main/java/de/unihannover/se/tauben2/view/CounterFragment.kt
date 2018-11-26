package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R.layout.fragment_counter
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
import kotlinx.android.synthetic.main.fragment_counter.*
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.text.SimpleDateFormat
import java.util.*

class CounterFragment : Fragment() {

    companion object {
        fun newInstance(): CounterFragment {
            return CounterFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)

        view.current_timestamp_value.text =
                SimpleDateFormat("dd.MM.yyyy – HH:mm", Locale.GERMANY).format(System.currentTimeMillis())

        view.counter_value.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 9999))

//        view.findViewById<View>(R.id.changedate_button).setOnClickListener(this)
//        view.findViewById<View>(R.id.resetdate_button).setOnClickListener(this)
//        view.findViewById<View>(R.id.send_count_button).setOnClickListener(this)

        view.plus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?:0) + 1
            counter_value.setText(value.toString())
        }

        view.minus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?:0) - 1
            counter_value.setText(value.toString())
        }

        return view
    }

}