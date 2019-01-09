package de.unihannover.se.tauben2.view.main.fragments


//import de.unihannover.se.tauben2.model.database.entity.PigeonCounter
//import de.unihannover.se.tauben2.viewmodel.PigeonCounterViewModel
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.util.TimeUtils
import android.view.*
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.model.CounterValue
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.view.input.InputFilterMinMax
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.statistics.AxisDateFormatter
import de.unihannover.se.tauben2.viewmodel.PopulationMarkerViewModel
import kotlinx.android.synthetic.main.fragment_counter_info.*
import kotlinx.android.synthetic.main.fragment_counter_info.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 */

class CounterInfoFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var selectedDate: Calendar = Calendar.getInstance()
    private var mPopulationMarker : PopulationMarker? = null

    private var mToolbarMenu: Menu? = null

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

        view.counter_value.filters = arrayOf<InputFilter>(InputFilterMinMax(0, 9999))

        arguments?.getInt("marker")?.let { markerID ->
            getViewModel(PopulationMarkerViewModel::class.java)?.populationMarkers?.filter { it.populationMarkerID == markerID  }?.observe(this, androidx.lifecycle.Observer {
                if(it?.status?.isSuccessful() == true) {
                    if(it.data?.size == 1) {
                        mPopulationMarker = it.data[0]
                    }

                    val dataList : MutableList<Entry> = mutableListOf()

                    val first = mPopulationMarker?.values?.sortedBy { value -> value.timestamp }?.firstOrNull()?.let {value ->
                        Calendar.getInstance().apply {
                            timeInMillis = value.timestamp*1000
                        }
                    }

                    val last = mPopulationMarker?.values?.sortedBy { value -> value.timestamp }?.lastOrNull()?.let { value ->
                        Calendar.getInstance().apply {
                            timeInMillis = value.timestamp*1000
                        }
                    }

                    mPopulationMarker?.values?.sortedBy { value -> value.timestamp }?.forEach { value ->
                        first?.let {
                            val index = TimeUnit.MILLISECONDS.toDays(value.timestamp * 1000 - first.timeInMillis)
                            dataList.add(Entry(index.toFloat(), value.pigeonCount.toFloat()))
                        }
                    }

                    val dataSet = LineDataSet(dataList, "")
                    dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    val lineData = LineData(dataSet)
                    view.chart.apply {
                        xAxis.position = XAxis.XAxisPosition.BOTTOM
                        xAxis.labelRotationAngle = -60f

                        multiLet(first, last) {f, l ->
                            xAxis.valueFormatter = AxisDateFormatter(f, l)
                        }
                        xAxis.setDrawGridLines(false)

                        description.isEnabled = false
                        legend.isEnabled = false

                        data = lineData
                        invalidate()
                    }
                }
            })
        }

        //OnClickListeners
        view.plus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?: 0) + 1
            counter_value.setText(value.toString())
        }

        view.minus_button.setOnClickListener {
            val value = (counter_value.text.toString().toIntOrNull() ?: 0) - 1
            counter_value.setText(value.toString())
        }

        view.current_timestamp_value.setOnClickListener {
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
            mPopulationMarker?.let {marker ->
                vm?.postCounterValue(CounterValue(counter_value.text.toString().toInt(), marker.populationMarkerID, selectedDate.timeInMillis / 1000))
            }
            counter_value.setText("0")
        }

        return view
    }

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
                                mPopulationMarker?.let {marker ->
                                    vm?.deleteMarker(marker)
                                    //dataList.add(Entry(dataList.size.toFloat(), counter_value.text.toString().toFloat()))
                                    //refreshChartData(view.chart)
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
        view?.current_timestamp_value?.text = getDateString(selectedDate.timeInMillis)
    }

    private fun setCurrentTimestamp() {
        selectedDate = Calendar.getInstance().apply {
            set(Calendar.HOUR, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        refreshTextView()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        selectedDate.set(year, month, day)
        refreshTextView()
    }

}
