package de.unihannover.se.tauben2.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_contact.view.*

class ContactFragment : Fragment() {

    companion object: Singleton<ContactFragment>() {
        override fun newInstance() = ContactFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        view.button_call.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:05751 918602")))
        }

        view.button_mail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:taubenrettung_hannover@yahoo.de"))
            startActivity(intent)
        }

        view.button_facebook.setOnClickListener {
            try {
                //start facebook app
                val uri = Uri.parse("fb://page/141319162866697")
                val facebookIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(facebookIntent)
            } catch (e: PackageManager.NameNotFoundException) {
                //start browser
                val uri = Uri.parse("https://www.facebook.com/netzwerk.taubenrettung")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }

        return view
    }
}