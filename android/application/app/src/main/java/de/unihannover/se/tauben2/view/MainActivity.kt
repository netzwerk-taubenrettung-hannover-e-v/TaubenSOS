package de.unihannover.se.tauben2.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.item_news -> {
                    replaceFragment(NewsFragment.newInstance())
                }
                R.id.item_counter -> {
                    replaceFragment(CounterFragment.newInstance())
                }
                R.id.item_cases -> {
                    val fragment = CasesFragment.newInstance()
                    getViewModel(CaseViewModel::class.java).cases.filter { it.isClosed }.observe(this, fragment)
                    replaceFragment(fragment)
                }
                R.id.item_graphs -> {
                    replaceFragment(GraphsFragment.newInstance())
                }
                R.id.item_more -> {
                    replaceFragment(MoreFragment.newInstance())
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun replaceFragment(fragment:Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment, fragment.toString())
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }
}

