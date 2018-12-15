package de.unihannover.se.tauben2.view.report

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.MapViewFragment
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_report00.view.*

class LocationReportFragment : ReportFragment(), Observer<Location?> {

    private var mLocation: LatLng? = null
    private lateinit var mapsFragment : MapViewFragment

    private val layoutId = R.layout.fragment_report_location

    override fun onResume() {
        super.onResume()
        getViewModel(LocationViewModel::class.java)?.observeCurrentLocation(this, this)
    }

    override fun onPause() {
        super.onPause()
        getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(layoutId, container, false)
        mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        mCreatedCase = arguments?.getParcelable("createdCase")
        setBtnListener(R.id.fragment_report_injuries, R.id.fragment_report_media)

        view.set_position_button.setOnClickListener {
            mapsFragment.selectPosition(null)
            mLocation = mapsFragment.getSelectedPosition()
        }

        return view
    }

    override fun onChanged(loc: Location?) {
        loc ?: return
        // If location not set, set to current location
        if (mLocation == null)
            mLocation = LatLng(loc.latitude, loc.longitude)
    }

    /**
     * saves location to created case
     * @return true if successful
     */
    private fun saveLocation(): Boolean {
        mLocation?.let {
            mCreatedCase?.longitude = it.longitude
            mCreatedCase?.latitude = it.latitude
            return true
        }
        return false
    }

   override fun canGoForward (): Boolean {
        if (saveLocation()) return true
        else setSnackBar(view!!, "please select a location")
        return false
   }

    fun setMarker() {

        if (mCreatedCase!!.latitude != 0.0 && mCreatedCase!!.longitude != 0.0) {
            mapsFragment.selectPosition(LatLng(mCreatedCase!!.latitude, mCreatedCase!!.longitude))
            mLocation = mapsFragment.getSelectedPosition()
        }
    }

}