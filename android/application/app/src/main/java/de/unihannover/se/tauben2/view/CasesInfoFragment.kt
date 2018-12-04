package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R

import de.unihannover.se.tauben2.databinding.FragmentCasesinfoBinding
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.model.entity.Injury

import kotlinx.android.synthetic.main.fragment_casesinfo.view.*


class CasesInfoFragment: Fragment() {

    private var injury = de.unihannover.se.tauben2.model.entity.Injury(1,true,false,false,true,true,false,false) //TODO: Change to real data once the local db structure allows for that


    companion object : Singleton<CasesInfoFragment>() {
        override fun newInstance() = CasesInfoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCasesinfoBinding>(inflater, R.layout.fragment_casesinfo, container, false)
        arguments?.getParcelable<Case>("case")?.let {
            binding.c = it
        }

        // Set Status Color
        if (binding.c?.isClosed == true) {
            binding.root.status_card_image_info.setColorFilter(ContextCompat.getColor(context!!, R.color.Green))
        } else {
            if (binding.c?.rescuer != null) {
                binding.root.status_card_image_info.setColorFilter(ContextCompat.getColor(context!!, R.color.Yellow))
            } else {
                binding.root.status_card_image_info.setColorFilter(ContextCompat.getColor(context!!, R.color.Red))
            }
        }

        binding.root.let{v->
            val injuryList = convertInjuryToStringList(injury)
            val adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, injuryList)
            /*val params = v.injury_card_value.layoutParams
            params.height = injuryList.size*50
            v.injury_card_value.layoutParams = params*/
            v.injury_card_value.adapter=adapter

        }

        return binding.root
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
