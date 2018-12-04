package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.Injury
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.fragment_report00.*
import kotlinx.android.synthetic.main.fragment_report00.view.*

class Report00Fragment : Fragment(), View.OnClickListener, Observer<Location?> {

    private var mUserLocation: Location? = null
    private var mCreatedCase: Case = Case(null, null, null, false,
            false, 0.0, 0.0, null, 1, 0,
            "", null, Injury(false, false, false,
            false, false, false, false))

    companion object : Singleton<Report00Fragment>() {
        override fun newInstance() = Report00Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report00, container, false)
        view.report_next_step_button.setOnClickListener(this)
        return view
    }

    override fun onResume() {
        super.onResume()
        val locationViewModel = getViewModel(LocationViewModel::class.java)
        locationViewModel?.observeCurrentLocation(this, this)
    }

    override fun onClick(view: View?) {

        when (view) {

            report_next_step_button -> {
                if (saveLocation()) {

                    val caseBundle = Bundle()
                    caseBundle.putParcelable("createdCase", mCreatedCase)

                    Navigation.findNavController(context as Activity, R.id.nav_host)
                            .navigate(R.id.report01Fragment, caseBundle)
                    getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(this)
                } else {
                    Toast.makeText(context, "No GPS Available", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onChanged(loc: Location?) {
        mUserLocation = loc
    }

    /**
     * saves location to created case
     * @return true if successful
     */
    private fun saveLocation(): Boolean {
        mUserLocation?.let {
            mCreatedCase.longitude = it.longitude
            mCreatedCase.latitude = it.latitude
            return true
        }
        return false
    }
}