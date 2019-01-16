package de.unihannover.se.tauben2.view.statistics

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.appbar.AppBarLayout
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.Injury
import de.unihannover.se.tauben2.model.database.PigeonBreed
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.main.fragments.MapViewFragment
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.PopulationMarkerViewModel
import kotlinx.android.synthetic.main.fragment_statistic.*
import kotlinx.android.synthetic.main.fragment_statistic.view.*
import kotlinx.android.synthetic.main.statistic_data.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StatisticFragment : Fragment() {

    // is dis bad?
    private lateinit var fragmentView: View

    private var selectedDateFrom: Calendar = Calendar.getInstance()
    private var selectedDateTo: Calendar = Calendar.getInstance()

    private lateinit var fromListener: DatePickerDialog.OnDateSetListener
    private lateinit var toListener: DatePickerDialog.OnDateSetListener

    private var populationData: List<PopulationMarker>? = null
    private var mCurrentObservedPopulationData: LiveDataRes<List<PopulationMarker>>? = null
    private lateinit var mCurrentPopulationObserver: LoadingObserver<List<PopulationMarker>>

    private var reportData: List<Case>? = null
    private var mCurrentObservedReportData: LiveDataRes<List<Case>>? = null
    private lateinit var mCurrentReportObserver: LoadingObserver<List<Case>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_statistic, container, false)


        // Set Area on Map
        // val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        // Set DatePicker
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

        datePickerDialogFrom?.let {
            it.datePicker.maxDate = System.currentTimeMillis()
        }

        datePickerDialogTo?.let {
            it.datePicker.maxDate = System.currentTimeMillis()
        }

        fragmentView.from_button.setOnClickListener { datePickerDialogFrom?.show() }
        fragmentView.to_button.setOnClickListener { datePickerDialogTo?.show() }

        fragmentView.collapse_button.setOnClickListener {

            val appbar = appbar as AppBarLayout
            val expand = appbar.height - appbar.bottom != 0

            appbar.setExpanded(expand)

            if (expand)
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_up)
            else
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_down)
        }
        refreshCharts()

        mCurrentPopulationObserver = LoadingObserver(successObserver = Observer {
            populationData = it
            refreshCharts()
        })
        mCurrentReportObserver = LoadingObserver(successObserver = Observer {
            reportData = it
            refreshCharts()
        })

        loadCases()

        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        refreshButtonLabel()
    }

    private fun loadCases() {

        getViewModel(PopulationMarkerViewModel::class.java)?.let { viewModel ->
            mCurrentObservedPopulationData?.removeObserver(mCurrentPopulationObserver)
            mCurrentObservedPopulationData = viewModel.populationMarkers
            mCurrentObservedPopulationData?.observe(this, mCurrentPopulationObserver)
        }
        getViewModel(CaseViewModel::class.java)?.let { viewModel ->
            mCurrentObservedReportData?.removeObserver(mCurrentReportObserver)
            mCurrentObservedReportData = viewModel.cases
            mCurrentObservedReportData?.observe(this, mCurrentReportObserver)
        }
    }

    // DATE SELECTION

    private fun refreshButtonLabel() {
        fragmentView.from_button.text =
                SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(selectedDateFrom.timeInMillis)
        fragmentView.to_button.text =
                SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(selectedDateTo.timeInMillis)
    }

    private fun createDateSelectListeners() {
        fromListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            selectedDateFrom.set(year, month, day)
            refreshButtonLabel()
            refreshCharts()
        }
        toListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            selectedDateTo.set(year, month, day)
            refreshButtonLabel()
            refreshCharts()
        }
    }

    // CHARTS

    private fun refreshCharts() {
        createLineChart(fragmentView.population_linechart, getPopulationLineChartData())
        createLineChart(fragmentView.reported_linechart, getReportLineChartData())
        createPieChart(fragmentView.injury_piechart, getInjuryData())
        createPieChart(fragmentView.breed_piechart, getBreedData())
    }

    private fun resetLineChart(chart: LineChart) {
        chart.fitScreen()
        chart.data?.clearValues()
        chart.xAxis.valueFormatter = null
        chart.notifyDataSetChanged()
        chart.clear()
        chart.invalidate()
    }

    private fun createLineChart(chart: LineChart, data: ArrayList<Entry>) {

        resetLineChart(chart)

        val dataSet = LineDataSet(data, null)

        // Style
        val color = ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark)
        dataSet.color = color
        dataSet.setCircleColor(color)
        dataSet.setDrawFilled(true)
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        chart.xAxis.valueFormatter = AxisDateFormatter(selectedDateFrom, selectedDateTo)
        chart.xAxis.labelRotationAngle = -60f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun createPieChart(chart: PieChart, data: ArrayList<PieEntry>) {

        val dataSet = PieDataSet(data, null)

        // Style
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c)
        dataSet.colors = colors

        chart.isDrawHoleEnabled = false
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setEntryLabelColor(Color.BLACK)

        chart.data = PieData(dataSet)
        chart.invalidate()
    }

    // GET CHART DATA

    private fun getExampleLineChartData(): ArrayList<Entry> {

        val testData = ArrayList<Entry>()
        var randNumber = Math.random() * 500

        val amountOfDays = TimeUnit.MILLISECONDS.toDays(Math.abs(selectedDateTo.timeInMillis - selectedDateFrom.timeInMillis)).toInt()

        for (i in 0..amountOfDays) {

            randNumber += 30 * when (randNumber.toInt()) {
                in 450..500 -> -Math.random()
                in 0..50 -> Math.random()
                else -> (-1.0 + (Math.random() * 2))
            }
            // testData.add(XAxis, YAxis)
            if (i % 2 == 0) {
                testData.add(Entry(i.toFloat(), randNumber.toFloat()))
            }
        }

        return testData
    }

    private fun getPopulationLineChartData(): ArrayList<Entry> {

        var overall = 0
        var countedDays = 0

        val data = ArrayList<Entry>()
        val currentDate = selectedDateFrom.clone() as Calendar

        while (currentDate != selectedDateTo) {

            var counter = 0

            populationData?.forEach {
                it.values.forEach {value ->

                    val date = Calendar.getInstance().apply { timeInMillis = value.timestamp * 1000 }

                    if (date.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR) &&
                            date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                        counter += value.pigeonCount
                    }
                }
            }

            if (counter != 0) {
                data.add(Entry((TimeUnit.MILLISECONDS.toDays(Math.abs(currentDate.timeInMillis - selectedDateFrom.timeInMillis)).toInt() + 1).toFloat(), counter.toFloat()))
                overall += counter
                countedDays++
            }

            currentDate.add(Calendar.DATE, 1)
        }

        var average = 0
        if (countedDays > 0) average = overall/countedDays
        fragmentView.population_total.text = fragmentView.context.getString(R.string.in_average_population, average)

        return data
    }

    var breeds = listOf<PigeonBreed>()
    var injuries = listOf<Injury?>()


    private fun getReportLineChartData(): ArrayList<Entry> {

        var overall = 0

        val data = ArrayList<Entry>()
        val currentDate = selectedDateFrom.clone() as Calendar

        while (currentDate != selectedDateTo) {

            var counter = 0

            reportData?.forEach {

                val date = Calendar.getInstance().apply { timeInMillis = it.timestamp * 1000 }
                if (date.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR) &&
                        date.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                    counter++
                }

                breeds += it.getPigeonBreed()
                injuries += it.injury
            }

            if (counter != 0) {
                data.add(Entry((TimeUnit.MILLISECONDS.toDays(Math.abs(currentDate.timeInMillis - selectedDateFrom.timeInMillis)).toInt() + 1).toFloat(), counter.toFloat()))
                overall += counter
            }

            currentDate.add(Calendar.DATE, 1)
        }

        var average = overall.toFloat() / (TimeUnit.MILLISECONDS.toDays(Math.abs(selectedDateTo.timeInMillis - selectedDateFrom.timeInMillis)).toInt() + 1).toFloat()
        fragmentView.reported_total.text = fragmentView.context.getString(R.string.in_average_reports, average)

        return data
    }

    private fun getInjuryData(): ArrayList<PieEntry> {

        val testData = ArrayList<PieEntry>()

        val injuries = arrayOf("Foot or Leg",
                "Wings",
                "Head or eye",
                "Paralysed or flightless",
                "Open wound",
                "Strapped feet",
                "Fledling",
                "Other")

        for (i in 0 until 8) {
            // testdata.add(AMOUNT, LABEL)
            testData.add(PieEntry((Math.abs(Math.random() * 10)).toFloat(), injuries[i]))
        }

        return testData
    }

    private fun getBreedData(): ArrayList<PieEntry> {

        val data = ArrayList<PieEntry>()

        var breedEntries = mutableListOf<PieEntry>()

        breeds.forEach {pb ->


            var entry : PieEntry? = null
            breedEntries.forEach {
                if (pb.getTitle() == it.label) {
                    entry = it
                }
            }
            entry?.let {
                val value = it.value + 1
                breedEntries.remove(it)
                breedEntries.add(PieEntry(value, pb.getTitle()))
            } ?: run {
                breedEntries.add(PieEntry(1F, pb.getTitle()))
            }
        }

        breedEntries.forEach {
            data.add(it)
        }

        return data
    }

}