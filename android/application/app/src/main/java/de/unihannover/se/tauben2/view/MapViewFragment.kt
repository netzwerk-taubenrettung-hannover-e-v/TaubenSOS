package de.unihannover.se.tauben2.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import de.unihannover.se.tauben2.R
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import de.unihannover.se.tauben2.databinding.FragmentCasesinfoBinding
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.network.Resource
import kotlinx.android.synthetic.main.fragment_cases.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.android.synthetic.main.fragment_report02.*
import org.json.JSONException
import java.util.*
import android.os.Handler

class MapViewFragment : SupportMapFragment(), Observer<List<MapMarkable>> {

    private var mMap: GoogleMap? = null
    private val mMarkers: MutableMap<MapMarkable, Marker?> = mutableMapOf()
    private var selectedPosition : Marker? = null

    override fun onChanged(data: List<MapMarkable>) {
        if(mMarkers.isEmpty()) {
            data.forEach { mMarkers[it] = null }
            setCaseMarkers(data)
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

        var LastPos : LatLng? = null
        var LastMarker : Marker?? = null

         mMap?.setOnMarkerClickListener {
            //TODO find MarkerCase
                mMarkers.keys.forEach { marker ->
                    if (LastPos != null && LastMarker != null) {
                        if (LastPos == marker.getMarker().position && LastMarker == it) {
                            val bundle = Bundle()
                            bundle.putParcelable("case", marker.getMarkerCase())
                            Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.casesInfoFragment, bundle)
                        }
                    }
                }
             LastPos = it.position
             LastMarker = it
            false
        }
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
                //addHeatMap()
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

    private fun addHeatMap() {

        // Bounds
        // LatLng(52.3050934, 9.4635117)
        // LatLng(52.5386801, 9.9908932)

        var testlist : MutableList<WeightedLatLng> = mutableListOf()

        for (i in 1..1000) {
            var r1 = Random().nextDouble()
            testlist.add(WeightedLatLng(
                    LatLng(52.3050934 + (52.5386801 - 52.3050934) * Random().nextDouble(), 9.4635117 + (9.9908932 - 9.4635117) * Random().nextDouble()), 10 * Random().nextDouble()))
        }

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        var mProvider = HeatmapTileProvider.Builder()
                .weightedData(testlist)
                .build()
        // Add a tile overlay to the map, using the heat map tile provider.
        var mOverlay = mMap?.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))

        // mOverlay?.remove()
    }
}