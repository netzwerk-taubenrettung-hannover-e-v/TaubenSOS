package de.unihannover.se.tauben2.model

import androidx.room.TypeConverter


class StringListConverter {
    companion object {
        /**
         * Converts a list of Strings into a String object so it can be stored in the database
         * @param list List of Strings to be converted
         */
        @TypeConverter
        @JvmStatic
        fun fromStringList(list: List<String>): String {
            return list.toString().trim('[', ']')
        }

        /**
         * Converts a String object representing a list of strings from the database to an actual
         * list of strings
         * @param str String to be converted into List
         */
        @TypeConverter
        @JvmStatic
        fun fromString(str: String): List<String> {
            return str.split(", ")
        }
    }
}