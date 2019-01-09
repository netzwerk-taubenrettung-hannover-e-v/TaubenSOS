package de.unihannover.se.tauben2.view.navigation

import android.os.Parcelable
import androidx.annotation.DrawableRes
import de.unihannover.se.tauben2.model.LimitedAccessible
import de.unihannover.se.tauben2.model.database.Permission
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FragmentMenuItem(val itemId: Int, val title: String, @DrawableRes val iconId: Int, val permission: Permission = Permission.GUEST, val onlyThatPermission: Boolean = false): LimitedAccessible, Parcelable {
    override fun hasPermission(permission: Permission) = if(onlyThatPermission) permission == this.permission else permission >= this.permission
}