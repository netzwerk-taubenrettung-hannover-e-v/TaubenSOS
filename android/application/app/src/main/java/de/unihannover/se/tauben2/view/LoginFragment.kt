package de.unihannover.se.tauben2.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_login
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.input.InputFilterRequired.Companion.allInputsFilled
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*


class LoginFragment : Fragment() {

    companion object : Singleton<LoginFragment>() {
        override fun newInstance() = LoginFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_login, container, false)

        view.btn_login.setOnClickListener {

            val userViewModel = getViewModel(UserViewModel::class.java)
            try {
                if (allInputsFilled(view as ViewGroup)) {
                    val username = view.edit_login_username.text.toString()
                    val pw = edit_login_password.text.toString()
                    val user = User(username, false, false, pw, null)

                    userViewModel?.login(user)
                    setSnackBar(view, "Login successful!")

                    // TODO change in app permission

                    val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
                    controller.navigatorProvider.getNavigator(BottomNavigator::class.java).popFromBackStack()
                    controller.navigate(R.id.newsFragment)
                } else {
                    setSnackBar(view, "Please fill out all the fields!")
                }
            } catch (e: Exception) {
                setSnackBar(view, "Wrong username or password!")
            }

        }

        return view
    }
}