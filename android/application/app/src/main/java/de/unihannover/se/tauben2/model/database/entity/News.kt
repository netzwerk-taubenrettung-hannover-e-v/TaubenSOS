package de.unihannover.se.tauben2.model.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.unihannover.se.tauben2.view.recycler.RecyclerItem
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "news")
data class News(@PrimaryKey
                val feedID: Int?,
                var author: String,
                var eventStart: Long?,
                var eventEnd: Long?,
                var text: String,
                var timestamp: Long,
                var title: String
) : RecyclerItem, Parcelable, DatabaseEntity() {
    override val refreshCooldown: Long
        get() = 0

    override fun getType() = RecyclerItem.Type.ITEM

    fun setToCurrentTime() {
        timestamp = System.currentTimeMillis() / 1000
    }

    companion object : AllUpdatable {

        override val refreshAllCooldown: Long
            get() = 900000 * 2 // 30 min
    }

    fun getEventStartMillis() = eventStart?.let { it * 1000 }
    fun getEventEndMillis() = eventEnd?.let { it * 1000 }

}