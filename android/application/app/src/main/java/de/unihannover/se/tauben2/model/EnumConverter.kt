package de.unihannover.se.tauben2.model

import android.arch.persistence.room.TypeConverter
import java.security.Permissions
import kotlin.reflect.KClass

class EnumConverter{

    companion object {
        @TypeConverter
        fun toInteger(enum :Permission) = enum.ordinal
    }




}