package de.unihannover.se.tauben2.view.list

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.list.ContactItem
import kotlinx.android.synthetic.main.card_contact.view.*

class ContactItemAdapter(private val context: Context,
                         private val contactItems: ArrayList<ContactItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cardView = inflater.inflate(R.layout.card_contact, parent, false)

        val descriptionTextView: TextView = cardView.text_description
        val contactButton: MaterialButton = cardView.button_contact

        val curItem = getItem(position) as ContactItem

        descriptionTextView.text = curItem.description
        contactButton.text = curItem.buttonLabel
        contactButton.icon = context.getDrawable(curItem.buttonIconId)
        contactButton.id = curItem.buttonId
        addOnclickListeners(context, contactButton)

        return cardView
    }

    override fun getItem(position: Int): Any {
        return contactItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return contactItems.size
    }

    private fun addOnclickListeners(context: Context, contactButton: MaterialButton) {
        when (contactButton.id) {
            R.id.contact_button_mail -> {
                contactButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:taubenrettung_hannover@yahoo.de"))
                    context.startActivity(intent)
                }
            }
            R.id.contact_button_call -> {
                contactButton.setOnClickListener {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:05751 918602")))
                }
            }
            R.id.contact_button_facebook -> contactButton.setOnClickListener {
                try {
                    //start facebook app
                    val uri = Uri.parse("fb://page/141319162866697")
                    val facebookIntent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(facebookIntent)
                } catch (e: PackageManager.NameNotFoundException) {
                    //start browser
                    val uri = Uri.parse("https://www.facebook.com/netzwerk.taubenrettung")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                }
            }
            R.id.contact_button_website -> contactButton.setOnClickListener {
                val uri = Uri.parse("http://taubenrettung-hannover.de/")
                val websiteIntent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(websiteIntent)
            }
        }
    }

}