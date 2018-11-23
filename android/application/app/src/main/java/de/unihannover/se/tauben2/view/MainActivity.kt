package de.unihannover.se.tauben2.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.Permission
import de.unihannover.se.tauben2.view.navigation.FragmentMenuItem
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        bottom_navigation.setMenuItems(
                FragmentMenuItem(0, "News", R.drawable.ic_today_white_24dp) { NewsFragment.newInstance() },
                FragmentMenuItem(1, "Counter", R.drawable.ic_bubble_chart_white_24dp) {CounterFragment.newInstance() },
                FragmentMenuItem(2, "Cases", R.drawable.ic_assignment_white_24dp, Permission.ADMIN) { CasesFragment.newInstance() },
                FragmentMenuItem(3, "Graphs", R.drawable.ic_show_chart_white_24dp) {GraphsFragment.newInstance() },
                FragmentMenuItem(4, "Report a Dove", R.drawable.ic_bubble_chart_white_24dp) { NewsFragment.newInstance() },
                FragmentMenuItem(5, "Emergency Call", R.drawable.ic_assignment_white_24dp) { NewsFragment.newInstance() },
                FragmentMenuItem(6, "Contact", R.drawable.ic_show_chart_white_24dp) { ContactFragment.newInstance()},
                FragmentMenuItem(7, "Logout", R.drawable.ic_more_horiz_white_24dp) { NewsFragment.newInstance() }
        )

        bottom_navigation.setStartFragmentListener { fragment ->
            when(fragment) {
                is CasesFragment -> getViewModel(CaseViewModel::class.java).cases.filter { it.isClosed }.observe(this, fragment)
                else -> replaceFragment(fragment)
            }
        }
    }

    override fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment, fragment.toString())
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }
}

