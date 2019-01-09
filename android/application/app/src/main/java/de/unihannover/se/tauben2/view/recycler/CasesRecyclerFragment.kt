package de.unihannover.se.tauben2.view.recycler

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardCaseBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.SquareImageView
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.card_case.view.*
import kotlinx.android.synthetic.main.fragment_cases.view.*

class CasesRecyclerFragment : RecyclerFragment<Case>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_case

    private var mLocation: Location? = null

    //TODO remove workaround
    var alreadyLoaded: Boolean = false


    private val locationObserver = Observer<Location?> {
        mLocation = it
        if(!alreadyLoaded) {
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
        if(binding is CardCaseBinding) {
            binding.c = data
            mLocation?.let { location ->
                val caseLoc = Location("")
                caseLoc.latitude = data.latitude
                caseLoc.longitude = data.longitude
                val res = ((Math.round(location.distanceTo(caseLoc)/10))/100.0).toString() + " km"
                binding.root.distance_text_card_value.text = res
            }


            val squareImgV = binding.root.image_card
            Picasso.get().load(if(data.media.isEmpty()) null else data.media[0])
                    .placeholder(R.drawable.ic_logo_48dp)
                    .into(squareImgV)

            if(squareImgV is SquareImageView && data.media.isNotEmpty()){
                activity?.let {
                    squareImgV.zoomImage(it.findViewById(R.id.image_expanded), it.findViewById(R.id.layout_main), it.findViewById(R.id.you_must_be_kidding_fix))
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