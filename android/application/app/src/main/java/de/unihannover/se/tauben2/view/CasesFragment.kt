package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.network.Resource
import kotlinx.android.synthetic.main.fragment_cases.*
import kotlinx.android.synthetic.main.fragment_cases.view.*


class CasesFragment : Fragment(), Observer<Resource<List<Case>>>, OnMapReadyCallback {

    override fun onMapReady(googleMap: GoogleMap?) {
        var mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
        val sydney = LatLng(-34.0, 151.0)
        mMap?.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }

    override fun onChanged(cases: Resource<List<Case>>?) {
        if (cases?.status?.isSuccessful() == true) {
            //view?.textView?.text = cases.data?.get(0)?.additionalInfo ?:"Probleme beim Laden"
        }
    }

    companion object {
        fun newInstance(): CasesFragment {
            return CasesFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cases, container, false)

        var mapFragment = fragmentManager?.findFragmentById(R.id.mapView)
        Log.d("FRAGEMTNAW", mapFragment.toString())
        return view
    }
}