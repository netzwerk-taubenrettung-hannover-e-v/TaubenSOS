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
import de.unihannover.se.tauben2.view.MapViewFragment
import de.unihannover.se.tauben2.view.Singleton
import kotlinx.android.synthetic.main.fragment_report00.*
import kotlinx.android.synthetic.main.fragment_report00.view.*

class Report00Fragment : Fragment() {

    companion object: Singleton<Report00Fragment>() {
        override fun newInstance() = Report00Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report00, container, false)

        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        val case = Case(null)

        // OnClickListeners
        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition()

            val pos = mapsFragment.getSelectedPosition()

            pos?.let { latlng ->
                case.latitude = latlng.latitude
                case.longitude = latlng.longitude
            }
        }
        view.report_next_step_button.setOnClickListener {

            if (mapsFragment.getSelectedPosition() == null) {
                context?.let { c ->
                    report_map_title.setTextColor(ContextCompat.getColor(c, R.color.errorColor))
                }
            } else {
                val bundle = Bundle()
                bundle.putParcelable("case", case)
                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report01Fragment, bundle)
            }
        }

        return view
    }

}