package de.unihannover.se.tauben2.view.navigation

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import de.unihannover.se.tauben2.R

@Navigator.Name("custom_fragment")
class BottomNavigator(
        private val activity: Activity,
        private val manager: FragmentManager,
        private val containerId: Int,
        private val bottomNavigationView: DynamicBottomNavigationView
): FragmentNavigator(activity, manager, containerId) {

    private val mBackStack: MutableList<Destination> = mutableListOf()

    private val mToRemove = listOf(R.id.casesInfoFragment)

//    private val mActionsList = listOf(R.id.action_report00Fragment_to_report01Fragment,
//            R.id.action_report01Fragment_to_report02Fragment,
//            R.id.action_moreFragment_to_navigation,
//            R.id.action_casesFragment_to_casesInfoFragment)


    override fun navigate(destination: Destination, args: Bundle?,
                          navOptions: NavOptions?, navigatorExtras: Navigator.Extras?): NavDestination? {

        val removeOldFragment= if(mBackStack.isNotEmpty())
            mToRemove.contains(mBackStack.last().id)
        else
            false
        replaceFragment(destination, removeOldFragment, args)

        return destination

    }

    fun onBackPressed() {
        if(mBackStack.size == 1)
            activity.finish()
        else {
            val removed = mBackStack.removeAt(mBackStack.lastIndex)
            val destination = mBackStack[mBackStack.size-1]
            replaceFragment(destination, mToRemove.contains(removed.id))
            bottomNavigationView.setSelectedItem(destination.id)
        }
    }

    private fun replaceFragment(destination: Destination, removeOldFragment: Boolean = false, args: Bundle? = null) {
        val tag = destination.id.toString()
        val transaction = manager.beginTransaction()

        manager.primaryNavigationFragment?.let { current ->
            if(removeOldFragment)
                transaction.remove(current)
            else
                transaction.detach(current)
//            if(current::class.java != bottomNavigationView.sele)
//                transaction.remove(current)
//            else
//                transaction.detach(current)

        }

        var fragment = manager.findFragmentByTag(tag)

        if (fragment == null) {
            fragment = instantiateFragment(activity, manager, destination.className, args)
            fragment.arguments = args

            transaction.replace(containerId, fragment, tag)
            mBackStack.add(destination)
        } else {
            mBackStack.remove(destination)
            mBackStack.add(destination)

            transaction.attach(fragment)
        }

        transaction.setPrimaryNavigationFragment(fragment)
        transaction.setReorderingAllowed(true)
        transaction.commit()
    }

    fun popFromBackStack(amount: Int = 1) {
        if(mBackStack.size < amount)
            return
        val transaction = manager.beginTransaction()
        for (i in mBackStack.size-1 downTo mBackStack.size-amount ) {
            val removed = mBackStack.removeAt(i)
            manager.findFragmentByTag(removed.id.toString())?.let {
                transaction.remove(it)
            }
        }
        transaction.commit()
    }

}