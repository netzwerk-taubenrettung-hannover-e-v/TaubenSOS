package de.unihannover.se.tauben2.view.input

import android.view.ViewGroup
import android.widget.EditText

class InputFilterRequired {
    companion object {
        fun allInputsFilled(viewGroup: ViewGroup): Boolean {
            for (i in 0 until viewGroup.childCount) {
                val curView = viewGroup.getChildAt(i)
                if (curView is EditText && curView.text.isBlank()) {
                    return false
                }
            }
            return true
        }
    }
}