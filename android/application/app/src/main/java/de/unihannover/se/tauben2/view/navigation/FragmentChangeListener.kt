package de.unihannover.se.tauben2.view.navigation

import androidx.fragment.app.Fragment

interface FragmentChangeListener {
    fun replaceFragment(fragment: Fragment)
}