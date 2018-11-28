package de.unihannover.se.tauben2.view.navigation

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.Singleton
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {

    private var menuItems: List<FragmentMenuItem> = listOf()
    companion object: Singleton<MoreFragment>() {
        override fun newInstance() = MoreFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

//        view.more_navigation.setNavigationItemSelectedListener {
//            for(i in 0 until menuItems.size) {
//                val item = menuItems[i]
//                if (item.itemId == it.itemId) {
//                    (activity as FragmentChangeListener).replaceFragment(item.getFragment())
//                    return@setNavigationItemSelectedListener true
//                }
//            }
//            return@setNavigationItemSelectedListener false
//        }

        view?.let {v ->
            val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
            controller.currentDestination?.defaultArguments?.getParcelableArrayList<FragmentMenuItem>("items")?.let {
                menuItems =  it
            }
            menuItems.forEach { v.more_navigation.menu.add(Menu.NONE, it.itemId, Menu.NONE, it.title).setIcon(it.iconId) }
//            val host = childFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
//            NavigationUI.setupWithNavController(v.more_navigation, host.navController)
//            NavigationUI.setupWithNavController(v.more_navigation, host.navController)

            v.more_navigation.setNavigationItemSelectedListener {
                controller.navigate(it.itemId)
                true
            }
        }


        return view
    }
}