package de.unihannover.se.tauben2.view.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {

    private lateinit var menuItems: List<FragmentMenuItem>

    companion object {
        fun newInstance(menuItems: List<FragmentMenuItem>): MoreFragment {
            val frag = MoreFragment()
            frag.menuItems = menuItems
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_more, container, false)
        menuItems.forEach { view.more_navigation.menu.add(Menu.NONE, it.itemId, Menu.NONE, it.title).setIcon(it.iconId) }
        view.more_navigation.setNavigationItemSelectedListener {
            for(i in 0 until menuItems.size) {
                val item = menuItems[i]
                if (item.itemId == it.itemId) {
                    (activity as FragmentChangeListener).replaceFragment(item.getStartFragment())
                    return@setNavigationItemSelectedListener true
                }
            }
            return@setNavigationItemSelectedListener false
        }

        return view
    }
}