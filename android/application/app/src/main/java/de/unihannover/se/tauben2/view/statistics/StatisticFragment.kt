package de.unihannover.se.tauben2.view.statistics

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


class StatisticFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)

        view.collapse_button.setOnClickListener {

            val appbar = appbar as AppBarLayout
            val expand = appbar.height - appbar.bottom != 0

            appbar.setExpanded(expand)

            if(expand)
                view.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_up)
            else
                view.collapse_button.setImageResource(R.drawable.ic_keyboard_arrow_down)
        }

        createChart(view)

        return view
    }

    private fun createChart (view : View) {

        val chart = view.chart as LineChart

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