package de.unihannover.se.tauben2.view

import androidx.fragment.app.Fragment

interface FragmentChangeListener {
    fun replaceFragment(fragment: Fragment)
}