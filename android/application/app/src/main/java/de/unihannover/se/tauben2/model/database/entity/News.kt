package de.unihannover.se.tauben2.model.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.unihannover.se.tauben2.view.recycler.RecyclerItem
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "news")
data class News (@PrimaryKey val newstitle: String,
                var timestamp : String,
                var writtenBy : String,
                var newsText : String

) : RecyclerItem, Parcelable {

    override fun getType() = RecyclerItem.Type.ITEM
}