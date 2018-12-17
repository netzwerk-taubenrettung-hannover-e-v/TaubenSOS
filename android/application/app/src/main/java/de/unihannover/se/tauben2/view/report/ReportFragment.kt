package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.common.util.IOUtils
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.activity_report.*

open class ReportFragment : Fragment() {

    companion object {
        private val LOG_TAG = ReportFragment::class.java.simpleName
    }

    // check bottom of the class
    enum class PagePos {
        FIRST, BETWEEN, LAST
    }

    var pagePos = PagePos.BETWEEN

    protected var mCreatedCase: Case? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCreatedCase = (activity as ReportActivity).case
    }

    override fun onResume() {
        super.onResume()
        (activity as ReportActivity).onFragmentChange()
        setButtonStyle()
    }

    protected fun sendCaseToServer() {

        val caseViewModel = getViewModel(CaseViewModel::class.java)

        mCreatedCase?.let { case ->
            caseViewModel?.let {
                // New Case
                if (case.caseID == null) {
                    case.setToCurrentTime()
                    Log.d("SENT CASE", "case sent: $case")
                    val mediaFiles = readAsRaw(case.media)
                    it.sendCase(case, mediaFiles)
                }
                // Edit Case
                else {
                    val mediaFiles = readAsRaw(case.media)

                    it.updateCase(case, mediaFiles)
                    // existing images inside the case are already saved as URLs!
                    Log.d("EDIT CASE", "case edited: $case")
                }
            }
        }
    }

    private fun readAsRaw(fileNames: List<String>): List<ByteArray> {
        val files: MutableList<ByteArray> = mutableListOf()
        for (name in fileNames) {
            val fileStream = context?.openFileInput(name)
            val file = IOUtils.readInputStreamFully(fileStream)
            files.add(file)
        }
        return files
    }


    private fun deleteCase() {
        val caseViewModel = getViewModel(CaseViewModel::class.java)

        mCreatedCase?.let { case ->
            caseViewModel?.let {
                it.deleteCase(case)
                Log.d(LOG_TAG, "Case deleted!")
            }
        }
    }

    fun setBtnListener(forwardId: Int?, backId: Int?) {

        (activity as ReportActivity).next_btn.setOnClickListener {
            if (canGoForward()) {
                forwardId?.let { id ->
                    (activity as ReportActivity).stepForward()
                    val caseBundle = Bundle()
                    caseBundle.putParcelable("createdCase", mCreatedCase)
                    Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(id, caseBundle)
                }
            }
        }
        (activity as ReportActivity).prev_btn.setOnClickListener {
            backId?.let { id ->
                (activity as ReportActivity).stepBack()
                val caseBundle = Bundle()
                caseBundle.putParcelable("createdCase", mCreatedCase)
                Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(id, caseBundle)
            }
        }
    }

    protected open fun canGoForward(): Boolean {
        return true
    }

    // dis is shit - TODO do it nice-etly
    private fun setButtonStyle() {

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
            PagePos.LAST -> {
                (activity as ReportActivity).next_btn.text = "Finish"
                (activity as ReportActivity).next_btn.icon = null
            }
        }
    }


}