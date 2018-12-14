package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReportCommentBinding
import kotlinx.android.synthetic.main.activity_report.*

class CommentReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_comment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReportCommentBinding>(inflater, layoutId, container, false)

        pagePos = PagePos.LAST
        mCreatedCase = arguments?.getParcelable("createdCase")
        mCreatedCase?.let {
            binding.createdCase = it
        }
        setBtnListener(null, R.id.fragment_report_breed)

        (activity as ReportActivity).next_btn.setOnClickListener {
            // send data to server
            
        }

        return binding.root
    }

}