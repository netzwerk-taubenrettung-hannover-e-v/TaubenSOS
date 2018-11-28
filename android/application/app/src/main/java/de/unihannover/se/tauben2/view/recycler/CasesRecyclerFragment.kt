package de.unihannover.se.tauben2.view.recycler

import android.location.Location
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardCaseBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.card_case.view.*

class CasesRecyclerFragment : RecyclerFragment<Case>() {
    override fun getRecylcerItemLayoutId(viewType: Int) = R.layout.card_case

    private var mExpandedPosition = -1

    private var mLocation: Location? = null

    override fun onResume() {
        super.onResume()
        val viewModel = getViewModel(LocationViewModel::class.java)
        viewModel?.observeCurrentLocation(this, Observer {
            mLocation = it
            notifyDataSetChanged()
        })
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
        }
    }

    override fun onDataLoaded(itemView: View, position: Int) {
        val isExpanded = position == mExpandedPosition

        itemView.expand_card.visibility = if (isExpanded) View.VISIBLE else View.GONE
        itemView.isActivated = isExpanded
        itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            // TransitionManager.beginDelayedTransition(recyclerView);
            notifyDataSetChanged()
        }
    }
}