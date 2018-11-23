package de.unihannover.se.tauben2.view.navigation

import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.model.LimitedAccessible
import de.unihannover.se.tauben2.model.Permission


data class FragmentMenuItem(val itemId: Int, val title: String, @DrawableRes val iconId: Int, val permission: Permission = Permission.GUEST, val getStartFragment: () -> Fragment): LimitedAccessible {
    override fun hasPermission(permission: Permission) = permission >= this.permission
}