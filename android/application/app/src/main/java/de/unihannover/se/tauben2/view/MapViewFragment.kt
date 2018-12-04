package de.unihannover.se.tauben2.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.network.Resource

class MapViewFragment : SupportMapFragment(), Observer<List<MapMarkable>> {

    private var mMap: GoogleMap? = null
    private val mMarkers: MutableMap<MapMarkable, Marker?> = mutableMapOf()
    private var selectedPosition : Marker? = null

    override fun onChanged(data: List<MapMarkable>) {
        if(mMarkers.isEmpty()) {
            data.forEach { mMarkers[it] = null }
            setCaseMarkers(data)
            return
        }
        val casesToRemove: MutableMap<MapMarkable, Marker?> = mutableMapOf()

        loop@ for((oldCase, oldMarker) in mMarkers) {
            for(newCase in data) {
                if(newCase == oldCase)
                    continue@loop
            }
            casesToRemove[oldCase] = oldMarker
        }

        for((oldCase, oldMarker) in casesToRemove) {
            oldMarker?.remove()
            mMarkers.remove(oldCase)
        }

        setCaseMarkers(data)
    }

    // fix that!: googleMap.isMyLocationEnabled = true
    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val view = super.onCreateView(inflater, container, savedInstanceState)


//        view.mapView.onCreate(savedInstanceState)
//
//        view.mapView.onResume() // needed to get the map to display immediately
//
//        try {
//            MapsInitializer.initialize(activity!!.applicationContext)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

//        view.mapView.
        if (mMap == null) {
            getMapAsync { map ->
                mMap = map

                // For showing a move to my location button
                map.isMyLocationEnabled = true

                // TODO Find best bound coordinates
                val bounds = LatLngBounds(LatLng(52.3050934, 9.4635117), LatLng(52.5386801, 9.9908932))
                map.setLatLngBoundsForCameraTarget(bounds)
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))

                setCaseMarkers(mMarkers.keys)
            }
        }

        return view
    }

    // add a marker at the middle of the map and save it in 'selectedPosition'
    fun selectPosition () {

        // remove the old position if exists
        selectedPosition?.remove()

        // add marker
        val mo = MarkerOptions()
        mo.position(mMap!!.cameraPosition.target)
        mo.title("your selected position")
        selectedPosition = mMap?.addMarker(mo)
    }

    fun getSelectedPosition () : LatLng? {
        return selectedPosition?.position
    }

    private fun setCaseMarkers(markers: Collection<MapMarkable>) {
        mMap?.let { map ->
            markers.forEach { marker ->
                if(mMarkers[marker] == null)
                    mMarkers[marker] = map.addMarker(marker.getMarker())
            }
        }
    }

}