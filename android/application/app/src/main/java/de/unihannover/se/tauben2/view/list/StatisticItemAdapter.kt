package de.unihannover.se.tauben2.view.list

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.card_statistic.view.*


class StatisticItemAdapter(private val context: Context,
                           private val statisticItems: ArrayList<StatisticItem>) : BaseAdapter() {

    class ViewHolder {
        lateinit var card: CardView
        lateinit var titleTextView: TextView
        lateinit var descriptionTextView: TextView
        lateinit var desImageView: ImageView
    }

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val cardView: View
        val holder: StatisticItemAdapter.ViewHolder

        if (convertView == null) {

            cardView = inflater.inflate(R.layout.card_statistic, parent, false)
            holder = StatisticItemAdapter.ViewHolder()

            holder.card = cardView.statistic_card
            holder.titleTextView = cardView.title
            holder.descriptionTextView = cardView.description
            holder.desImageView = cardView.squareImageView
            cardView.tag = holder

        } else {
            cardView = convertView
            holder = convertView.tag as StatisticItemAdapter.ViewHolder
        }

        val curItem = getItem(position) as StatisticItem

        holder.titleTextView.text = curItem.title
        holder.descriptionTextView.text = curItem.description
        holder.desImageView.setImageResource(curItem.imageId)

        holder.card.setOnClickListener{
            Navigation.findNavController(context as Activity, R.id.nav_host).navigate(curItem.destinationId)
        }

        return cardView
    }

    override fun getItem(position: Int): Any {
        return statisticItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return statisticItems.size
    }

}