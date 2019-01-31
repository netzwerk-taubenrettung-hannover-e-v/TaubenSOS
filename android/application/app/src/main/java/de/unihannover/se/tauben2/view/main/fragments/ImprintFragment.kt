package de.unihannover.se.tauben2.view.main.fragments

import android.text.Html
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.TextPage

class ImprintFragment: TextPageFragment(R.string.imprint_title) {

    override fun getTextPage(): TextPage {
        return context?.let { cxt ->
            TextPage(cxt.getString(R.string.imprint_title), cxt.getString(R.string.imprint))
        } ?: TextPage("", "")
    }

}