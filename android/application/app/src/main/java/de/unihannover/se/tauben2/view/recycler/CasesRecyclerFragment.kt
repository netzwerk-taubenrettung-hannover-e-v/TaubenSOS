package de.unihannover.se.tauben2.view.recycler

import android.app.Activity
import android.location.Location
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardCaseBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.SquareImageView
import de.unihannover.se.tauben2.view.main.MainActivity
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.card_case.view.*

class CasesRecyclerFragment : RecyclerFragment<Case>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_case

    private var mLocation: Location? = null

    //TODO remove workaround
    var alreadyLoaded: Boolean = false

    override fun onChanged(t: List<Case>?) {
        super.onChanged(t?.sortedByDescending { it.timestamp })
    }

    private val locationObserver = Observer<Location?> {
        mLocation = it
        if (!alreadyLoaded) {
            notifyDataSetChanged()
            alreadyLoaded = true
        }
    }

    override fun onResume() {
        super.onResume()
        getViewModel(LocationViewModel::class.java)?.observeCurrentLocation(this, locationObserver)
    }

    override fun onPause() {
        super.onPause()
        getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(locationObserver)
    }



    override fun onBindData(binding: ViewDataBinding, data: Case) {
        if (binding is CardCaseBinding) {
            binding.c = data
            mLocation?.let { location ->
                val caseLoc = Location("")
                caseLoc.latitude = data.latitude
                caseLoc.longitude = data.longitude
                val res = ((Math.round(location.distanceTo(caseLoc) / 10)) / 100.0).toString() + " km"
                binding.root.distance_text_card_value.text = res
            }


            val squareImgV = binding.root.image_card

            data.loadMediaFromServerInto(if(data.media.isEmpty()) null else data.media[0], squareImgV)

            if (squareImgV is SquareImageView && data.media.isNotEmpty()) {
                activity?.let {
                    // TODO Load full sized image only on Zoom
                    squareImgV.zoomImage(it.findViewById(R.id.image_expanded), it.findViewById(R.id.layout_main), it.findViewById(R.id.you_must_be_kidding_fix), activity as MainActivity)
                    squareImgV.addImageZoomListener (
                    {
                        data.loadMediaFromServerInto(data.media[0], it.findViewById(R.id.image_expanded), fit = false)
                    }, {
                        data.loadMediaFromServerInto(data.media[0], squareImgV)
                    })
                }
            }

            binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("case", data)
                Navigation.findNavController(it.context as Activity, R.id.nav_host).navigate(R.id.casesInfoFragment, bundle)
            }
        }
    }
}