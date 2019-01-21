package de.unihannover.se.tauben2.view.list

import android.content.Intent

data class ContactItem(val description: String, val buttonLabel: String, val buttonIconId: Int,
                       val buttonId: Int, val intent: Intent, val infoButton: Int?)