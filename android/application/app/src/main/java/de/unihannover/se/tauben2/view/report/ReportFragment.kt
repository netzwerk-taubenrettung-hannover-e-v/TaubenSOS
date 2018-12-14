package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.entity.Case
import kotlinx.android.synthetic.main.activity_report.*

open class ReportFragment : Fragment() {

    enum class PagePos {
        FIRST, BETWEEN, LAST
    }

    var pagePos = PagePos.BETWEEN

    protected var mCreatedCase: Case? = null

    override fun onResume() {
        super.onResume()
        (activity as ReportActivity).onFragmentChange()
        setButtonStyle()
    }

    fun setBtnListener(forwardId: Int?, backId: Int?) {

        (activity as ReportActivity).next_btn.setOnClickListener {
            if (canGoForward()) {
                forwardId?.let { id ->
                    (activity as ReportActivity).stepForward()
                    val caseBundle = Bundle()
                    caseBundle.putParcelable("createdCase", mCreatedCase)
                    Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(id)
                }
            }
        }
        (activity as ReportActivity).prev_btn.setOnClickListener {
            backId?.let { id ->
                (activity as ReportActivity).stepBack()
                Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(id)
            }
        }
    }

    protected open fun canGoForward () : Boolean {
        return true
    }

    // dis is shit - TODO do it nice-etly
    private fun setButtonStyle () {

        when (pagePos) {
            PagePos.FIRST -> {
                (activity as ReportActivity).prev_btn.text = "Cancel"
                (activity as ReportActivity).prev_btn.icon = null
            }
            PagePos.BETWEEN -> {
                (activity as ReportActivity).prev_btn.text = "Back"
                (activity as ReportActivity).prev_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_keyboard_arrow_left_white_24dp)
                (activity as ReportActivity).next_btn.text = "Next"
                (activity as ReportActivity).next_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_keyboard_arrow_right_white_24dp)
            }
            PagePos . LAST -> {
                (activity as ReportActivity).next_btn.text = "Finish"
                (activity as ReportActivity).next_btn.icon = null
            }
        }
    }

}