package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.Singleton
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

        // this will reset the frame - no gud. change plox
        view.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }

        view.report_send_button.setOnClickListener {
            // send informations
            if(report_injury_checkBox_06.isChecked && report_additional_information_textfield.toString().length == 0) {
                report_additional_information_title.setTextColor(Color.RED)
                report_additional_information_textfield.setBackgroundResource(R.drawable.border_layout)
            } else {
                Report00Fragment.removeInstance()
                Report01Fragment.removeInstance()
                Report02Fragment.removeInstance()
            }
        }

        return view
    }

}