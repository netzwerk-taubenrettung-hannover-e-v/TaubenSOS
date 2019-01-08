package de.unihannover.se.tauben2.model.database.converter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(val mediaID: Int, val mimeType: String): Parcelable