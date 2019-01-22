package de.unihannover.se.tauben2.view.main.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.firebase.iid.FirebaseInstanceId
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_login
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.UserRegistrationToken
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
                            userViewModel?.login(user)
                            userViewModel?.updateRegistrationToken(user.username, UserRegistrationToken(result.token))
                            finishLogin()

                        } catch (e: Exception) {
                            showLoginErrorMessage(view)
                        }
                    }
                    addOnFailureListener {
                        try {
                            userViewModel?.login(user)
                            finishLogin()
                        } catch (e: Exception) {
                            showLoginErrorMessage(view)
                        }
                    }
                }


            } else {
                setSnackBar(view, getString(R.string.fill_out_all_fields))
                closeKeyboard()
            }
        }

        return view
    }

    private fun finishLogin() {
        activity?.finish()
        Intent(context, BootingActivity::class.java).apply { startActivity(this) }
    }

    private fun showLoginErrorMessage(view: View) {
        setSnackBar(view, getString(R.string.wrong_username_password))
        closeKeyboard()
    }

    private fun closeKeyboard () {
        activity?.let { it ->
            val imm : InputMethodManager = (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            view?.let { v ->
                imm.hideSoftInputFromWindow(v.edit_login_username.windowToken, 0)
                imm.hideSoftInputFromWindow(v.edit_login_password.windowToken, 0)
            }
        }
    }
}