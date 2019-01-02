package de.unihannover.se.tauben2.view.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_statistic.*
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_statistic.view.*


class StatisticFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)

        view.collapse_button.setOnClickListener {
            val appbar = appbar as AppBarLayout
            appbar.setExpanded(!(appbar.height - appbar.bottom == 0))
        }

        return view
    }

}