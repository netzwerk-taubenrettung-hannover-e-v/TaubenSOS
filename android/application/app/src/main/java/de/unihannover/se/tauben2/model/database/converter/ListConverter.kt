package de.unihannover.se.tauben2.model.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {

    @TypeConverter
    fun fromStringToStringList(value: String): List<String> = fromString(value)

    @TypeConverter
    fun fromStringListToString(list: List<String>) = fromArrayList(list)

    @TypeConverter
    fun fromStringToMediaList(value: String): List<Media> = fromString(value)

    @TypeConverter
    fun fromMediaListToString(list: List<Media>) = fromArrayList(list)

    fun <T> fromString(value: String): List<T> {
        val listType = object : TypeToken<ArrayList<T>>() {}.type
        return Gson().fromJson(value, listType)
    }

    fun <T> fromArrayList(list: List<T>): String {
        return Gson().toJson(list)
    }
}