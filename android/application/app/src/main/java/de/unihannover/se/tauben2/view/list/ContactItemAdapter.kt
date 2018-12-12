package de.unihannover.se.tauben2.view.list

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.id.infoButtonContact
import kotlinx.android.synthetic.main.card_contact.view.*
import kotlinx.android.synthetic.main.card_contact2.view.*

class ContactItemAdapter(private val context: Context,
                         private val contactItems: ArrayList<ContactItem>) : BaseAdapter() {

    class ViewHolder {
        lateinit var descriptionTextView: TextView
        lateinit var contactButton: MaterialButton
        var infoButton: ImageButton? = null
    }

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val cardView: View
        val holder: ViewHolder

        var isInfoButton = false
        if (convertView == null) {

            if(position == 0) {
                cardView = inflater.inflate(R.layout.card_contact2, parent, false)
                isInfoButton = true

            }else{
                cardView = inflater.inflate(R.layout.card_contact, parent, false)

            }
            holder = ViewHolder()

            if(isInfoButton) {
                holder.infoButton = cardView.infoButtonContact
                holder.descriptionTextView = cardView.text_description2
                holder.contactButton = cardView.button_contact2
            }else{
                holder.descriptionTextView = cardView.text_description
                holder.contactButton = cardView.button_contact
            }

            cardView.tag = holder


        } else {
            cardView = convertView
            holder = convertView.tag as ViewHolder
        }

        val descriptionTextView = holder.descriptionTextView
        val contactButton = holder.contactButton

        val curItem = getItem(position) as ContactItem

        descriptionTextView.text = curItem.description
        contactButton.text = curItem.buttonLabel
        contactButton.icon = context.getDrawable(curItem.buttonIconId)
        contactButton.id = curItem.buttonId
        if(isInfoButton){
            val contactInfoButton = holder.infoButton
            addInfoOnClickListener(context, contactInfoButton)
        }else{
            addOnclickListeners(context, contactButton)
        }

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

    private fun addInfoOnClickListener(context: Context, infoButton: ImageButton?){
        infoButton?.setOnClickListener{
            //Pop up for more info
            val alertDialogBuilder = AlertDialog.Builder(
                    context)

            alertDialogBuilder.setTitle("Anruf")

            alertDialogBuilder
                    .setMessage(R.string.contact_info)

            val alertDialog = alertDialogBuilder.create()

            alertDialog.show()
        }

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