package de.unihannover.se.tauben2

import android.annotation.SuppressLint
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView

@SuppressLint("RestrictedApi")
fun BottomNavigationView.disableShiftMode(){
    val menuView = this.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView

            item.setShiftingMode(false)
            // set once again checked value, so view will be updated

            item.setChecked(item.itemData.isChecked)
        }
    } catch (e: NoSuchFieldException) {
        android.util.Log.e("BottomNavigationView", "Unable to get shift mode field", e)
    } catch (e: IllegalAccessException) {
        android.util.Log.e("BottomNavigationView", "Unable to change value of shift mode", e)
    }
}