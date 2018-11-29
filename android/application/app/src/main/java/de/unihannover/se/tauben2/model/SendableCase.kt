package de.unihannover.se.tauben2.model

/**
 * class for case objects that only has the attributes needed for creating case requests to the
 * server
 */
data class SendableCase(var additionalInfo: String,

                        var isCarrierPigeon: Boolean,
                        var isWeddingPigeon: Boolean,

                        var latitude: Double,
                        var longitude: Double,

                        var phone: String,
                        var priority: Int,
                        var timestamp: Long,

                        var injury: Injury)