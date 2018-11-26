package de.unihannover.se.tauben2.view.input

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax(private val min: Int, private val max: Int): InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val before = dest.toString()
        val input: Int = before.replaceRange(dstart until dend, source).toIntOrNull() ?: return min.toString()
        if (isInRange(min, max, input))
            return null
        return when {
            start + end == 1 -> ""
            input < min -> min.toString()
            input > max -> max.toString()
            else -> "0"
        }
    }

    private fun isInRange(a: Int, b: Int, c: Int) = if (b > a) c in a..b else c in b..a
}