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
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.stat.BreedStat
import de.unihannover.se.tauben2.model.database.entity.stat.InjuryStat
import de.unihannover.se.tauben2.model.database.entity.stat.PigeonNumberStat
import de.unihannover.se.tauben2.model.database.entity.stat.PopulationStat
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
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import de.unihannover.se.tauben2.view.main.fragments.BaseMainFragment


class StatisticFragment : BaseMainFragment(R.string.graphs) {

    companion object {
        private val LOG_TAG = StatisticFragment::class.java.simpleName
    }

    private lateinit var fragmentView: View

    private var selectedDateFrom: Calendar = Calendar.getInstance()
    private var selectedDateTo: Calendar = Calendar.getInstance()

    private lateinit var fromListener: DatePickerDialog.OnDateSetListener
    private lateinit var toListener: DatePickerDialog.OnDateSetListener

    private lateinit var northeast: LatLng
    private lateinit var southwest: LatLng

    private var datePickerDialogFrom: DatePickerDialog? = null
    private var datePickerDialogTo: DatePickerDialog? = null

    private var mCurrentObservedPopulationData: LiveDataRes<List<PopulationStat>>? = null
    private lateinit var mCurrentPopulationObserver: LoadingObserver<List<PopulationStat>>

    private var mCurrentObservedReportData: LiveDataRes<List<PigeonNumberStat>>? = null
    private lateinit var mCurrentReportObserver: LoadingObserver<List<PigeonNumberStat>>

    private var mCurrentObservedInjuryData: LiveDataRes<InjuryStat>? = null
    private lateinit var mCurrentInjuryObserver: LoadingObserver<InjuryStat>

