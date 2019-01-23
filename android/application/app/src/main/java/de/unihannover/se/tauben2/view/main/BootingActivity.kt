package de.unihannover.se.tauben2.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.model.network.Resource
import de.unihannover.se.tauben2.viewmodel.UserViewModel

class BootingActivity : AppCompatActivity() {

    private val mOwnerObserver: Observer<Resource<User>>
    private lateinit var mUserViewModel: UserViewModel

    companion object {

        var owner: User? = null

        @JvmStatic
        fun getOwnerPermission() = owner?.getPermission() ?: Permission.GUEST

    }

    init {
        mOwnerObserver = Observer {

            if(it == null || it.hasError()) {
                finishBooting()
                return@Observer
            }

            if(it.status.isSuccessful()) {
                owner = it.data
                finishBooting()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        owner = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booting)

        mUserViewModel = getViewModel(UserViewModel::class.java)

        mUserViewModel.owner.observe(this, mOwnerObserver)
    }

    private fun finishBooting() {
        mUserViewModel.owner.removeObserver(mOwnerObserver)
        Intent(this, MainActivity::class.java).apply { startActivity(this) }
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }
}
