package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.list.ContactItem
import de.unihannover.se.tauben2.view.list.ContactItemAdapter
import kotlinx.android.synthetic.main.fragment_contact.view.*

class ContactFragment : Fragment() {

    private val contactItems = arrayListOf(
            ContactItem("Contact us by mail:", "E-Mail",
                    R.drawable.ic_baseline_mail_outline_24px, R.id.contact_button_mail),
            ContactItem("Contact us by phone:", "Call",
                    R.drawable.ic_baseline_phone_24px, R.id.contact_button_call),
            ContactItem("Stay up to date:", "Facebook",
                    R.drawable.ic_facebook, R.id.contact_button_facebook),
            ContactItem("Visit our website", "Website",
                    R.drawable.ic_public_white_24dp, R.id.contact_button_website))


    companion object : Singleton<ContactFragment>() {
        override fun newInstance() = ContactFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        val contactListAdapter = ContactItemAdapter(view.context, contactItems)
        view.contact_listview.adapter = contactListAdapter
        return view
    }
}