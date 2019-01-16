package de.unihannover.se.tauben2.model.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.view.recycler.RecyclerItem
import kotlinx.android.parcel.Parcelize

/**
 * Entity for saving user id's and their permission level for accessing the app's content
 */
@Parcelize
@Entity(tableName = "user")
data class User(@PrimaryKey val username: String,
                var isActivated : Boolean,
                var isAdmin : Boolean,
                var password : String?,
                var phone : String?,
                var registrationToken: String?

) : RecyclerItem, Parcelable, DatabaseEntity() {

    override val refreshCooldown: Long
        get() = 300000 // 5 min

    fun getPermission() = when {
        isAdmin -> Permission.ADMIN
        isActivated -> Permission.AUTHORISED
        else -> Permission.GUEST
    }

    override fun getType() = RecyclerItem.Type.ITEM

    companion object: AllUpdatable {

        override val refreshAllCooldown: Long
            get() = 900000 * 2 // 30 min

    }
}