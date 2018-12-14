package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.activity_report.*

open class ReportFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        (activity as ReportActivity).onFragmentChange()
    }

    fun setBtnListener(forwardId: Int?, backId: Int?) {

        (activity as ReportActivity).next_btn.setOnClickListener {
            forwardId?.let { id ->
                (activity as ReportActivity).stepForward()
                Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(id)
            }
        }
        (activity as ReportActivity).prev_btn.setOnClickListener {
            backId?.let { id ->
                (activity as ReportActivity).stepBack()
                Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(id)
            }
        }
    }
}