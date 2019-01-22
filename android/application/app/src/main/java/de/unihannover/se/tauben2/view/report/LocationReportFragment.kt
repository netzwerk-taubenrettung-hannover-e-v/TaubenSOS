package de.unihannover.se.tauben2.view.report

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.view.main.fragments.MapViewFragment
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_report_location.*
import kotlinx.android.synthetic.main.fragment_report_location.view.*

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
            mCreatedCase.longitude = it.longitude
            mCreatedCase.latitude = it.latitude
            return true
        }
        return false
    }

   override fun canGoForward (): Boolean {
        if (saveLocation()) return true
        else setSnackBar(getString(R.string.select_location))
        return false
   }

    fun setMarker() {

        // works but is bad
        val crosshair = ImageView(context)

        crosshair.setImageDrawable(resources.getDrawable(R.drawable.ic_location_crosshair, null))
        crosshair.setColorFilter(Color.BLACK)

        crosshair.id = View.generateViewId()
        // set last alignments here
        //layout.
        crosshair.layoutParams = ViewGroup.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        crosshair.setPadding(0,0,0,0)
        map_ui.addView(crosshair)

        val set = ConstraintSet()
        set.clone(map_ui)
        set.connect(crosshair.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        set.connect(crosshair.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0)
        set.connect(crosshair.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        set.connect(crosshair.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)


        set.applyTo(map_ui)

        if (mCreatedCase.latitude != 0.0 && mCreatedCase.longitude != 0.0) {
            mapsFragment.selectPosition(LatLng(mCreatedCase.latitude, mCreatedCase.longitude))
            mLocation = mapsFragment.getSelectedPosition()
        }
    }

}