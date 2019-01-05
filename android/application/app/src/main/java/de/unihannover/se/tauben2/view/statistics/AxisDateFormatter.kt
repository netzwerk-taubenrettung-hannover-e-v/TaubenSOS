package de.unihannover.se.tauben2.view.statistics

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AxisDateFormatter(private val startDate: Calendar, private val endDate: Calendar) : IAxisValueFormatter {

    private val mDates : Array<String>

    init {
        this.mDates = getDates()
    }

    private fun getDates () : Array<String> {

        val dates = Array(TimeUnit.MILLISECONDS.toDays(Math.abs(endDate.timeInMillis - startDate.timeInMillis)).toInt() + 1){""}
        val currentDate = startDate.clone() as Calendar

        for (i in 0 until dates.size) {
            dates[i] = SimpleDateFormat("dd.MM.yy", Locale.GERMANY).format(currentDate.timeInMillis)
            currentDate.add(Calendar.DATE, 1)
        }

        return dates
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return mDates[value.toInt()]
    }

}