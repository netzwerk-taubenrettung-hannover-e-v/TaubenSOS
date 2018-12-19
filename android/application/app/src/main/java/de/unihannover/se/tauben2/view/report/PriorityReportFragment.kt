package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReportPriorityBinding
import kotlinx.android.synthetic.main.fragment_report_priority.view.*

class PriorityReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_priority

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReportPriorityBinding>(inflater, layoutId, container, false)

        mCreatedCase = arguments?.getParcelable("createdCase")
        mCreatedCase?.let {
            binding.createdCase = it
        }
        setBtnListener(R.id.fragment_report_breed, R.id.fragment_report_injuries)

        return binding.root
    }

}