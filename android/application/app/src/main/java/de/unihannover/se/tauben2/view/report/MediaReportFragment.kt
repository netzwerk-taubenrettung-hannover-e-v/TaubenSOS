package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.entity.Case
import kotlinx.android.synthetic.main.activity_report.*

class MediaReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_media

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(layoutId, container, false)

        pagePos = PagePos.FIRST
        mCreatedCase = Case.getCleanInstance()
        setBtnListener (R.id.fragment_report_location, null)

        (activity as ReportActivity).prev_btn.setOnClickListener {
            (activity as ReportActivity).finish()
        }

        return view
    }

}