package de.unihannover.se.tauben2.view.navigation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_more.view.*

class MoreFragment : Fragment() {

    private var menuItems: List<FragmentMenuItem> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        view?.let { v ->
            val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
            controller.currentDestination?.defaultArguments?.getParcelableArrayList<FragmentMenuItem>("items")?.let {
                menuItems = it
            }
            menuItems.forEach { v.more_navigation.menu.add(Menu.NONE, it.itemId, Menu.NONE, it.title).setIcon(it.iconId) }

            v.more_navigation.setNavigationItemSelectedListener {
                if (it.itemId == R.id.button_logout) {
                    getViewModel(UserViewModel::class.java)?.let { vm ->
                        vm.logout()
                        setSnackBar(v, "Logout Successful")
                        activity?.finish()
                        Intent(context, BootingActivity::class.java).apply { startActivity(this) }
                    }
                    true
                } else {
                    controller.navigate(it.itemId)
                    true
                }
            }
        }


        return view
    }
}