package de.unihannover.se.tauben2.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_register
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.User
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*

class RegisterFragment : Fragment() {

    companion object : Singleton<RegisterFragment>() {
        override fun newInstance() = RegisterFragment()

        private val LOG_TAG = RegisterFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_register, container, false)


        view.buttonRegister.setOnClickListener {

            val username = registerUsernameField.text.toString()
            val pw = edit_register_pw.text.toString()
            val confirmedPw = edit_register_confirm_pw.text.toString()

            when {
                pw != confirmedPw -> {
                    setSnackBar(view, "The passwords don't match!")
                }
                allInputsFilled(view as ViewGroup) -> {
                    val userViewModel = getViewModel(UserViewModel::class.java)
                    // TODO get phone number
                    userViewModel?.register(User(username, false, false, pw, ""))

                    setSnackBar(view, "Registration has been requested!")
                    // copy pasta
                    val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
                    controller.navigatorProvider.getNavigator(BottomNavigator::class.java).popFromBackStack()
                    controller.navigate(R.id.newsFragment)
                }
                else -> setSnackBar(view, "Please fill out all the fields to register!")
            }
        }
        return view
    }

    private fun allInputsFilled(viewGroup: ViewGroup): Boolean {
        for (i in 0 until viewGroup.childCount) {
            val curView = viewGroup.getChildAt(i)
            if (curView is EditText && curView.text.isBlank()) {
                return false
            }
        }
        return true
    }
}