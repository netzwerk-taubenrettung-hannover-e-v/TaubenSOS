package de.unihannover.se.tauben2.model.database

import android.os.Parcelable
import de.unihannover.se.tauben2.multiLet
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(val mediaID: Int, val mimeType: String, var toDelete: Boolean = false): Parcelable {

    fun getType() = MimeType.getInstance(mimeType)

    enum class MimeType(val category: String, val type: String) {
        JPEG("image", "jpeg"), PNG("image", "png"), MP4("video", "mp4"), OTHER("", "");

        companion object {
            fun getInstance(mimeType: String): MimeType {
                val splitted = mimeType.split("/")

                multiLet(splitted.firstOrNull(), splitted.lastOrNull()) { category, type ->
                    MimeType.values().forEach {
                        if(it.category == category && it.type == type) return@multiLet it
                    }
                }

                return OTHER
            }
        }

        fun isImage() = category == "image"

        fun isVideo() = category == "video"

    }
}