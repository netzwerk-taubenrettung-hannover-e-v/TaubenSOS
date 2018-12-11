package de.unihannover.se.tauben2.view.navigation

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {

    private var menuItems: List<FragmentMenuItem> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        view?.let {v ->
            val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
            controller.currentDestination?.defaultArguments?.getParcelableArrayList<FragmentMenuItem>("items")?.let {
                menuItems =  it
            }
            menuItems.forEach { v.more_navigation.menu.add(Menu.NONE, it.itemId, Menu.NONE, it.title).setIcon(it.iconId) }

            v.more_navigation.setNavigationItemSelectedListener {
                controller.navigate(it.itemId)
                true
            }
        }


        return view
    }
}