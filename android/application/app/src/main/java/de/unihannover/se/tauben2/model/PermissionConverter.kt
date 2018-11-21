package de.unihannover.se.tauben2.model

import androidx.room.TypeConverter
import java.security.Permissions
import kotlin.reflect.KClass

class PermissionConverter{

    companion object {
        @TypeConverter
        fun toInteger(enum : Permission) = enum.ordinal

        @TypeConverter
        fun toPermission(ord : Int) = when (ord ){
            0->Permission.ADMIN
            1->Permission.AUTHORISED
            2->Permission.GUEST
            else->Permission.GUEST
        }
    }




}