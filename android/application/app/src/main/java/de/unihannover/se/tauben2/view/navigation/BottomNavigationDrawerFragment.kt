package de.unihannover.se.tauben2.view.navigation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_bottomsheet.*

class BottomNavigationDrawerFragment: BottomSheetDialogFragment() {

    companion object: Singleton<BottomNavigationDrawerFragment>() {
        override fun newInstance() = BottomNavigationDrawerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottomsheet, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            // Bottom Navigation Drawer menu item clicks
            when (menuItem!!.itemId) {
                R.id.nav1 -> {Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.editNewsFragment, BottomNavigationDrawerFragment.bundle); this.dismiss()}
                R.id.nav2 -> {
                    activity.let{
                    if(it!=null){
                        val vm = ViewModelProviders.of(it).get(NewsViewModel::class.java)
                        vm.newsPost.value?.data.let{n->
                            if(n!=null) {
                                vm.deleteNews(n)
                            }
                        }
                    }
                }
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            true
        }
    }

    // This is an extension method for easy Toast call
    fun Context.toast(message: CharSequence) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 600)
        toast.show()
    }
}