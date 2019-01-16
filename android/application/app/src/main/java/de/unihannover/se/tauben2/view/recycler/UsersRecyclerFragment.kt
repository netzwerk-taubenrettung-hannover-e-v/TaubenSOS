package de.unihannover.se.tauben2.view.recycler

import androidx.databinding.ViewDataBinding
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardUserBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.Auth
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.viewmodel.UserViewModel

class UsersRecyclerFragment : RecyclerFragment<User>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_user

    private lateinit var user: User

    override fun onBindData(binding: ViewDataBinding, data: User) {

        val vm = getViewModel(UserViewModel::class.java)

        this.user = data
        if (binding is CardUserBinding) {
            binding.c = data
            binding.cardUserIsAuthorized.setOnClickListener {
                vm?.updatePermissions(data.username, Auth(data.isActivated, data.isAdmin))
            }
            binding.cardUserIsAdmin.setOnClickListener {
                vm?.updatePermissions(data.username, Auth(data.isActivated, data.isAdmin))
            }
        }
    }
}

