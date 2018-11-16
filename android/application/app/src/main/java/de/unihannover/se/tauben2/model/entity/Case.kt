package de.unihannover.se.tauben2.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * represents the case of an injured pigeon
 */
@Entity(tableName = "case", foreignKeys = [ForeignKey(
        entity = Injury::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("injury_id"))])
data class Case(@PrimaryKey(autoGenerate = true) val id: Int,
                var coordinates: String, // TODO Coordinate class and converter?
                var status: String, // TODO Status Enum + converter
                var priority: Int, // TODO Priority Enum
                var picture1Path: String?,
                var picture2Path: String?,
                var picture3Path: String?,
                var weddingPigeon: Boolean,
                var carrierPigeon: Boolean,
                @ColumnInfo(name = "injury_id") var injury: String
)