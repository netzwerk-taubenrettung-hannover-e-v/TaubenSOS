package de.unihannover.se.tauben2.viewmodel

import androidx.lifecycle.ViewModel
import de.unihannover.se.tauben2.model.Injury
import de.unihannover.se.tauben2.model.SendableCase

class ReportSharedViewModel : ViewModel() {
    var editedCase: SendableCase = SendableCase("", false,
            false, 0.0, 0.0, "", 1, 0,
            Injury(false, false, false, false, false,
                    false, false))
}