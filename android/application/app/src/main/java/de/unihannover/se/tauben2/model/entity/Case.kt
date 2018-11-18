package de.unihannover.se.tauben2.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * represents the case of an injured pigeon
 */
@Entity(tableName = "case",
        foreignKeys = [
            ForeignKey(entity = Injury::class, parentColumns = ["id"], childColumns = ["injury_id"])
        ])
data class Case(@PrimaryKey(autoGenerate = true) val id: Int,
                var coordinates: String, // TODO Coordinate class and converter?
                var status: String, // TODO Status Enum + converter
                var priority: Int, // TODO Priority Enum
                var picture1Path: String?,
                var picture2Path: String?,
                var picture3Path: String?,
                var weddingPigeon: Boolean,
                var carrierPigeon: Boolean,
                @ColumnInfo(name = "injury_id") var injury: Int
)