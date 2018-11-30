package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.Injury
import de.unihannover.se.tauben2.model.SendableCase
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.fragment_report02.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Report02Fragment : Fragment() {

    private val LOG_TAG = this::class.java.simpleName

    companion object : Singleton<Report02Fragment>() {
        override fun newInstance() = Report02Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report02, container, false)

        // this will reset the frame - no gud. change plox
        view.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }

        view.report_send_button.setOnClickListener {
            // send information
            sendCaseToServer()
            Report00Fragment.removeInstance()
            Report01Fragment.removeInstance()
            Report02Fragment.removeInstance()
        }

        return view
    }

    private fun sendCaseToServer() {
        val service = App.getNetworkService()
        // TODO replace with actual data
        val myNewCase = Case(null, "Test 4321", null,
                false, false, 52.379, 9.74170,
                null, 1, 1543527726, "110", null,
                Injury(true, false, true, false, true,
                        true, true))
        val caseViewModel = getViewModel(CaseViewModel::class.java)
        caseViewModel?.sendCase(myNewCase)
    }
}