package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReportInjuriesBinding
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.setSnackBar
import kotlinx.android.synthetic.main.fragment_report_injuries.*

class InjuriesReportFragment : ReportFragment() {

    private val layoutId = R.layout.fragment_report_injuries

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReportInjuriesBinding>(inflater, layoutId, container, false)

        arguments?.getParcelable<Case>("createdCase")?.let {
            mCreatedCase = it
            binding.createdCase = it
        }
        setBtnListener(R.id.fragment_report_priority, R.id.fragment_report_location)

        return binding.root
    }

    override fun canGoForward(): Boolean {

        for (i in 0 until report_injury_layout.childCount) {
            val child = report_injury_layout.getChildAt(i)
            if (child is CheckBox) {
                if (child.isChecked) {
                    return true
                }
            }
        }
        setSnackBar(view!!, "please select at least one injury")
        return false
    }

}
