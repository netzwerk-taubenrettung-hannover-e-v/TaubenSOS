package de.unihannover.se.tauben2.view.recycler

import android.app.Activity
import android.location.Location
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardCaseBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.card_case.view.*
import android.widget.ImageView
import androidx.navigation.Navigation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_recyler_view.view.*

class CasesRecyclerFragment : RecyclerFragment<Case>() {

    override fun getRecylcerItemLayoutId(viewType: Int) = R.layout.card_case


    private lateinit var case: Case
    private var mLocation: Location? = null

    private val locationObserver = Observer<Location?> {
        mLocation = it
        notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        getViewModel(LocationViewModel::class.java)?.observeCurrentLocation(this, locationObserver)
        // TODO make scroll fix
//        view?.recylcer_view?.scrollToPosition(0)
//        view?.recylcer_view?.invalidate()
    }

    override fun onPause() {
        super.onPause()
        getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(locationObserver)
    }

    override fun onBindData(binding: ViewDataBinding, data: Case) {
        this.case = data
        if(binding is CardCaseBinding) {
            binding.c = data
            mLocation?.let { location ->
                val caseLoc = Location("")
                caseLoc.latitude = data.latitude
                caseLoc.longitude = data.longitude
                val res = ((Math.round(location.distanceTo(caseLoc)/10))/100.0).toString() + " km"
                binding.root.distance_text_card_value.text = res
            }

            Picasso.get().load(if(data.media.isEmpty()) null else data.media[0])
                    .placeholder(R.drawable.ic_logo_48dp)
                    .into(binding.root.findViewById<ImageView>(R.id.image_card))

            binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("case", data)
                Navigation.findNavController(it.context as Activity, R.id.nav_host).navigate(R.id.casesInfoFragment, bundle)
            }
        }
    }
}