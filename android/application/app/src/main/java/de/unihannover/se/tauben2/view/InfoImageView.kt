package de.unihannover.se.tauben2.view

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getDpValue
import kotlinx.android.synthetic.main.info_image_view.view.*

class InfoImageView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attrs, defStyleAttr, defStyleAttr) {

    private var mRootView: View = View.inflate(context, R.layout.info_image_view, this)

    private var mClosable: Boolean = false

    private var mPlayable: Boolean = false

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    init {
        if(attrs != null) {

            val tarr = context.obtainStyledAttributes(attrs, R.styleable.InfoImageView)

            setClosable(tarr.getBoolean(R.styleable.InfoImageView_closable, mClosable))
            setPlayable(tarr.getBoolean(R.styleable.InfoImageView_playable, mPlayable))

            tarr.recycle()
        }
    }

    fun setClosable(closable: Boolean) {
        mClosable = closable
        if(mClosable)
            btn_close.visibility = View.VISIBLE
        else
            btn_close.visibility = View.GONE
    }

    fun setPlayable(playable: Boolean) {
        mPlayable = playable
        if(mPlayable)
            image_play.visibility = View.VISIBLE
        else {
            image_play.visibility = View.GONE
        }
    }

    fun getImage(): SquareImageView = image_media
}