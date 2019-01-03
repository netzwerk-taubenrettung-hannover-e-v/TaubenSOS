package de.unihannover.se.tauben2.model

import de.unihannover.se.tauben2.model.database.entity.DatabaseEntity

data class CounterValue(var pigeonCount: Int,
                        var populationMarkerID: Int,
                        var timestamp: Long) : DatabaseEntity() {
    override val refreshCooldown: Long
        get() = 1000 * 60 * 60 * 3 // 3 h

}