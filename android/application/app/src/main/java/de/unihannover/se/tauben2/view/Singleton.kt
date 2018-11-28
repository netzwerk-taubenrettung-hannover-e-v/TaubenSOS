package de.unihannover.se.tauben2.view

import android.os.Bundle

abstract class Singleton<T> {

    private var inst: T? = null

    protected var bundle: Bundle? = null

    fun getInstance(bundle: Bundle? = null): T {
        return inst ?: saveAndGetInstance(bundle)
    }

    private fun saveAndGetInstance(bundle: Bundle? = null): T {
        if(inst == null)
            this.inst = newInstance()
        this.bundle = bundle
        return inst!!
    }

    protected abstract fun newInstance(): T

    fun removeInstance() {
        inst = null
    }
}