package de.unihannover.se.tauben2.model.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.unihannover.se.tauben2.model.database.Media

class ListConverter {
//
//    @TypeConverter
//    fun fromStringToStringList(value: String): List<String> = fromString(value)
//
//    @TypeConverter
//    fun fromStringListToString(list: List<String>) = fromList(list)

    @TypeConverter
    fun fromStringToMediaList(value: String): List<Media> {
        val listType = object : TypeToken<ArrayList<Media>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromMediaListToString(list: List<Media>) = Gson().toJson(list)

    fun <T> fromString(value: String): List<T> {
        val listType = object : TypeToken<List<T>>() {}.type
        return Gson().fromJson(value, listType)
    }

    fun <T> fromList(list: List<T>): String {
        return Gson().toJson(list)
    }
}