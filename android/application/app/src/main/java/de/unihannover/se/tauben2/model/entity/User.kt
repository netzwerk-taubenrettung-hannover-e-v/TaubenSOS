package de.unihannover.se.tauben2.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.unihannover.se.tauben2.model.Permission

/**
 * Entity for saving user id's and their permission level for accessing the app's content
 */
@Entity(tableName = "user")
data class User(@PrimaryKey val id: Int, var permission: Permission)