package de.unihannover.se.tauben2.model.database.entity

import android.content.Context
import de.unihannover.se.tauben2.App

abstract class DatabaseEntity {

    // in milliseconds
    var lastUpdated = -1L

    abstract val refreshCooldown: Long

    fun shouldFetch() = System.currentTimeMillis() - lastUpdated > refreshCooldown

    interface AllUpdatable {

        // in milliseconds
        val refreshAllCooldown: Long

        fun getLastAllUpdated() = App.context.getSharedPreferences("tauben2", Context.MODE_PRIVATE).getLong("${javaClass.name}_last_update", -1L)

        fun setLastAllUpdated(time: Long) {
            App.context.getSharedPreferences("tauben2", Context.MODE_PRIVATE).edit().putLong("${javaClass.name}_last_update", time).apply()
        }

        fun setLastAllUpdatedToNow() {
            setLastAllUpdated(System.currentTimeMillis())
        }

        fun shouldFetch() = System.currentTimeMillis() - getLastAllUpdated() > refreshAllCooldown
    }
}