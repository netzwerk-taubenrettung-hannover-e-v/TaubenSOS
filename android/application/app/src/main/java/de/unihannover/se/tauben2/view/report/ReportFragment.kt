package de.unihannover.se.tauben2.view.report

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.util.Patterns
import android.webkit.URLUtil
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.common.util.IOUtils
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.phone_alert_dialog.view.*

open class ReportFragment : Fragment() {

    companion object {
        private val LOG_TAG = ReportFragment::class.java.simpleName
    }

    // check bottom of the class
    enum class PagePos {
        FIRST, BETWEEN, LAST
    }

    var pagePos = PagePos.BETWEEN

    protected lateinit var mCreatedCase: Case

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCreatedCase = (activity as ReportActivity).case ?: Case.getCleanInstance()

    }

    override fun onResume() {
        super.onResume()
        (activity as ReportActivity).onFragmentChange()
        setButtonStyle()
    }

    protected fun sendCaseToServer() {

        getViewModel(CaseViewModel::class.java)?.let {
            // New Case
            if (mCreatedCase.caseID == null) {
                mCreatedCase.setToCurrentTime()
                Log.d("SENT CASE", "case sent: $mCreatedCase")
                val mediaFiles = readAsRaw(mCreatedCase.media)
                it.sendCase(mCreatedCase, mediaFiles)
            }
            // Edit Case
            else {
                mCreatedCase.media = mCreatedCase.media.filter { url -> !URLUtil.isValidUrl(url) }
                Log.d(LOG_TAG, mCreatedCase.media.toString())

                val mediaFiles = readAsRaw(mCreatedCase.media)
                it.updateCase(mCreatedCase, mediaFiles)
                // existing images inside the case are already saved as URLs!
                Log.d("EDIT CASE", "case edited: $mCreatedCase")
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

    private fun setButtonStyle() {

        when (pagePos) {
            PagePos.FIRST -> {
                (activity as ReportActivity).prev_btn.text = getString(R.string.cancel)
                (activity as ReportActivity).prev_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_close)
                (activity as ReportActivity).next_btn.text = getString(R.string.next)
                (activity as ReportActivity).next_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_keyboard_arrow_right_white_24dp)
            }
            PagePos.BETWEEN -> {
                (activity as ReportActivity).prev_btn.text = getString(R.string.back)
                (activity as ReportActivity).prev_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_keyboard_arrow_left_white_24dp)
                (activity as ReportActivity).next_btn.text = getString(R.string.next)
                (activity as ReportActivity).next_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_keyboard_arrow_right_white_24dp)
            }
            PagePos.LAST -> {
                (activity as ReportActivity).next_btn.text = getString(R.string.send)
                (activity as ReportActivity).next_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_done)
                (activity as ReportActivity).prev_btn.text = getString(R.string.back)
                (activity as ReportActivity).prev_btn.icon = ContextCompat.getDrawable(activity as ReportActivity, R.drawable.ic_keyboard_arrow_left_white_24dp)
            }
        }
    }

    protected fun setSnackBar(snackTitle: String) {
        view?.let {
            setSnackBar(it, snackTitle,(activity as ReportActivity).report_bottom_bar)
        }
    }


}