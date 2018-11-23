package de.unihannover.se.tauben2.model.entity

data class Injury (var fledgling: Boolean,
                   var footOrLeg: Boolean,
                   var headOrEye: Boolean,
                   var openWound: Boolean,
                   var other: Boolean,
                   var paralyzedOrFlightless: Boolean,
                   var wing: Boolean)