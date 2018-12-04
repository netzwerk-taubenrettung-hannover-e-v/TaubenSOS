package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.fragment_report01.*
import kotlinx.android.synthetic.main.fragment_report02.*
import kotlinx.android.synthetic.main.fragment_report02.view.*


class Report02Fragment : Fragment() {

    companion object: Singleton<Report02Fragment>() {
        override fun newInstance() = Report02Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report02, container, false)
        val model = activity?.run { ViewModelProviders.of(this).get(ReportViewModel::class.java) }

        // this will reset the frame - no gud. change plox
        view.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }

        view.report_send_button.setOnClickListener {

            // at the moment, not necessary
            model?.setCarrierPigeon(report_special_checkbox_00.isChecked)
            model?.setWeddingPigeon(report_special_checkbox_01.isChecked)
            model?.setAdditionalInformations(report_additional_information_textfield_value.text.toString())

            Log.d("REPORT_A_DOVE POSITION", model?.getPosition().toString())
            Log.d("REPORT_A_DOVE MEDIA", "")
            for (i in 0 until 7)
                Log.d("REPORT_A_DOVE INJURY", model?.getInjuryAt(i).toString())
            Log.d("REPORT_A_DOVE PRIORITY", model?.getPriority().toString())
            Log.d("REPORT_A_DOVE CARRIER", model?.isCarrierPigeon().toString())
            Log.d("REPORT_A_DOVE WEDDING", model?.isWeddingPigeon().toString())
            Log.d("REPORT_A_DOVE ADDITIONS", model?.getAdditionalInformations())

            Report00Fragment.removeInstance()
            Report01Fragment.removeInstance()
            Report02Fragment.removeInstance()
        }

        return view
    }

}