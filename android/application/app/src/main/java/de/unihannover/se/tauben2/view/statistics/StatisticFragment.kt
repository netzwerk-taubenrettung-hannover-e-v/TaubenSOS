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
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StatisticFragment : Fragment() {

    // is dis bad?
    private lateinit var fragmentView : View

    private var selectedDateFrom: Calendar = Calendar.getInstance()
    private var selectedDateTo: Calendar = Calendar.getInstance()

    private lateinit var fromListener: DatePickerDialog.OnDateSetListener
    private lateinit var toListener: DatePickerDialog.OnDateSetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_statistic, container, false)

        createDateSelectListeners()
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

        fragmentView.from_button.setOnClickListener { datePickerDialogFrom?.show() }
        fragmentView.to_button.setOnClickListener { datePickerDialogTo?.show() }

        fragmentView.collapse_button.setOnClickListener {

            val appbar = appbar as AppBarLayout
            val expand = appbar.height - appbar.bottom != 0

            appbar.setExpanded(expand)

            if(expand)
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_up)
            else
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_down)
        }

        refreshCharts()

        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        refreshButtonLabel()
    }

    // DATE SELECTION

    private fun refreshButtonLabel() {
        fragmentView.from_button.text =
                SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(selectedDateFrom.timeInMillis)
        fragmentView.to_button.text =
                SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(selectedDateTo.timeInMillis)
    }

    private fun createDateSelectListeners () {
        fromListener = DatePickerDialog.OnDateSetListener {_, year, month, day ->
            selectedDateFrom.set(year, month, day)
            refreshButtonLabel()
            refreshCharts()
        }
        toListener = DatePickerDialog.OnDateSetListener {_, year, month, day ->
            selectedDateTo.set(year, month, day)
            refreshButtonLabel()
            refreshCharts()
        }
    }

    // CHARTS

    private fun refreshCharts() {
        createPopulationLineChart()
        createReportedDovesLineChart()
    }

    private fun resetLineChart(chart : LineChart) {
        chart.fitScreen()
        chart.data?.clearValues()
        chart.xAxis.valueFormatter = null
        chart.notifyDataSetChanged()
        chart.clear()
        chart.invalidate()
    }

    private fun setLineChartStyle (chart : LineChart, dataSet : LineDataSet) {

        val color = ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark)
        dataSet.color = color
        dataSet.setCircleColor(color)

        chart.xAxis.valueFormatter = AxisDateFormatter(selectedDateFrom, selectedDateTo)
        chart.xAxis.labelRotationAngle = -60f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
    }

    private fun createPopulationLineChart () {

        val chart = fragmentView.population_linechart
        resetLineChart(chart)

        // Get Values
        val dataSet = LineDataSet(getExampleData(), null)

        setLineChartStyle(chart, dataSet)

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun createReportedDovesLineChart () {

        val chart = fragmentView.reported_linechart
        resetLineChart(chart)

        // Get Values
        val dataSet = LineDataSet(getExampleData(), null)

        setLineChartStyle(chart, dataSet)

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun getExampleData() : ArrayList<Entry> {

        val testData = ArrayList<Entry>()
        var randNumber = Math.random() * 500

        val amountOfDays = TimeUnit.MILLISECONDS.toDays(Math.abs(selectedDateTo.timeInMillis - selectedDateFrom.timeInMillis)).toInt()

        for (i in 0..amountOfDays) {

            randNumber += 30 * when (randNumber.toInt()) {
                in 450..500 -> -Math.random()
                in 0..50 -> Math.random()
                else -> (-1.0 + (Math.random() * 2))
            }
            testData.add(Entry(i.toFloat(), randNumber.toFloat()))
        }

        return testData
    }

}