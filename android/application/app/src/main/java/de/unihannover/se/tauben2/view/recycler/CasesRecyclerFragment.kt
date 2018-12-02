package de.unihannover.se.tauben2.view.recycler

import android.app.Activity
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardCaseBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.Injury
import de.unihannover.se.tauben2.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.card_case.view.*

import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation


class CasesRecyclerFragment : RecyclerFragment<Case>() {
    override fun getRecylcerItemLayoutId(viewType: Int) = R.layout.card_case

    private var mExpandedPosition = -1

    private var mLocation: Location? = null

    private lateinit var case: Case

    private var injury = de.unihannover.se.tauben2.model.entity.Injury(1,true,false,true,true,true,false,false) //TEMPORARY FOR TESTING


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val injuryList = convertInjuryToStringList(injury);

        val adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, injuryList)
        //injury_card_value.adapter=adapter
    }

    override fun onResume() {
        super.onResume()
        val viewModel = getViewModel(LocationViewModel::class.java)
        viewModel?.observeCurrentLocation(this, Observer {
            mLocation = it
            notifyDataSetChanged()
        })
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

            // Set Status Color
            if (case.isClosed) {
                binding.root.status_card_image.setColorFilter(ContextCompat.getColor(context!!, R.color.Green))
            } else {
                if (case.rescuer != null) {
                    binding.root.status_card_image.setColorFilter(ContextCompat.getColor(context!!, R.color.Yellow))
                } else {
                    binding.root.status_card_image.setColorFilter(ContextCompat.getColor(context!!, R.color.Red))
                }
            }
        }
    }

    override fun onDataLoaded(itemView: View, position: Int) {
        val isExpanded = position == mExpandedPosition

        itemView.setOnClickListener {
            // notifyDataSetChanged()
            val bundle = Bundle()
            bundle.putParcelable("case", case)
            Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.casesInfoFragment, bundle)
        }

    }

    private fun convertInjuryToStringList(injury: Injury) : List<String>{
        val injuryList = mutableListOf<String>()
        if(injury.footOrLeg) injuryList.add("Verletzter Fuß")
        if(injury.wing) injuryList.add("Verletzter Flügel")
        if(injury.head) injuryList.add("Verletzter Kopf")
        if(injury.openWound) injuryList.add("Offene Wunde")
        if(injury.paralyzedOrFlightless) injuryList.add("Bewegungs- oder Flugunfähig")
        if(injury.chick) injuryList.add("Küken")
        if(injury.other) injuryList.add("Sonstige:")                                       //TODO: Add actual text of other field either here or in CasesRecyclerFragment.kt
        return injuryList
    }
}