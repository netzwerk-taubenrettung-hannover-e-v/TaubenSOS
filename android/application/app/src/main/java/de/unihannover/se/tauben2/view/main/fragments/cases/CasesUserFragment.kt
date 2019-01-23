package de.unihannover.se.tauben2.view.main.fragments.cases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.unihannover.se.tauben2.R

class CasesUserFragment : CasesFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        mFilter = Filter.MY

        return view
    }
}