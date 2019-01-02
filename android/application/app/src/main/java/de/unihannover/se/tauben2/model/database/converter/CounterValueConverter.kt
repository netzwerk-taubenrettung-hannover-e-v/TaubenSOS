package de.unihannover.se.tauben2.model.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.unihannover.se.tauben2.model.CounterValue

class CounterValueConverter {
    @TypeConverter
    fun fromCounterList(counterValues: List<CounterValue>): String {
        val type = object : TypeToken<List<CounterValue>>() {}.type
        return Gson().toJson(counterValues, type)
    }

    @TypeConverter
    fun toCounterValueList(listString: String): List<CounterValue> {
        val type = object : TypeToken<List<CounterValue>>() {}.type
        return Gson().fromJson(listString, type)
    }
}