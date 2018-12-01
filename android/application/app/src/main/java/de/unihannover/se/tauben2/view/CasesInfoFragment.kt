package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCasesinfoBinding
import de.unihannover.se.tauben2.model.entity.Case

class CasesInfoFragment: Fragment() {

    companion object : Singleton<CasesInfoFragment>() {
        override fun newInstance() = CasesInfoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCasesinfoBinding>(inflater, R.layout.fragment_casesinfo, container, false)
        arguments?.getParcelable<Case>("case")?.let {
            binding.c = it
        }
        return binding.root

    }


}
