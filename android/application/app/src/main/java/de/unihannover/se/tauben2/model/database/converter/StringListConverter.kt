package de.unihannover.se.tauben2.model.database.converter

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {

    @TypeConverter
    fun fromString(value: String): List<String> {
        Log.i("Tauben2", "Convert $value to List<String>")
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<String>): String {
        Log.i("Tauben2", "Convert $list to String")
        return Gson().toJson(list)
    }
}