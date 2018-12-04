package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.MapViewFragment
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.ReportViewModel
import kotlinx.android.synthetic.main.fragment_report00.*
import kotlinx.android.synthetic.main.fragment_report00.view.*
import kotlinx.android.synthetic.main.fragment_report01.*

class Report00Fragment : Fragment() {

    companion object: Singleton<Report00Fragment>() {
        override fun newInstance() = Report00Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report00, container, false)
        val model = activity?.run { ViewModelProviders.of(this).get(ReportViewModel::class.java) }
        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment



        // OnClickListeners
        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition()
            model?.setPosition(mapsFragment.getSelectedPosition()!!)
        }
        view.report_next_step_button.setOnClickListener {

            if (mapsFragment.getSelectedPosition() == null) {
                context?.let { c ->
                    report_map_title.setTextColor(ContextCompat.getColor(c, R.color.errorColor))
                }
            } else {
                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report01Fragment)
            }
        }

        return view
    }

}