    private var mCurrentObservedBreedData: LiveDataRes<BreedStat>? = null
    private lateinit var mCurrentBreedObserver: LoadingObserver<BreedStat>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_statistic, container, false)
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment_statistic) as MapViewFragment

        northeast = mapsFragment.getNorthEast()
        southwest = mapsFragment.getNorthEast()

        //northeast = LatLng(54.447689, 16.107250)
        //southwest = LatLng(48.140436, 4.521094)


        // for debugging purposes remove later
        /*
        val vm = getViewModel(StatsViewModel::class.java)
        vm?.let { viewModel ->
            viewModel.getBreedStat(0, 1547725671, 54.447689, 16.107250,
                    48.140436, 4.521094).observeForever {
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
                Log.d("BLUEDABE_EXPANDED", "------------------------------------------------------------------------")
                loadCases()
                fragmentView.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_down)
            }
        }

        mCurrentPopulationObserver = LoadingObserver(successObserver = Observer {
            createLineChart(fragmentView.population_linechart, getPopulationLineChartData(it), null)
        })
        mCurrentReportObserver = LoadingObserver(successObserver = Observer {
            createReportLineChart(fragmentView.reported_linechart, getReportLineChartData(it))
        })
        mCurrentInjuryObserver = LoadingObserver(successObserver = Observer {
            createPieChart(fragmentView.injury_piechart, getInjuryData(it))
        })
        mCurrentBreedObserver = LoadingObserver(successObserver = Observer {
            createPieChart(fragmentView.breed_piechart, getBreedData(it))
        })

       // loadCases()

        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        refreshButtonLabel()
    }

    private fun loadCases() {

        getViewModel(StatsViewModel::class.java)?.let { viewModel ->
            mCurrentObservedPopulationData?.removeObserver(mCurrentPopulationObserver)
            mCurrentObservedReportData?.removeObserver(mCurrentReportObserver)
            mCurrentObservedInjuryData?.removeObserver(mCurrentInjuryObserver)
            mCurrentObservedBreedData?.removeObserver(mCurrentBreedObserver)

            mCurrentObservedPopulationData = viewModel.getPopulationStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude,
                    southwest.latitude, southwest.longitude)
            mCurrentObservedReportData = viewModel.getReportStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude,
                    southwest.latitude, southwest.longitude)
            mCurrentObservedInjuryData = viewModel.getInjuryStats(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude,
                    southwest.latitude, southwest.longitude)
            mCurrentObservedBreedData = viewModel.getBreedStat(selectedDateFrom.timeInMillis / 1000, selectedDateTo.timeInMillis / 1000, northeast.latitude, northeast.longitude,
                    southwest.latitude, southwest.longitude)

            mCurrentObservedPopulationData?.observe(this, mCurrentPopulationObserver)
            mCurrentObservedReportData?.observe(this, mCurrentReportObserver)
            mCurrentObservedInjuryData?.observe(this, mCurrentInjuryObserver)
            mCurrentObservedBreedData?.observe(this, mCurrentBreedObserver)
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
            setDatePicker()
            refreshButtonLabel()
            Log.d("BLUEDABE_DATECHANGE", "------------------------------------------------------------------------")
            loadCases()
        }
        toListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            selectedDateTo.set(year, month, day)
            setDatePicker()
            refreshButtonLabel()
            Log.d("BLUEDABE_DATECHANGE", "------------------------------------------------------------------------")
            loadCases()
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

    private fun resetLineChart(chart: LineChart) {
        chart.fitScreen()
        chart.data?.clearValues()
        chart.xAxis.valueFormatter = null
        chart.notifyDataSetChanged()
        chart.clear()
        chart.invalidate()
    }

    private fun createReportLineChart(chart: LineChart, data: ArrayList<ArrayList<Entry>>) {
        createLineChart(chart, data[0], data[1])
    }

    private fun createLineChart(chart: LineChart, data1: ArrayList<Entry>, data2: ArrayList<Entry>?) {

        resetLineChart(chart)

        val dataSet1 = LineDataSet(data1, null)

        // Style
        val color1 = ContextCompat.getColor(fragmentView.context, R.color.colorPrimaryDark)
        dataSet1.color = color1
        dataSet1.setCircleColor(color1)
        dataSet1.setDrawFilled(true)
        dataSet1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER


        chart.xAxis.valueFormatter = AxisDateFormatter(selectedDateFrom, selectedDateTo)
        chart.axisLeft.axisMinimum = 0F
        chart.xAxis.labelRotationAngle = -60f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false

        if (data2 != null) {

            val dataSet2 = LineDataSet(data2, null)

            val color2 = ContextCompat.getColor(fragmentView.context, R.color.Gray)
            dataSet2.color = color2
            dataSet2.setCircleColor(color2)
            dataSet2.setDrawFilled(true)
            dataSet2.fillColor = color2
            dataSet2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

            val lines = ArrayList<ILineDataSet>()
            lines.add(dataSet1)
            lines.add(dataSet2)
            chart.data = LineData(lines)
        } else {
            chart.data = LineData(dataSet1)
        }

        chart.invalidate()
    }

    private fun createPieChart(chart: PieChart, data: ArrayList<PieEntry>) {

        val dataSet = PieDataSet(data, null)

        // Style
        val colors = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c)
        dataSet.colors = colors
        dataSet.valueFormatter = PercentFormatter()
        dataSet.valueTextSize = 18F

        chart.isDrawHoleEnabled = false
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(18F)

        chart.data = PieData(dataSet)
        chart.invalidate()
    }

    // GET CHART DATA

    private fun getPopulationLineChartData(populationData: List<PopulationStat>): ArrayList<Entry> {

        Log.d("BLUEDABE_POPULATIONDATA", populationData.toString())

        var overall = 0
        val days = TimeUnit.MILLISECONDS.toDays(selectedDateTo.timeInMillis - selectedDateFrom.timeInMillis)
        var countedDays = 0
        val data = ArrayList<Entry>()

        populationData.forEach { value ->

            val index = TimeUnit.SECONDS.toDays(value.day - selectedDateFrom.timeInMillis / 1000).toFloat()

            if (index >= 0 && index < days) {
                data.add(Entry(index, value.count.toFloat()))
                overall += value.count
                countedDays++
            }

        }

        var average = 0
        if (countedDays > 0) average = overall / countedDays
        fragmentView.population_total.text = fragmentView.context.getString(R.string.in_average_population, average)

        return data
    }

    private fun getReportLineChartData(reportData: List<PigeonNumberStat>): ArrayList<ArrayList<Entry>> {

        Log.d("BLUEDABE_REPORTDATA", reportData.toString())

        var overall = 0
        val countedDays = TimeUnit.MILLISECONDS.toDays(selectedDateTo.timeInMillis - selectedDateFrom.timeInMillis)
        val data = ArrayList<ArrayList<Entry>>()
        val all = ArrayList<Entry>()
        val notFoundOrDead = ArrayList<Entry>()


        /* DONT IGNORE 0 as value
        for (i in 0 until countedDays) {
            data.add(Entry(i.toFloat(), 0F))
        }

        reportData?.forEach { value ->

            val index = TimeUnit.SECONDS.toDays(value.day - selectedDateFrom.timeInMillis / 1000).toFloat()

            if (index >= 0 && index < data.size) {
                data[index.toInt()] = Entry(index, value.count.toFloat())
                overall += value.count
            }
        }
        */

        // IGNORE 0 as value
        reportData.forEach { value ->

            val index = TimeUnit.SECONDS.toDays(value.day - selectedDateFrom.timeInMillis / 1000).toFloat()

            if (index >= 0 && index < countedDays) {
                all.add(Entry(index, value.count.toFloat()))
                notFoundOrDead.add(Entry(index, value.sumFoundDead.toFloat() + value.sumNotFound.toFloat()))
                overall += value.count
            }

        }

        val average = overall.toFloat() / countedDays.toFloat()
        fragmentView.reported_total.text = fragmentView.context.getString(R.string.in_average_reports, average)

        data.add(all)
        data.add(notFoundOrDead)

        return data
    }

    private fun getInjuryData(injuryData: InjuryStat): ArrayList<PieEntry> {

        Log.d("BLUEDABE_INJURYDATA", injuryData.toString())

        val data = ArrayList<PieEntry>()


        val labels = arrayOf(getString(R.string.injury_foot_leg),
                getString(R.string.injury_wings),
                getString(R.string.injury_head_eye),
                getString(R.string.injury_paralyzed_flightless),
                getString(R.string.injury_open_wound),
                getString(R.string.injury_strings_feet),
                getString(R.string.injury_fledgling),
                getString(R.string.injury_other_short))

        val values = arrayOf(injuryData.sumFootOrLeg.toFloat(),
                injuryData.sumWing.toFloat(),
                injuryData.sumHeadOrEye.toFloat(),
                injuryData.sumParalyzedOrFlightless.toFloat(),
                injuryData.sumOpenWound.toFloat(),
                injuryData.sumStrappedFeet.toFloat(),
                injuryData.sumFledgling.toFloat(),
                injuryData.sumOther.toFloat())

        for (i in 0 until values.size) {
            if (values[i] != 0F) data.add(PieEntry(values[i], labels[i]))
        }


        return data
    }

    private fun getBreedData(breedData: BreedStat): ArrayList<PieEntry> {

        Log.d("BLUEDABE_BREEDDATA", breedData.toString())

        val data = ArrayList<PieEntry>()

        val labels = arrayOf(getString(R.string.carrier_pigeon),
                getString(R.string.common_wood_pigeon),
                getString(R.string.feral_pigeon),
                getString(R.string.fancy_pigeon),
                getString(R.string.no_specification))

        val values = arrayOf(breedData.carrierPigeon.toFloat(),
                breedData.commonWoodPigeon.toFloat(),
                breedData.feralPigeon.toFloat(),
                breedData.fancyPigeon.toFloat(),
                breedData.undefined.toFloat())

        for (i in 0 until values.size) {
            if (values[i] != 0F) data.add(PieEntry(values[i], labels[i]))
        }

        return data
    }

}