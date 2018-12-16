package de.unihannover.se.tauben2.model.database.converter

import androidx.room.TypeConverter
import de.unihannover.se.tauben2.model.database.Permission

class PermissionConverter {

    @TypeConverter
    fun toInteger(enum : Permission) = enum.ordinal

    @TypeConverter
    fun toPermission(ord : Int) = when (ord ){
        0-> Permission.ADMIN
        1-> Permission.AUTHORISED
        2-> Permission.GUEST
        else-> Permission.GUEST
    }




}