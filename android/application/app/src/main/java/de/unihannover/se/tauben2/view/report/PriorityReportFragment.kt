package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReportPriorityBinding
import de.unihannover.se.tauben2.model.database.entity.Case

class PriorityReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_priority

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReportPriorityBinding>(inflater, layoutId, container, false)
        
        binding.createdCase = mCreatedCase

        setBtnListener(R.id.fragment_report_breed, R.id.fragment_report_injuries)

        return binding.root
    }

}