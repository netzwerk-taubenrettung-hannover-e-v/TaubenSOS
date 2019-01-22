package de.unihannover.se.tauben2.view.main.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.iid.FirebaseInstanceId
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_login
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.view.input.InputFilterRequired.Companion.allInputsFilled
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : BaseMainFragment(R.string.login) {

    companion object : Singleton<LoginFragment>() {
        override fun newInstance() = LoginFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_login, container, false)

        view.btn_login.setOnClickListener {

            val userViewModel = getViewModel(UserViewModel::class.java)

            if (allInputsFilled(view as ViewGroup)) {
                val username = view.edit_login_username.text.toString()
                val pw = edit_login_password.text.toString()
                val user = User(username, false, false, pw, null, null)

                FirebaseInstanceId.getInstance().instanceId.apply {
                    addOnSuccessListener { result ->
                        try {
                            userViewModel?.login(user.apply { registrationToken = result.token })
                            finishLogin(view)

                        } catch (e: Exception) {
                            showLoginErrorMessage(view)
                        }
                    }
                    addOnFailureListener {
                        try {
                            userViewModel?.login(user)
                            finishLogin(view)
                        } catch (e: Exception) {
                            showLoginErrorMessage(view)
                        }
                    }
                }


            } else {
                setSnackBar(view, "Please fill out all the fields!")
            }
        }

        return view
    }

    private fun finishLogin(view: View) {
        activity?.finish()
        Intent(context, BootingActivity::class.java).apply { startActivity(this) }
        setSnackBar(view, "Login successful!")
    }

    private fun showLoginErrorMessage(view: View) {
        setSnackBar(view, "Wrong username or password or not activated!")
    }
}