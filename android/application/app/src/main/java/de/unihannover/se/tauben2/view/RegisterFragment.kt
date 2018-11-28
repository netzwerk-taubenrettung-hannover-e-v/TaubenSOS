package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R.layout.fragment_register

class RegisterFragment : Fragment() {

    companion object: Singleton<RegisterFragment>() {
        override fun newInstance() = RegisterFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragment_register, container, false)
    }
}