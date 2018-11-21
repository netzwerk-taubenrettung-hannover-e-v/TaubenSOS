package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_more.*

class MoreFragment : Fragment() {

    companion object {
        fun newInstance(): MoreFragment {
            return MoreFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onStart() {
        super.onStart()

        more_navigation.setNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.item_report_dove -> {

                }
                R.id.item_emergency_call -> {
                    (activity as FragmentChangeListener).replaceFragment(EmergencyCallFragment.newInstance())
                }
                R.id.item_contact -> {
                    (activity as FragmentChangeListener).replaceFragment(ContactFragment.newInstance())
                }
                R.id.item_logout -> {
                   
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
}