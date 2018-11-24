package de.unihannover.se.tauben2.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.network.Resource
import kotlinx.android.synthetic.main.fragment_map.view.*

class MapViewFragment : Fragment(), Observer<Resource<List<MapMarkable>>> {

    private var mMap: GoogleMap? = null

    private val mMarkers: MutableMap<MapMarkable, Marker?> = mutableMapOf()

    override fun onChanged(data: Resource<List<MapMarkable>>?) {
        if(data?.data == null) return

        if(data.status.isSuccessful()) {

            if(mMarkers.isEmpty()) {
                data.data.forEach { mMarkers[it] = null }
                setCaseMarkers(data.data)
                return
            }

            val cases = data.data.toMutableList()
            loop@ for((oldCase, oldMarker) in mMarkers) {
                for(newCase in cases) {
                    if(oldCase == newCase) {
                        cases.remove(oldCase)
                        continue@loop
                    }
                }
                oldMarker?.remove()
                mMarkers.remove(oldCase)
            }
            setCaseMarkers(cases)
        }
    }

    // fix that!: googleMap.isMyLocationEnabled = true
    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        view.mapView.onCreate(savedInstanceState)

        view.mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        view.mapView.getMapAsync { map ->
            mMap = map

            // For showing a move to my location button
            map.isMyLocationEnabled = true

            // TODO Find best bound coordinates
            val bounds = LatLngBounds(LatLng(52.3050934, 9.4635117), LatLng(52.5386801, 9.9908932))
            map.setLatLngBoundsForCameraTarget(bounds)
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))

            setCaseMarkers(mMarkers.keys)

        }

        return view
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