package de.unihannover.se.tauben2.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_emergency_call.view.*

class EmergencyCallFragment : Fragment() {

    companion object: Singleton<EmergencyCallFragment>() {
        override fun newInstance() = EmergencyCallFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_emergency_call, container, false)
        view.callbutton.setOnClickListener {

            try {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:05751 918602"))
                startActivity(intent)
            } catch (e: PackageManager.NameNotFoundException) {
                Log.i("Error", "Something is wrong")
            }
        }
        return view
    }
}