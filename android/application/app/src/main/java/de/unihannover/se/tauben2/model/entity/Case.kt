package de.unihannover.se.tauben2.model.entity

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

/**
 * represents the case of an injured pigeon
 */
@Entity(tableName = "case"/*,
        foreignKeys = [
            ForeignKey(entity = InjuryEntity::class, parentColumns = ["id"], childColumns = ["injury_id"], onDelete = CASCADE)
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

                var wasFoundDead: Boolean?,
//                var media: List<String>,


                @Embedded var injury: Injury?
)