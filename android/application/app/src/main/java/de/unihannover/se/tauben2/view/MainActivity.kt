package de.unihannover.se.tauben2.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener {item ->
            when (item.itemId) {
                R.id.item_news -> {
                    val fragment = NewsFragment.newInstance()
                    replaceFragment(fragment)
                }
                R.id.item_counter -> {
                    val fragment = CounterFragment.newInstance()
                    replaceFragment(fragment)
                }
                R.id.item_cases -> {
                    val fragment = CasesFragment.newInstance()
                    replaceFragment(fragment)
                }
                R.id.item_graphs -> {
                    val fragment = GraphsFragment.newInstance()
                    replaceFragment(fragment)
                }
                R.id.item_more -> {
                    val fragment = MoreFragment.newInstance()
                    replaceFragment(fragment)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment)
        fragmentTransaction.commit()
    }
}

