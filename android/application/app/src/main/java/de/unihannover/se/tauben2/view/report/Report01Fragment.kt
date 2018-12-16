package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReport01Binding
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.Singleton
import kotlinx.android.synthetic.main.fragment_report01.*
import kotlinx.android.synthetic.main.fragment_report01.view.*

class Report01Fragment : Fragment() {

    private var mCreatedCase: Case? = null

    override fun onResume() {
        super.onResume()
    }

    companion object : Singleton<Report01Fragment>() {
        override fun newInstance() = Report01Fragment()

        private val LOG_TAG = Report01Fragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReport01Binding>(inflater, R.layout.fragment_report01, container, false)

        mCreatedCase = arguments?.getParcelable(Report00Fragment.CREATED_CASE_KEY)
        mCreatedCase?.let {
            binding.createdCase = it
        }

        binding.root.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.report_nav_host).navigate(R.id.report00Fragment)
        }

        binding.root.report_next_step_button.setOnClickListener {
            if (canGoForward()) {
                arguments?.putParcelable(Report00Fragment.CREATED_CASE_KEY, mCreatedCase)

                Log.d(LOG_TAG, "Passed ${arguments?.getParcelable<Case>(Report00Fragment.CREATED_CASE_KEY)} to next Fragment")
                Log.d(LOG_TAG, "Passed ${arguments?.getStringArrayList(Report00Fragment.MEDIA_PATHS_KEY)} to next Fragment")

                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report02Fragment, arguments)
            } else {
                context?.let { c ->
                    report_injury_title.setTextColor(ContextCompat.getColor(c, R.color.errorColor))
                    report_injury_title.setError("")
                }
            }
        }

        binding.root.infoButtonReport.setOnClickListener {
            //Pop up for more info
            val alertDialogBuilder = AlertDialog.Builder(
                    context)

            alertDialogBuilder.setTitle("Zustand der Taube")

            alertDialogBuilder
                    .setMessage(R.string.taube_melden_info)

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }

        return binding.root
    }

    private fun canGoForward(): Boolean {

        for (i in 0 until report_injury_layout.childCount) {
            val child = report_injury_layout.getChildAt(i)
            if (child is CheckBox) {
                if (child.isChecked) {
                    return true
                }
            }
        }
        return false
    }
}