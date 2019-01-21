package de.unihannover.se.tauben2.view.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.list.StatisticItem
import de.unihannover.se.tauben2.view.list.StatisticItemAdapter
import kotlinx.android.synthetic.main.fragment_statistic_menu.view.*

class StatisticMenuFragment : Fragment() {

    private val statisticItems = arrayListOf(
            StatisticItem("test0", "test0", R.drawable.ic_logo, R.id.statisticFragment),
            StatisticItem("test1", "test1", R.drawable.ic_logo, R.id.statisticFragment))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistic_menu, container, false)

        val statisticListAdapter = StatisticItemAdapter(view.context, statisticItems)
        view.statistics_listview.adapter = statisticListAdapter

        return view
    }

}