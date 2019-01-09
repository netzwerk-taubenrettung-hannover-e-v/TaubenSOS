package de.unihannover.se.tauben2.view.main.fragments

import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.TextPage

class PrivacyFragment: TextPageFragment() {
    override fun getTextPage(): TextPage {
        return context?.let { cxt ->
            TextPage(cxt.getString(R.string.privacy_title), cxt.getString(R.string.lorem_ipsum))
        } ?: TextPage("", "")
    }
}