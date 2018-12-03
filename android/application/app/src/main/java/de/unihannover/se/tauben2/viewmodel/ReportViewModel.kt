package de.unihannover.se.tauben2.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class ReportViewModel : ViewModel() {

    private lateinit var position : LatLng
    private lateinit var injuries : BooleanArray
    private var priority : Int = 0
    private var carrierPigeon = false
    private var weddingPigeon = false
    private lateinit var additionalInformations : String

    fun getPosition (): LatLng {
        return position
    }

    fun setPosition (pos : LatLng) {
        position = pos
    }

    fun getInjuryAt (i : Int) : Boolean {
        return injuries[i]
    }

    fun setInjuries (injuries : BooleanArray) {
        this.injuries = injuries
    }

    fun getPriority () : Int {
        return priority
    }

    fun setPriority (prio : Int) {
        priority = prio
    }

    fun isCarrierPigeon () : Boolean {
        return carrierPigeon
    }

    fun setCarrierPigeon (p0 : Boolean) {
        carrierPigeon = p0
    }

    fun isWeddingPigeon () : Boolean {
        return weddingPigeon
    }

    fun setWeddingPigeon (p0 : Boolean) {
        weddingPigeon = p0
    }

    fun getAdditionalInformations () : String {
        return additionalInformations
    }

    fun setAdditionalInformations (info : String) {
        additionalInformations = info
    }

}