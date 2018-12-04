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
            val injuryList = binding.c?.injury?.toStringList() ?: listOf()
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, injuryList)
            /*val params = v.injury_card_value.layoutParams
            params.height = injuryList.size*50
            v.injury_card_value.layoutParams = params*/
            v.injury_card_value.adapter=adapter

        }

        return binding.root
    }


}
