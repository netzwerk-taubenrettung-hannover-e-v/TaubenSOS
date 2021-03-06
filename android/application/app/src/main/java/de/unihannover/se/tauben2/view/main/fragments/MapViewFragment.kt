package de.unihannover.se.tauben2.view.main.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.MapMarkable
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.PopulationMarker
import de.unihannover.se.tauben2.view.main.fragments.cases.CasesAdminFragment
import de.unihannover.se.tauben2.view.main.fragments.cases.CasesFragment
import de.unihannover.se.tauben2.view.report.LocationReportFragment
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import java.util.*

class MapViewFragment : SupportMapFragment(), Observer<List<MapMarkable>> {

    var mMap: GoogleMap? = null
    private val mMarkers: MutableMap<MapMarkable, Pair<Marker, Circle?>?> = mutableMapOf()
    private var selectedPosition: Marker? = null
    var circle: Circle? = null
    private var selectedArea: Polygon? = null

    private val hanBounds = LatLngBounds(LatLng(52.2050934, 9.4635117), LatLng(52.5386801, 9.9908932))


    override fun onStart() {
        super.onStart()
        context?.let {
            val permission = ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION)

            if (permission == PackageManager.PERMISSION_GRANTED)
                mMap?.isMyLocationEnabled = true

        }
    }

    override fun onChanged(data: List<MapMarkable>) {

        if (mMarkers.isEmpty()) {
            data.forEach { mMarkers[it] = null }
            setMarkers(data)
        }
        val casesToRemove: MutableMap<MapMarkable, Pair<Marker, Circle?>?> = mutableMapOf()

        loop@ for ((oldCase, oldMarker) in mMarkers) {
            for (newCase in data) {
                if (newCase == oldCase)
                    continue@loop
            }
            casesToRemove[oldCase] = oldMarker
        }

        for ((oldCase, oldMarker) in casesToRemove) {
            oldMarker?.first?.remove()
            oldMarker?.second?.remove()
            mMarkers.remove(oldCase)
        }

        setMarkers(data)
    }

    // fix that!: googleMap.isMyLocationEnabled = true
//    @SuppressLint("MissingPermission")
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
                context?.let { act ->
                    val permission = ContextCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION)

                    if (permission != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                    else
                        map.isMyLocationEnabled = true

                }

                map.setMinZoomPreference(9.5f)

                map.uiSettings.isRotateGesturesEnabled = false
                map.uiSettings.isTiltGesturesEnabled = false
                map.setLatLngBoundsForCameraTarget(hanBounds)
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(hanBounds, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, 0))
                map.clear()
                setMarkers(mMarkers.keys)


                when (this.parentFragment) {
                    is CasesFragment -> {
                        mMap?.setOnInfoWindowClickListener { clickedMarker ->
                            //TODO find MarkerCase

                            val filter = mMarkers.filter { it.value?.first == clickedMarker }
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

                        map.setOnInfoWindowClickListener { clickedMarker ->
                            val filter = mMarkers.filter { it.value?.first == clickedMarker }
                            if (filter.size == 1) {
                                val populationMarker = filter.keys.toList()[0] as PopulationMarker
                                val bundle = Bundle().apply { putInt("marker", populationMarker.populationMarkerID) }
                                Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.counterInfoFragment, bundle)
                            }
                        }
                    }
                }
            }
        }

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        context?.let { cxt ->
            val permission = ContextCompat.checkSelfPermission(cxt, Manifest.permission.ACCESS_FINE_LOCATION)

            if (permission == PackageManager.PERMISSION_GRANTED)
                    mMap?.isMyLocationEnabled = true
//
//            if (requestCode == 1) {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                }
//            }
        }
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
                val bounds: LatLngBounds = LatLngBounds(pos.position, pos.position)
                map.setLatLngBoundsForCameraTarget(bounds)
            }
        }
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

    private fun setMarkers(markers: Collection<MapMarkable>) {
        mMap?.let { map ->
            markers.forEach { marker ->
                if (mMarkers[marker] == null) {
                    var c: Circle? = null
                    if (marker is PopulationMarker) {

                        c = map.addCircle(CircleOptions()
                                .center(marker.getMarker().position)
                                .radius(marker.radius)
                                .strokeColor(App.getColor(R.color.colorPrimaryDarkTransparent))
                                .fillColor(App.getColor(R.color.colorPrimaryTransparent)))
                    }
                    mMarkers[marker]= Pair(map.addMarker(marker.getMarker()), c)
                }
            }
        }
    }


    private fun addHeatMap() {

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

        val mProvider = HeatmapTileProvider.Builder()
                .weightedData(testlist)
                .gradient(gradient)
                .radius(50)
                .opacity(0.5)
                .build()

        var mOverlay = mMap?.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))
        //mOverlay?.remove()
    }

    fun getNorthEast () : LatLng {
        mMap?.let {
            val ne = it.projection.visibleRegion.latLngBounds.northeast
            return ne
        }
       return LatLng(0.0,0.0)
    }

    fun getSouthWest () : LatLng {
        mMap?.let {
            val sw = it.projection.visibleRegion.latLngBounds.southwest
            return sw
        }
        return LatLng(0.0,0.0)
    }

    fun markArea () {

        mMap?.let {
            val northeast = it.projection.visibleRegion.latLngBounds.northeast
            val southwest = it.projection.visibleRegion.latLngBounds.southwest
            val northwest = LatLng(it.projection.visibleRegion.latLngBounds.northeast.latitude, it.projection.visibleRegion.latLngBounds.southwest.longitude)
            val southeast = LatLng(it.projection.visibleRegion.latLngBounds.southwest.latitude, it.projection.visibleRegion.latLngBounds.northeast.longitude)

            selectedArea?.let(Polygon::remove)

            selectedArea = it.addPolygon(PolygonOptions()
                    .add(northwest, northeast, southeast, southwest)
                    .strokeWidth(2F)
                    .strokeColor(R.color.colorPrimaryDark))

        }
    }
}