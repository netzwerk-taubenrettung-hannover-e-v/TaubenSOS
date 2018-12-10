package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReport02Binding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.fragment_report02.view.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_report02.*
import androidx.core.content.ContextCompat
import android.widget.TextView
import android.accounts.AccountManager.KEY_ERROR_MESSAGE
import kotlinx.android.synthetic.main.fragment_news.view.*
import android.view.Gravity
import android.os.Build


class Report02Fragment : Fragment() {

    private val LOG_TAG = this::class.java.simpleName

    private var mCreatedCase: Case? = null

    companion object : Singleton<Report02Fragment>() {
        override fun newInstance() = Report02Fragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReport02Binding>(inflater, R.layout.fragment_report02, container, false)

        mCreatedCase = arguments?.getParcelable("createdCase")
        mCreatedCase?.let {
            binding.createdCase = it
        }

        binding.root.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }

        binding.root.report_send_button.setOnClickListener {
            mCreatedCase?.setToCurrentTime()
            sendCaseToServer()
//            Report00Fragment.removeInstance()
//            Report01Fragment.removeInstance()
//            Report02Fragment.removeInstance()
        }

        return binding.root
    }

    private fun sendCaseToServer() {
        val caseViewModel = getViewModel(CaseViewModel::class.java)
        mCreatedCase?.let { case ->
            caseViewModel?.let {
                it.sendCase(case)
                Log.d(LOG_TAG, "Sent case: $case")
                //Toast.makeText(context, "Case Sent!", Toast.LENGTH_SHORT).show()
                var snack = Snackbar.make(myCoordinatorLayoutCase, "Case Sent", Snackbar.LENGTH_SHORT)
                val mainTextView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    mainTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                else
                    mainTextView.gravity = Gravity.CENTER_HORIZONTAL
                mainTextView.gravity = Gravity.CENTER_HORIZONTAL
                snack.show()
            }
        }

    }

}