package de.unihannover.se.tauben2.view.main.fragments


import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.multiLet
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.report.ReportActivity
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.PopulationMarkerViewModel
import de.unihannover.se.tauben2.view.statistics.AxisDateFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 */

class CounterInfoFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private var selectedDate: Calendar = Calendar.getInstance()
    private var mPopulationMarker : PopulationMarker? = null

    private var mToolbarMenu: Menu? = null

    private var dataList : MutableList<Entry> = mutableListOf<Entry>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_counter_info, container, false)
        setHasOptionsMenu(true)

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

        arguments?.let {args ->
            mPopulationMarker = args.getParcelable<PopulationMarker>("marker")
        }

        //var dataList = mutableListOf<Entry>()

        mPopulationMarker?.let {marker ->
            var i = 0
            for(value in marker.values){
                dataList.add(Entry(i.toFloat(), value.pigeonCount.toFloat()))
                i++
            }
        }

        var dataSet = LineDataSet(dataList, "")
        var lineData = LineData(dataSet)
        view.chart.data = lineData
        view.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //val calender : Calendar = Calendar(mPopulationMarker.values.first().timestamp))
        /*mPopulationMarker?.let {marker -> val start : Calendar = Calendar.getInstance()
            start.timeInMillis = marker.values.first().timestamp
            val end : Calendar = Calendar.getInstance()
            start.timeInMillis = marker.values.last().timestamp
            view.chart.xAxis.valueFormatter = AxisDateFormatter(start, end)
        }*/

        //view.chart.xAxis.valueFormatter = AxisDateFormatter(selectedDateFrom, selectedDateTo)
        view.chart.xAxis.labelRotationAngle = -60f

        view.chart.description.isEnabled = false
        view.chart.legend.isEnabled = false

        view.chart.invalidate()

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

            val vm = getViewModel(PopulationMarkerViewModel::class.java)
            vm?.let {vm ->
                mPopulationMarker?.let {marker ->
                    vm.postCounterValue(CounterValue(counter_value.text.toString().toInt(), marker.populationMarkerID, selectedDate.timeInMillis / 1000))
                    //dataList.add(Entry(dataList.size.toFloat(), counter_value.text.toString().toFloat()))
                    //refreshChartData(view.chart)
                }
            }
        }

        return view
    }

    /*private fun refreshChartData(chart : LineChart){
        var dataSet = LineDataSet(dataList, "")
        var lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }*/

    override fun onStart() {
        super.onStart()
        setCurrentTimestamp()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        mToolbarMenu = menu
        mToolbarMenu?.let { setOptionsMenuItems(it) }
    }

    private fun setOptionsMenuItems(menu: Menu) {
        val permission = BootingActivity.getOwnerPermission()
        menu.apply{
            if(permission == Permission.ADMIN || permission == Permission.AUTHORISED) {
                findItem(R.id.toolbar_delete)?.isVisible = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val result = super.onOptionsItemSelected(item)

        when(item?.itemId){
            R.id.toolbar_delete -> {
                context?.let { cxt ->
                    androidx.appcompat.app.AlertDialog.Builder(cxt)
                            .setTitle("Do you want to delete this marker?")
                            .setMessage("There is no point of return.")
                            .setPositiveButton(R.string.delete) { _, _ ->
                                //TODO Delete Marker
                                val vm = getViewModel(PopulationMarkerViewModel::class.java)
                                vm?.let {vm ->
                                    mPopulationMarker?.let {marker ->
                                        vm.deleteMarker(marker)
                                        //dataList.add(Entry(dataList.size.toFloat(), counter_value.text.toString().toFloat()))
                                        //refreshChartData(view.chart)
                                    }
                                }
                                val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
                                controller.navigatorProvider.getNavigator(BottomNavigator::class.java).popFromBackStack()
                                controller.navigate(R.id.counterFragment)
                            }.setNegativeButton(R.string.cancel) { di, _ ->
                                di.cancel()
                            }.show()
                }
            }
        }
        return result
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
