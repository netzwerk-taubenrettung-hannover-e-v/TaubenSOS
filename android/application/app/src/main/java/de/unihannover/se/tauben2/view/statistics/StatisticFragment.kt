package de.unihannover.se.tauben2.view.statistics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_statistic.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_statistic.view.*
import kotlinx.android.synthetic.main.statistic_data.view.*
import java.text.SimpleDateFormat
import java.util.*


class StatisticFragment : Fragment() {

    private var selectedDateFrom: Calendar = Calendar.getInstance()
    private var selectedDateTo: Calendar = Calendar.getInstance()

    private lateinit var fromListener: DatePickerDialog.OnDateSetListener
    private lateinit var toListener: DatePickerDialog.OnDateSetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)

        createListeners()
        selectedDateFrom.add(Calendar.MONTH, -1)

        val datePickerDialogFrom = context?.let {
            DatePickerDialog(it, fromListener,
                    selectedDateFrom.get(Calendar.YEAR), selectedDateFrom.get(Calendar.MONTH),
                    selectedDateFrom.get(Calendar.DAY_OF_MONTH))
        }

        val datePickerDialogTo = context?.let {
            DatePickerDialog(it, toListener,
                    selectedDateTo.get(Calendar.YEAR), selectedDateTo.get(Calendar.MONTH),
                    selectedDateTo.get(Calendar.DAY_OF_MONTH))
        }

        view.from_button.setOnClickListener { datePickerDialogFrom?.show() }
        view.to_button.setOnClickListener { datePickerDialogTo?.show() }

        view.collapse_button.setOnClickListener {

            val appbar = appbar as AppBarLayout
            val expand = appbar.height - appbar.bottom != 0

            appbar.setExpanded(expand)

            if(expand)
                view.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_up)
            else
                view.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_down)
        }

        createLineChart(view, view.population_linechart)
        createLineChart(view, view.reported_linechart)


        return view
    }

    override fun onStart() {
        super.onStart()
        refreshButtonLabel()
    }

    private fun refreshButtonLabel() {
        view?.from_button?.text =
                SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(selectedDateFrom.timeInMillis)
        view?.to_button?.text =
                SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(selectedDateTo.timeInMillis)
    }

    private fun createListeners () {
        fromListener = DatePickerDialog.OnDateSetListener {_, year, month, day ->
            selectedDateFrom.set(year, month, day)
            refreshButtonLabel()
        }
        toListener = DatePickerDialog.OnDateSetListener {_, year, month, day ->
            selectedDateTo.set(year, month, day)
            refreshButtonLabel()
        }
    }

    // CHARTS

    private fun createLineChart (view : View, chart : LineChart) {

        val testData = ArrayList<Entry>()
        var randNumber = Math.random() * 500
        for (i in 0..100) {
            randNumber *= (0.8 + (Math.random() * 0.4))
            testData.add(Entry(i.toFloat(), randNumber.toFloat()))
        }

        val dataSet = LineDataSet(testData, null)

        // Styling DataSet
        val color = ContextCompat.getColor(view.context, R.color.colorPrimaryDark)
        dataSet.color = color
        dataSet.setCircleColor(color)

        val lineData = LineData(dataSet)

        chart.data = lineData

        // Styling Graph
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        chart.invalidate()
    }


}