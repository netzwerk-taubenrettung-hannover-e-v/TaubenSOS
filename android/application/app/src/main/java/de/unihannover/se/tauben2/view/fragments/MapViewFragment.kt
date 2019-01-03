package de.unihannover.se.tauben2.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.SphericalUtil

import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.database.entity.Case
import java.util.*
import de.unihannover.se.tauben2.view.fragments.cases.CasesFragment
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
//import de.unihannover.se.tauben2.model.database.entity.PigeonCounter
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.report.LocationReportFragment

class MapViewFragment : SupportMapFragment(), Observer<List<MapMarkable>> {

    private var mMap: GoogleMap? = null
    private val mMarkers: MutableMap<MapMarkable, Marker?> = mutableMapOf()
    private var selectedPosition: Marker? = null
    private var circle: Circle? = null

    private val hanBounds = LatLngBounds(LatLng(52.3050934, 9.4635117), LatLng(52.5386801, 9.9908932))


    override fun onChanged(data: List<MapMarkable>) {

        if (mMarkers.isEmpty()) {
            data.forEach { mMarkers[it] = null }
            setCaseMarkers(data)
        }
        val casesToRemove: MutableMap<MapMarkable, Marker?> = mutableMapOf()

        loop@ for ((oldCase, oldMarker) in mMarkers) {
            for (newCase in data) {
                if (newCase == oldCase)
                    continue@loop
            }
            casesToRemove[oldCase] = oldMarker
        }

        for ((oldCase, oldMarker) in casesToRemove) {
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

                map.setMinZoomPreference(9.5f)
                // TODO Find best bound coordinates

                map.setLatLngBoundsForCameraTarget(hanBounds)
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(hanBounds, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, 0))
                setCaseMarkers(mMarkers.keys)




                when (this.parentFragment) {
                    is GraphsFragment -> addHeatMap()
                    is CasesFragment -> {
                        mMap?.setOnInfoWindowClickListener { clickedMarker ->
                            //TODO find MarkerCase

                            val filter = mMarkers.filter { it.value == clickedMarker }
                            if (filter.size == 1) {
                                val case = filter.keys.toList()[0] as? Case
                                        ?: return@setOnInfoWindowClickListener

                                val bundle = Bundle()
                                bundle.putParcelable("case", case)
                                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.casesInfoFragment, bundle)
                            }
                        }
                    }
                    is LocationReportFragment -> (this.parentFragment as LocationReportFragment).setMarker()
                    is CounterFragment -> {
                        /*mMap?.setOnMarkerClickListener { clickedMarker ->
                            val filter = mMarkers.filter { it.value == clickedMarker }
                            if (filter.size == 1) {
                                val populationMarker = filter.keys.toList()[0] as? PopulationMarker
                                (this.parentFragment as CounterFragment).mSelectedMarkerID = populationMarker?.populationMarkerID
                            }
                            false // enables default behaviour i.e. focusing the marker and opening the info window
                        }*/

                        map.setOnMarkerClickListener {
                            val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
                            controller.navigate(R.id.counterInfoFragment)
                            false
                        }
                    }
                }
            }
        }

        return view
    }

    // add a marker at the middle of the map and save it in 'selectedPosition'
    fun selectPosition(position: LatLng?) {
        // remove the old position if exists
        selectedPosition?.remove()

        // add marker
        val mo = MarkerOptions()
        if (position == null) mo.position(mMap!!.cameraPosition.target)
        else mo.position(position)
        mo.title("your selected position")
        mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        selectedPosition = mMap?.addMarker(mo)
    }

    fun getSelectedPosition(): LatLng? {
        return selectedPosition?.position
    }

    fun focusSelectedPosition() {
        mMap?.let { map ->
            selectedPosition?.let { pos ->
                var bounds: LatLngBounds = LatLngBounds(pos.position, pos.position)
                map.setLatLngBoundsForCameraTarget(bounds)
            }
        }
    }

    fun getCircle(): Circle? {
        return circle
    }

    fun chooseRadius() {
        mMap?.let { map ->
            selectedPosition?.let { pos ->
                var visRegion: VisibleRegion = map.projection.visibleRegion
                var left: LatLng = visRegion.farLeft
                var right: LatLng = visRegion.farRight
                var dist: Double = SphericalUtil.computeDistanceBetween(left, right)

                if (circle == null) {
                    circle = map.addCircle(CircleOptions()
                            .center(pos.position)
                            .radius(dist / 2.5)
                            .strokeColor(Color.argb(40, 59, 148, 225))
                            .fillColor(Color.argb(20, 59, 148, 225)))
                }


                map.setOnCameraMoveListener {
                    visRegion = map.projection.visibleRegion
                    left = visRegion.farLeft
                    right = visRegion.farRight
                    dist = SphericalUtil.computeDistanceBetween(left, right)
                    circle?.let { circle ->
                        circle.radius = dist / 2.5
                    }
                }
            }
        }
    }

    fun unfocusSelectedPosition() {
        mMap?.let { map ->
            map.setLatLngBoundsForCameraTarget(hanBounds)
        }
    }

    fun removeCircle() {
        circle?.let {
            it.remove()
            circle = null
        }
    }

    fun removeSelectedPosition() {
        selectedPosition?.let {
            it.remove()
        }
    }

    fun setCaseMarkers(markers: Collection<MapMarkable>) {
        mMap?.let { map ->
            markers.forEach { marker ->
                if (mMarkers[marker] == null)
                    mMarkers[marker] = map.addMarker(marker.getMarker())
                if (marker is PopulationMarker) {
                    map.addCircle(CircleOptions()
                            .center(marker.getMarker().position)
                            .radius(marker.radius)
                            .strokeColor(Color.argb(40, 59, 148, 225))
                            .fillColor(Color.argb(20, 59, 148, 225)))
                }
            }
        }
    }

    fun addHeatMap() {

        // Bounds
        // LatLng(52.3050934, 9.4635117)
        // LatLng(52.5386801, 9.9908932)

        // test data
        var testlist: MutableList<WeightedLatLng> = mutableListOf()
        for (i in 1..1000) {
            testlist.add(WeightedLatLng(
                    LatLng(52.3050934 + (52.5386801 - 52.3050934) * Random().nextDouble(), 9.4635117 + (9.9908932 - 9.4635117) * Random().nextDouble()), 100 * Random().nextDouble()))
        }

        val colors = intArrayOf(Color.rgb(255, 100, 100),
                Color.rgb(255, 0, 0))
        val startPoints = floatArrayOf(0.2f, 1f)
        val gradient = Gradient(colors, startPoints)

        var mProvider = HeatmapTileProvider.Builder()
                .weightedData(testlist)
                .gradient(gradient)
                .radius(50)
                .opacity(0.5)
                .build()

        var mOverlay = mMap?.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))
        //mOverlay?.remove()
    }
}