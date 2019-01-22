package de.unihannover.se.tauben2.view.main.fragments

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@StringRes var mTitleRes: Int): Fragment() {

    override fun onStart() {
        super.onStart()
        activity?.title = getString(mTitleRes)
    }

}