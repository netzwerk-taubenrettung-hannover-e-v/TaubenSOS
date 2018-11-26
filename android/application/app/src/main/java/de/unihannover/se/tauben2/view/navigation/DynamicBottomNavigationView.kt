package de.unihannover.se.tauben2.view.navigation

import android.content.Context
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.unihannover.se.tauben2.App
import de.unihannover.se.tauben2.R

class DynamicBottomNavigationView(context: Context, attrs: AttributeSet?): BottomNavigationView(context, attrs) {

    private var mRootView: View = View.inflate(context, R.layout.dynamic_bottom_navigation_view, this)

    private var mStartFragmentListener: StartFragmentListener? = null

    private var mSize: Int = 5

    private var mMoreMenuItem: FragmentMenuItem? = null

    private val MORE_MENU_ITEM = 99

    private var menuSize: Int = menu.size()

    constructor(context: Context): this(context, null)

    init {
        if(attrs != null) {

            val tarr = context.obtainStyledAttributes(attrs, R.styleable.DynamicBottomNavigationView)

            mSize = tarr.getInteger(R.styleable.DynamicBottomNavigationView_size, 5)

            tarr.recycle()
        }

//        if (hasOverflowMenu()) {
//            val overflowItems = mutableListOf<MenuItem>()
//
//            for(i in mSize-1 until menu.size()) {
//                val item = menu.getItem(i)
//                overflowItems.add(item)
//                menu.removeItem(item.itemId)
//            }
//
//            val moreItem = menu.add("More")
//            moreItem.setIcon(R.drawable.ic_more_horiz_white_24dp)
//
//            val moreMenu = MoreFragment.newInstance().more_navigation.menu
//            moreMenu.clear()
//            overflowItems.forEach { moreMenu.add(it.itemId) }
//
//        }
    }

    fun setMenuItems(vararg items: FragmentMenuItem) {
        menu.clear()
        val permissibleItems = items.filter { it.hasPermission(App.CURRENT_PERMISSION) }

        permissibleItems.slice(0 until mSize-1).forEach {
            if(it.hasPermission(App.CURRENT_PERMISSION)){
                addMenuItem(it)
            }
        }
        if(permissibleItems.size > mSize) {
            createOverflowMenu(permissibleItems.slice(mSize-1 until permissibleItems.size))
        }
        setOnNavigationItemSelectedListener {
            if(mMoreMenuItem?.itemId == it.itemId) {
                mMoreMenuItem?.let { moreItem ->
                    mStartFragmentListener?.onStartFragment(moreItem.getFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            for(i in 0 until permissibleItems.size) {
                val item = permissibleItems[i]
                if (item.itemId == it.itemId) {
                    mStartFragmentListener?.onStartFragment(item.getFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    private fun createOverflowMenu(items: List<FragmentMenuItem>) {
        mMoreMenuItem = FragmentMenuItem(MORE_MENU_ITEM, "More", R.drawable.ic_more_horiz_white_24dp) { MoreFragment.newInstance(items) }
        mMoreMenuItem?.let { addMenuItem(it) }
    }

    private fun addMenuItem(item: FragmentMenuItem) {
        menu.add(Menu.NONE, item.itemId, Menu.NONE, item.title).setIcon(item.iconId)
    }

    fun hasOverflowMenu() = menuSize > mSize

    fun setStartFragmentListener(listener: (fragment: Fragment) -> Unit){
        mStartFragmentListener = object : StartFragmentListener {
            override fun onStartFragment(fragment: Fragment) {
                listener(fragment)
            }
        }
    }

    fun clearStartFragmentListener(){
        mStartFragmentListener = null
    }

    interface StartFragmentListener {
        fun onStartFragment(fragment: Fragment)
    }
}
