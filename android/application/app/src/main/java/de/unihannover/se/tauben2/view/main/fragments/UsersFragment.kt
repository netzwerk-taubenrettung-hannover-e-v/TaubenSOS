package de.unihannover.se.tauben2.view.main.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.view.main.MainActivity
import de.unihannover.se.tauben2.view.recycler.UsersRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.UserViewModel

class UsersFragment : BaseMainFragment(R.string.users) {

    private lateinit var recyclerFragment : UsersRecyclerFragment

    private var mCurrentObservedData: LiveDataRes<List<User>>? = null
    private lateinit var mCurrentObserver: LoadingObserver<List<User>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_users, container, false)
        recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as UsersRecyclerFragment

        mCurrentObserver = LoadingObserver(successObserver = recyclerFragment)

        loadUsers()

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.findItem(R.id.toolbar_reload)?.apply {
            isVisible = true
            setOnMenuItemClickListener {
                view?.let { v ->
                    getViewModel(UserViewModel::class.java)?.reloadUsersFromServer{ setSnackBar(v, getString(R.string.reload_successful)) }
                    return@setOnMenuItemClickListener true
                }
                false
            }
        }
    }

    private fun loadUsers () {

        getViewModel(UserViewModel::class.java)?.let { viewModel ->

            // Remove old Observers
            mCurrentObservedData?.removeObserver(mCurrentObserver)

            mCurrentObservedData = viewModel.users.filter { it.username != getViewModel(UserViewModel::class.java)?.getOwnerUsername() }

            mCurrentObservedData?.observe(this, mCurrentObserver)
        }
    }

}