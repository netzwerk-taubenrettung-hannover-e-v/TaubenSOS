package de.unihannover.se.tauben2.model

import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler

class PicassoVideoRequestHandler: RequestHandler() {

    companion object {
        const val SCHEME_VIDEO = "video"
    }

    override fun canHandleRequest(data: Request?) = data?.uri?.scheme == SCHEME_VIDEO

    override fun load(request: Request?, networkPolicy: Int): Result? {
        return request?.let { req ->
            val bm = ThumbnailUtils.createVideoThumbnail(req.uri.path, MediaStore.Images.Thumbnails.MINI_KIND)
            Result(bm, Picasso.LoadedFrom.DISK)
        }
    }
}