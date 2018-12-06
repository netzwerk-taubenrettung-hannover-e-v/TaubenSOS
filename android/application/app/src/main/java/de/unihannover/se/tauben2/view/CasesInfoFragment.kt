package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCasesinfoBinding
import de.unihannover.se.tauben2.model.Injury
import de.unihannover.se.tauben2.model.entity.Case
import kotlinx.android.synthetic.main.fragment_casesinfo.view.*
import kotlinx.android.synthetic.main.notification_template_lines_media.view.*
import java.text.SimpleDateFormat
import java.util.*


class CasesInfoFragment: Fragment() {

    companion object : Singleton<CasesInfoFragment>() {
        override fun newInstance() = CasesInfoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCasesinfoBinding>(inflater, R.layout.fragment_casesinfo, container, false)
        arguments?.getParcelable<Case>("case")?.let {
            binding.c = it

            val media = it.media
            val views = listOf(binding.root.media_00_card, binding.root.media_01_card, binding.root.media_02_card)

            views.forEachIndexed { i, image ->
                Picasso.get().load(if(media.size >= i+1) media[i] else null)
                        .placeholder(R.drawable.ic_logo_48dp)
                        .into(image)
            }
        }
        binding.root.let{v->
            //convert injury into string list and pass it to the list view
            val injuryList = binding.c?.injury?.toStringList() ?: listOf()
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, injuryList)
            v.injury_card_value.adapter=adapter

            v.additional_information_card_textfield.text = binding.c?.additionalInfo

            //set date string
            val timestamp = binding.c?.timestamp
            if(timestamp!=null){
                val sdf = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
                val netDate = Date(timestamp*1000)
                var formattedDate = sdf.format(netDate)
                val sinceString = binding.c?.getSinceString()
                v.date_time_card_value.text = "$formattedDate$sinceString"

            }

            //TODO: Automatically scale height of injury_card_value and additional_information_card_textfield based on number of injuries/text length
            /*val params = v.injury_card_value.layoutParams
            params.height = injuryList.size*50
            v.injury_card_value.layoutParams = params*/
        }

        return binding.root
    }


}
