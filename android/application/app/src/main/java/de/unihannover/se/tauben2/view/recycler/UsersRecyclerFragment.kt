package de.unihannover.se.tauben2.view.recycler

import android.view.View.INVISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardUserBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.Auth
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.viewmodel.UserViewModel

class UsersRecyclerFragment : RecyclerFragment<User>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_user

    private lateinit var user: User

    override fun onBindData(binding: ViewDataBinding, data: User) {

        val vm = getViewModel(UserViewModel::class.java)

        this.user = data
        if (binding is CardUserBinding) {
            binding.c = data
            if (data.username != vm?.getOwnerUsername()) {
                binding.cardUserIsAuthorized.setOnClickListener {
                    vm?.updatePermissions(data.username, Auth(data.isActivated, data.isAdmin))
                }
                binding.cardUserIsAdmin.setOnClickListener {
                    vm?.updatePermissions(data.username, Auth(data.isActivated, data.isAdmin))
                }
                binding.cardBtnUserDelete.setOnClickListener {
                    context?.run {
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.title_deleting_user))
                            setMessage(getString(R.string.desc_delete_user))

                            setPositiveButton(R.string.delete) { _, _ ->
                                vm?.deleteUser(data)
                                setSnackBar(binding.root, getString(R.string.alert_user_deleted))
                            }

                            setNegativeButton(R.string.cancel) { dialogInterface, _ ->
                                dialogInterface.cancel()
                            }

                        }.show()
                    }

                }
            } else {
                binding.cardUserIsAuthorized.isEnabled = false
                binding.cardUserIsAdmin.isEnabled = false
                binding.cardBtnUserDelete.visibility = INVISIBLE

            }
        }
    }
}

