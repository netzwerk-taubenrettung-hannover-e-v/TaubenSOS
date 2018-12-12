package de.unihannover.se.tauben2.view.recycler

import androidx.databinding.ViewDataBinding
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardUserBinding
import de.unihannover.se.tauben2.model.entity.User

class UsersRecyclerFragment : RecyclerFragment<User>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_user

    private lateinit var user: User

    override fun onBindData(binding: ViewDataBinding, data: User) {
        this.user = data
        if(binding is CardUserBinding) {
            binding.c = data
        }
    }

}