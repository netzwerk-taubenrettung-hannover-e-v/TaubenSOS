package de.unihannover.se.tauben2.view.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.view.recycler.UsersRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.UserViewModel

class UsersFragment : Fragment() {

    private lateinit var recyclerFragment : UsersRecyclerFragment

    private var mCurrentObservedData: LiveDataRes<List<User>>? = null
    private lateinit var mCurrentObserver: LoadingObserver<List<User>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as UsersRecyclerFragment

        mCurrentObserver = LoadingObserver(successObserver = recyclerFragment)

        loadUsers()

        return view
    }

    private fun loadUsers () {

        getViewModel(UserViewModel::class.java)?.let { viewModel ->

            // Remove old Observers
            mCurrentObservedData?.removeObserver(mCurrentObserver)

            mCurrentObservedData = viewModel.users

            mCurrentObservedData?.observe(this, mCurrentObserver)
        }
    }

}