package de.unihannover.se.tauben2.view.main.fragments

import androidx.annotation.StringRes
import de.unihannover.se.tauben2.view.main.MainActivity

abstract class BaseMainFragment(@StringRes mTitleRes: Int): BaseFragment(mTitleRes) {

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).disableBackButton()
    }

}