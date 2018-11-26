package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter
import kotlinx.android.synthetic.main.fragment_counter.*

class CounterFragment : Fragment(), View.OnClickListener {

   private var day : Int = 0
   private var month : Int = 0
   private var year : Int = 0
   private var hour : Int = 0
   private var minute : Int = 0


    companion object {
        fun newInstance(): CounterFragment {
            return CounterFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)

        setCurrentTime(view)

        view.findViewById<View>(R.id.changedate_button).setOnClickListener(this)
        view.findViewById<View>(R.id.resetdate_button).setOnClickListener(this)
        view.findViewById<View>(R.id.plus_button).setOnClickListener(this)
        view.findViewById<View>(R.id.minus_button).setOnClickListener(this)
        view.findViewById<View>(R.id.send_count_button).setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {

        when (view) {

            changedate_button -> {
                // change Date and Time fragment or popup
            }
            resetdate_button -> {
                // reset Date and Time to actual timestamp
            }
            plus_button -> {
                val value = if (counter_value.text.isNullOrEmpty()) {
                    0
                } else {
                    Integer.parseInt(counter_value.text.toString())
                }
                if (value < 9999) {
                    counter_value.setText((value + 1).toString())
                }
            }
            minus_button -> {
                val value = if (counter_value.text.isNullOrEmpty()) {
                    0
                } else {
                    Integer.parseInt(counter_value.text.toString())
                }
                if (value > 0) {
                    counter_value.setText((value - 1).toString())
                }
            }
            send_count_button -> {
                // send data to the server
            }
        }

    }

    private fun setCurrentTime(view: View?) {

        day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
        month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

        hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        minute = java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE)

        setTime(view, day, month, year, hour, minute)
    }

    private fun setTime(view: View?, day : Int, month : Int, year : Int, hour : Int, minute : Int) {
        view?.findViewById<TextView>(R.id.current_timestamp_value)?.text = String.format("%02d.%02d.%04d %02d:%02d Uhr", day, month, year, hour, minute)
    }

}

