package de.unihannover.se.tauben2.model

import de.unihannover.se.tauben2.model.database.Permission

interface LimitedAccessible {

    fun hasPermission(permission: Permission): Boolean
}