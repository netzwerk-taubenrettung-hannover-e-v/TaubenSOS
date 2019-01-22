package de.unihannover.se.tauben2.view.main.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.list.ContactItem
import de.unihannover.se.tauben2.view.list.ContactItemAdapter
import kotlinx.android.synthetic.main.fragment_contact.view.*

class ContactFragment : BaseMainFragment(R.string.contact) {

    companion object {
        val PHONE: Uri = Uri.parse("tel:05751 918602")
        val MAIL: Uri = Uri.parse("mailto:taubenrettung_hannover@yahoo.de")
        val WEBSITE: Uri  = Uri.parse("http://taubenrettung-hannover.de/")
        val FACEBOOK_WEBSITE: Uri = Uri.parse("https://www.facebook.com/netzwerk.taubenrettung")
        val FACEBOOK_APP: Uri = Uri.parse("fb://page/141319162866697")
    }

    private var contactItems = listOf<ContactItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        var facebookUri: Uri = FACEBOOK_WEBSITE

        try {
            val info = activity?.packageManager?.getPackageInfo("com.facebook.katana", PackageManager.GET_META_DATA)
            if(info != null)
                facebookUri = FACEBOOK_APP //Facebook app installed

        } catch (ignored: PackageManager.NameNotFoundException) {}

        contactItems = listOf(ContactItem(getString(R.string.contact_by_phone), getString(R.string.call),
                    R.drawable.ic_phone_primary, R.id.contact_button_call,
                    Intent(Intent.ACTION_DIAL, PHONE), R.id.infoButtonContact),
            ContactItem(getString(R.string.contact_by_mail), getString(R.string.mail),
                    R.drawable.ic_mail, R.id.contact_button_mail,
                    Intent(Intent.ACTION_SENDTO, MAIL), null),
            ContactItem(getString(R.string.contact_by_facebook), getString(R.string.facebook),
                    R.drawable.ic_facebook, R.id.contact_button_facebook,
                    Intent(Intent.ACTION_VIEW, facebookUri),null),
            ContactItem(getString(R.string.contact_by_website), getString(R.string.website),
                    R.drawable.ic_world, R.id.contact_button_website,
                    Intent(Intent.ACTION_VIEW, WEBSITE), null))

        view.contact_listview.adapter = ContactItemAdapter(view.context, contactItems)

        return view
    }
}