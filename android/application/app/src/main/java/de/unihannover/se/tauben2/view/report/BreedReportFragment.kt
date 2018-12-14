package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReportBreedBinding

class BreedReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_breed

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReportBreedBinding>(inflater, layoutId, container, false)

        mCreatedCase = arguments?.getParcelable("createdCase")
        mCreatedCase?.let {
            binding.createdCase = it
        }
        setBtnListener(R.id.fragment_report_comment, R.id.fragment_report_priority)

        return binding.root
    }

}