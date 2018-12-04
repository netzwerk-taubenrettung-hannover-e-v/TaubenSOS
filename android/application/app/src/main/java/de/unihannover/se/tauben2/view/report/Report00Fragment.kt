package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.maps.model.LatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.Injury
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.MapViewFragment
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_report00.*
import kotlinx.android.synthetic.main.fragment_report00.view.*

class Report00Fragment : Fragment(), Observer<Location?> {

    private var mLocation: LatLng? = null
    private var mCreatedCase: Case = Case(null, null, null, false,
            false, 0.0, 0.0, null, 1, 0,
            "", null, Injury(false, false, false,
            false, false, false, false), listOf())

    companion object : Singleton<Report00Fragment>() {
        override fun newInstance() = Report00Fragment()
    }

    override fun onResume() {
        super.onResume()
        val locationViewModel = getViewModel(LocationViewModel::class.java)
        locationViewModel?.observeCurrentLocation(this, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report00, container, false)

        val mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition()
            mLocation = mapsFragment.getSelectedPosition()
        }

        view.report_next_step_button.setOnClickListener {

            if (!saveLocation()) {
                context?.let { c ->
                    report_map_title.setTextColor(ContextCompat.getColor(c, R.color.errorColor))
                }
            } else {
                val bundle = Bundle()
                bundle.putParcelable("createdCase", mCreatedCase)
                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report01Fragment, bundle)
            }
        }

        return view
    }

    override fun onChanged(loc: Location?) {
        loc ?: return
        mLocation = LatLng(loc.latitude, loc.longitude)
    }

    /**
     * saves location to created case
     * @return true if successful
     */
    private fun saveLocation(): Boolean {
        mLocation?.let {
            mCreatedCase.longitude = it.longitude
            mCreatedCase.latitude = it.latitude
            return true
        }
        return false
    }
}