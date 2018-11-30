package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.Singleton
import kotlinx.android.synthetic.main.fragment_report00.*
import kotlinx.android.synthetic.main.fragment_report00.view.*

class Report00Fragment : Fragment(), View.OnClickListener {

    companion object: Singleton<Report00Fragment>() {
        override fun newInstance() = Report00Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report00, container, false)

        view.report_next_step_button.setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {
        when (view) {
            report_next_step_button -> {
                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report01Fragment)
            }
        }
    }

}