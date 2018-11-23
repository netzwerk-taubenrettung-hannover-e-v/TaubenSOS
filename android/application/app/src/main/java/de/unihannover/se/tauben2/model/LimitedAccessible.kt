package de.unihannover.se.tauben2.model

interface LimitedAccessible {

    fun hasPermission(permission: Permission): Boolean
}