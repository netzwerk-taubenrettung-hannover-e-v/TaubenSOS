package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.unihannover.se.tauben2.R

class InjuriesReportFragment : ReportFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_injuries, container, false)

        setBtnListener(R.id.fragment_report_priority, R.id.fragment_report_location)
        return view
    }

}
