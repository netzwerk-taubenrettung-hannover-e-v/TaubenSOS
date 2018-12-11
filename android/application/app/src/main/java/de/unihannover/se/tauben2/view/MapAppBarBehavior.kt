package de.unihannover.se.tauben2.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.appbar.AppBarLayout

class MapAppBarBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {

    init {
        setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {

                if ((appBarLayout.height - appBarLayout.bottom) == 0) return false
                return true
            }
        })
    }
}