package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.fragment_report01.*
import kotlinx.android.synthetic.main.fragment_report01.view.*

class Report01Fragment : Fragment() {

    companion object : Singleton<Report01Fragment>() {
        override fun newInstance() = Report01Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report01, container, false)
        val model = activity?.run { ViewModelProviders.of(this).get(ReportViewModel::class.java) }

        view.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }

        view.report_next_step_button.setOnClickListener {

            if (canGoForward(model)) {
                model?.setPriority(report_state_seekbar.progress)
                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report02Fragment)
            } else {
                context?.let { c ->
                    report_injury_title.setTextColor(ContextCompat.getColor(c, R.color.errorColor))
                }
            }
        }

        return view
    }

    private fun canGoForward(model : ReportViewModel?): Boolean {

        var injuries = BooleanArray(report_injury_layout.childCount)
        var check = false

        for (i in 0 until report_injury_layout.childCount) {
            val child = report_injury_layout.getChildAt(i)
            if (child is CheckBox) {
                if (child.isChecked) {
                    injuries[i] = true
                    check = true
                }
            }
        }

        model?.setInjuries(injuries)
        return check
    }
}