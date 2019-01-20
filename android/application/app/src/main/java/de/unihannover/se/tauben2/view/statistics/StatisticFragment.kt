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
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.stat.InjuryStat
import de.unihannover.se.tauben2.model.database.entity.stat.PigeonNumberStat
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.main.fragments.MapViewFragment
import de.unihannover.se.tauben2.viewmodel.StatsViewModel
import kotlinx.android.synthetic.main.fragment_statistic.*
import kotlinx.android.synthetic.main.fragment_statistic.view.*
import kotlinx.android.synthetic.main.statistic_data.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StatisticFragment : Fragment() {

    companion object {
        private val LOG_TAG = StatisticFragment::class.java.simpleName
    }

    // is dis bad?
    private lateinit var fragmentView: View

    private var selectedDateFrom: Calendar = Calendar.getInstance()
    private var selectedDateTo: Calendar = Calendar.getInstance()

    private lateinit var fromListener: DatePickerDialog.OnDateSetListener
    private lateinit var toListener: DatePickerDialog.OnDateSetListener

    private lateinit var northeast: LatLng
    private lateinit var southwest: LatLng

    private var datePickerDialogFrom: DatePickerDialog? = null
    private var datePickerDialogTo: DatePickerDialog? = null

    private var populationData: List<PopulationMarker>? = null
    private var mCurrentObservedPopulationData: LiveDataRes<List<PopulationMarker>>? = null
    private lateinit var mCurrentPopulationObserver: LoadingObserver<List<PopulationMarker>>

    private var reportData: List<PigeonNumberStat>? = null
    private var mCurrentObservedReportData: LiveDataRes<List<PigeonNumberStat>>? = null
    private lateinit var mCurrentReportObserver: LoadingObserver<List<PigeonNumberStat>>

    private var injuryData: InjuryStat? = null
    private var mCurrentObservedInjuryData: LiveDataRes<InjuryStat>? = null
    private lateinit var mCurrentInjuryObserver: LoadingObserver<InjuryStat>

    /*
    private var breedData: InjuryStat? = null
    private var mCurrentObservedBreedData: LiveDataRes<InjuryStat>? = null
    private lateinit var mCurrentBreedObserver: LoadingObserver<InjuryStat>
    */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_statistic, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment_statistic) as MapViewFragment
        northeast = mapsFragment.getNorthEast()
        southwest = mapsFragment.getSouthWest()


        // for debugging purposes remove later
        /*
        val vm = getViewModel(StatsViewModel::class.java)
        vm?.let { viewModel ->
            viewModel.getInjuryStats(0, 1547725671, 52.4, 9.1,
                    51.3, 10.0).observeForever {
                if (it.status == Resource.Status.SUCCESS) {
                    Log.d(LOG_TAG, it.data.toString())
                }
            }
        }*/

        // Set DatePicker
        createDateSelectListeners()
        selectedDateFrom.add(Calendar.MONTH, -1)

        setDatePicker()

        fragmentView.from_button.setOnClickListener {
            datePickerDialogFrom?.show()
            datePickerDialogFrom?.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark))
            datePickerDialogFrom?.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark))

        }
        fragmentView.to_button.setOnClickListener {
            datePickerDialogTo?.show()
            datePickerDialogTo?.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark))
            datePickerDialogTo?.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark))
        }

        fragmentView.collapse_button.setOnClickListener {

            val appbar = appbar as AppBarLayout
            val expand = appbar.height - appbar.bottom != 0

            appbar.setExpanded(expand)

            if (expand) {
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_up)
            } else {

                northeast = mapsFragment.getNorthEast()
                southwest = mapsFragment.getSouthWest()
                refreshCharts()
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_down)
            }
        }

        /*
        mCurrentPopulationObserver = LoadingObserver(successObserver = Observer {
            populationData = it
            refreshCharts()
        })
        */
        mCurrentInjuryObserver = LoadingObserver(successObserver = Observer {
            injuryData = it
            Log.d("BLUEDABE_INJURY", it.toString())
        })
        mCurrentReportObserver = LoadingObserver(successObserver = Observer {
            reportData = it
            Log.d("BLUEDABE_REPORT", it.toString())
        })
        /*
        mCurrentBreedObserver = LoadingObserver(successObserver = Observer {
            breedData = it
            refreshCharts()
        })
        */

        loadCases()

        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        refreshButtonLabel()
    }

    private fun loadCases() {

        getViewModel(StatsViewModel::class.java)?.let { viewModel ->
            //mCurrentObservedPopulationData?.removeObserver(mCurrentPopulationObserver)
            mCurrentObservedReportData?.removeObserver(mCurrentReportObserver)
            mCurrentObservedInjuryData?.removeObserver(mCurrentInjuryObserver)
            //mCurrentObservedBreedData?.removeObserver(mCurrentBreedObserver)

            // test
            //mCurrentObservedInjuryData = viewModel.getInjuryStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, 52.4, 9.1, 51.3, 10.0)
            //mCurrentObservedReportData = viewModel.getReportStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, 52.4, 9.1, 51.3, 10.0)

            //mCurrentObservedPopulationData = viewModel.getPopulationStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)
            mCurrentObservedReportData = viewModel.getReportStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)
            mCurrentObservedInjuryData = viewModel.getInjuryStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)
            //mCurrentObserverBreedData = viewModel.getBreedStat(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)

            //mCurrentObservedPopulationData?.observe(this, mCurrentPopulationObserver)
            mCurrentObservedReportData?.observe(this, mCurrentReportObserver)
            mCurrentObservedInjuryData?.observe(this, mCurrentInjuryObserver)
            //mCurrentObservedBreedData?.observe(this, mCurrentBreedObserver)
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
            setDatePicker()
        }
        toListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            selectedDateTo.set(year, month, day)
            refreshButtonLabel()
            refreshCharts()
            setDatePicker()
        }
    }

    private fun setDatePicker() {
        context?.let {
            datePickerDialogFrom = DatePickerDialog(it,
                    R.style.PickerTheme,
                    fromListener,
                    selectedDateFrom.get(Calendar.YEAR), selectedDateFrom.get(Calendar.MONTH),
                    selectedDateFrom.get(Calendar.DAY_OF_MONTH))

            datePickerDialogTo = DatePickerDialog(it,
                    R.style.PickerTheme,
                    toListener,
                    selectedDateTo.get(Calendar.YEAR), selectedDateTo.get(Calendar.MONTH),
                    selectedDateTo.get(Calendar.DAY_OF_MONTH))
        }

        datePickerDialogFrom?.datePicker?.maxDate = selectedDateTo.timeInMillis
        datePickerDialogTo?.datePicker?.maxDate = System.currentTimeMillis()
        datePickerDialogTo?.datePicker?.minDate = selectedDateFrom.timeInMillis
    }

    // CHARTS

    private fun refreshCharts() {

        loadCases()

        //createLineChart(fragmentView.population_linechart, getPopulationLineChartData())
        createLineChart(fragmentView.reported_linechart, getReportLineChartData())
        createPieChart(fragmentView.injury_piechart, getInjuryData())
        //createPieChart(fragmentView.breed_piechart, getBreedData())
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
        chart.axisLeft.axisMinimum = 0F
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
        chart.setEntryLabelColor(Color.WHITE)

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

        val data = ArrayList<Entry>()

        return data
    }

    private fun getReportLineChartData(): ArrayList<Entry> {

        var overall = 0
        val countedDays = TimeUnit.MILLISECONDS.toDays(selectedDateTo.timeInMillis - selectedDateFrom.timeInMillis)
        val data = ArrayList<Entry>()

        for (i in 0 until countedDays) {
            data.add(Entry(i.toFloat(), 0F))
        }

        reportData?.forEach { value ->

            val index = TimeUnit.SECONDS.toDays(value.day - selectedDateFrom.timeInMillis / 1000).toFloat()

            if (index >= 0 && index < data.size) data[index.toInt()] = Entry(index, value.count.toFloat())
            overall += value.count
        }

        val average = overall.toFloat() / countedDays.toFloat()
        fragmentView.reported_total.text = fragmentView.context.getString(R.string.in_average_reports, average)

        return data
    }

    private fun getInjuryData(): ArrayList<PieEntry> {

        Log.d("BLUEDABE_INJURYDATA", injuryData.toString())

        val data = ArrayList<PieEntry>()

        injuryData?.let {

            val labels = arrayOf(getString(R.string.injury_foot_leg),
                    getString(R.string.injury_wings),
                    getString(R.string.injury_head_eye),
                    getString(R.string.injury_paralyzed_flightless),
                    getString(R.string.injury_open_wound),
                    getString(R.string.injury_strings_feet),
                    getString(R.string.injury_fledgling),
                    getString(R.string.injury_other))

            val values = arrayOf(it.sumFootOrLeg.toFloat(),
                    it.sumWing.toFloat(),
                    it.sumHeadOrEye.toFloat(),
                    it.sumParalyzedOrFlightless.toFloat(),
                    it.sumOpenWound.toFloat(),
                    it.sumStrappedFeet.toFloat(),
                    it.sumFledgling.toFloat(),
                    it.sumOther.toFloat())

            for (i in 0 until values.size) {
                if (values[i] != 0F) data.add(PieEntry(values[i], labels[i]))
            }
        }

        return data
    }

    private fun getBreedData(): ArrayList<PieEntry> {

        val data = ArrayList<PieEntry>()
/*
        breedData?.let {

            val labels = arrayOf(getString(R.string.carrier_pigeon),
                    getString(R.string.common_wood_pigeon),
                    getString(R.string.feral_pigeon),
                    getString(R.string.wedding_pigeon),
                    getString(R.string.no_specification))

            val values = arrayOf(it.sumCarrierPigeon.toFloat(),
                    it.sumWoodPigeon.toFloat(),
                    it.sumFeralPigeon.toFloat(),
                    it.sumFancyPigeon.toFloat(),
                    it.sumNoSpecification.toFloat())

            for (i in 0 until values.size) {
                if (values[i] != 0F) data.add(PieEntry(values[i], labels[i]))
            }
        }
*/
        return data
    }

}