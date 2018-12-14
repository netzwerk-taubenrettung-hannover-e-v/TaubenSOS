package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.unihannover.se.tauben2.R

class PriorityReportFragment : ReportFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_priority, container, false)

        setBtnListener(R.id.fragment_report_breed, R.id.fragment_report_injuries)
        return view
    }

}