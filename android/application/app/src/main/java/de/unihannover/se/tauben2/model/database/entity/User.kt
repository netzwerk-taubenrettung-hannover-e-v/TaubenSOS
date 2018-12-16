package de.unihannover.se.tauben2.model.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.unihannover.se.tauben2.view.recycler.RecyclerItem
import kotlinx.android.parcel.Parcelize

/**
 * Entity for saving user id's and their permission level for accessing the app's content
 */
@Parcelize
@Entity(tableName = "user")
//data class User(@PrimaryKey val id: Int, var permission: Permission) : RecyclerItem, Parcelable {
data class User(@PrimaryKey val username: String,
                var isActivated : Boolean,
                var isAdmin : Boolean,
                var password : String,
                var phone : String?

) : RecyclerItem, Parcelable {

    override fun getType() = RecyclerItem.Type.ITEM
}