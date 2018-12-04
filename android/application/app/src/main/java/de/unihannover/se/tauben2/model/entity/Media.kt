package de.unihannover.se.tauben2.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = Case::class, parentColumns = ["caseID"], childColumns = ["caseID"]) ])
data class Media(@PrimaryKey var url: String,
                 var caseID: Int? )