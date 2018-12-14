package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.unihannover.se.tauben2.R

class MediaReportFragment : ReportFragment() {

    override fun onResume() {
        super.onResume()
        (activity as ReportActivity).firstPageButton()
    }

    override fun onPause() {
        super.onPause()
        (activity as ReportActivity).normalPageButton()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report_media, container, false)

        setBtnListener (R.id.fragment_report_location, null)
        return view
    }

}