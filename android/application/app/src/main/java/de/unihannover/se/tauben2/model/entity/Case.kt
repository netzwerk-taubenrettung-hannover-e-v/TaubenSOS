package de.unihannover.se.tauben2.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * represents the case of an injured pigeon
 */
@Entity(tableName = "case"/*,
        foreignKeys = [
            ForeignKey(entity = Injury::class, parentColumns = ["id"], childColumns = ["injury_id"])
        ]*/)
data class Case(@PrimaryKey var caseID: Int,
                var additionalInfo: String?,

                var isClosed: Boolean,
                var isWeddingPigeon: Boolean,
                var isCarrierPigeon: Boolean,

                var latitude: Double,
                var longitude: Double,

                var rescuer: String?,
                var priority: Int,
                var timestamp: Long,
                var phone: String,
                var wasFoundDead: Boolean
//                var media: List<String>,

//                @ColumnInfo(name = "injury_id") var injury: Int
)