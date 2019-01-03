package de.unihannover.se.tauben2.view.fragments


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
//import de.unihannover.se.tauben2.model.database.entity.PigeonCounter
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
//import de.unihannover.se.tauben2.viewmodel.PigeonCounterViewModel
import kotlinx.android.synthetic.main.fragment_counter_info.*
import kotlinx.android.synthetic.main.fragment_counter_info.view.*
import java.text.SimpleDateFormat
import java.util.*
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 */

class CounterInfoFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_counter_info, container, false)

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

        //These are test dates for the population counter graph
        val calendar = Calendar.getInstance()
        val d1 = calendar.time
        calendar.add(Calendar.MONTH, 2)
        val d2 = calendar.time
        calendar.add(Calendar.MONTH, 2)
        val d3 = calendar.time
        calendar.add(Calendar.MONTH, 2)
        val d4 = calendar.time
        calendar.add(Calendar.MONTH, 2)
        val d5 = calendar.time

        val series = LineGraphSeries<DataPoint>(arrayOf<DataPoint>(
                DataPoint(d1, 7.0),
                DataPoint(d2, 5.0),
                DataPoint(d3, 6.0),
                DataPoint(d4, 12.0),
                DataPoint(d5, 20.0)))

        //Graph View Configuration
        series.isDrawBackground = true
        series.isDrawDataPoints = true
        view.graph.addSeries(series)

        view.graph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(activity)
        view.graph.gridLabelRenderer.numHorizontalLabels = 3
        view.graph.gridLabelRenderer.padding = 32
        view.graph.gridLabelRenderer.setHumanRounding(false)

        view.graph.viewport.isScalable = false
        view.graph.viewport.isXAxisBoundsManual = true
        view.graph.viewport.setMinX(d1.time.toDouble())
        view.graph.viewport.setMaxX(d5.time.toDouble())
        view.graph.viewport.isYAxisBoundsManual = true
        view.graph.viewport.setMinY(0.0)
        view.graph.viewport.setMaxY(40.0)


        //OnClickListeners
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

            /*if (mapsFragment.getSelectedPosition() == null) {
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

                    // Reset Page
                    counter_value.setText("0")
                    setCurrentTimestamp()
                    // Success MSG
                    setSnackBar(view, "Taubenanzahl erfolgreich eigetragen.")
                }
            }
            */
        }

        return view
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
