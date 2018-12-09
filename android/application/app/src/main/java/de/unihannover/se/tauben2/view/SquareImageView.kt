package de.unihannover.se.tauben2.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : ImageView(context, attrs, defStyleAttr, defStyleRes){
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) = super.onMeasure(widthMeasureSpec, widthMeasureSpec)
}