package de.unihannover.se.tauben2.view.navigation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.database.Permission
import java.util.*

class DynamicBottomNavigationView(context: Context, attrs: AttributeSet?, defStyleAttr: Int): BottomNavigationView(context, attrs, defStyleAttr) {


    private var mRootView: View = View.inflate(context, R.layout.dynamic_bottom_navigation_view, this)

    private var mSize: Int = 5

    private val MORE_MENU_ITEM = R.id.moreFragment

    private var menuSize: Int = menu.size()

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    init {
        if(attrs != null) {

            val tarr = context.obtainStyledAttributes(attrs, R.styleable.DynamicBottomNavigationView)

            setSize(tarr.getInteger(R.styleable.DynamicBottomNavigationView_size, mSize))

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

    fun setMenuItems(permission: Permission, vararg items: FragmentMenuItem) {
        menu.clear()
        val permissibleItems = items.filter { it.hasPermission(permission) }

        permissibleItems.slice(0 until mSize-1).forEach {
            if(it.hasPermission(permission)){
                addMenuItem(item = it)
            }
        }
        if(permissibleItems.size > mSize-1) {
            createOverflowMenu(permissibleItems.slice(mSize-1 until permissibleItems.size))
        }
//        setOnNavigationItemSelectedListener {
//            if(mMoreMenuItem?.itemId == it.itemId) {
//                mMoreMenuItem?.let { moreItem ->
//                    mStartFragmentListener?.onStartFragment(moreItem.getFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//            }
//            for(i in 0 until permissibleItems.size) {
//                val item = permissibleItems[i]
//                if (item.itemId == it.itemId) {
//                    mStartFragmentListener?.onStartFragment(item.getFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
//            }
//            return@setOnNavigationItemSelectedListener false
//        }
    }

    fun setSelectedItem(id: Int) {
        menu.findItem(id)?.let {
            menu.findItem(id).isChecked = true
//            selectedItemId = it.itemId
        }
    }

    fun selectMoreTab(){
        setSelectedItem(MORE_MENU_ITEM)
    }

    fun isCurrentTabMore() = menu.findItem(MORE_MENU_ITEM).isChecked

    private val moreFragmentBundle = Bundle()

    private fun createOverflowMenu(items: List<FragmentMenuItem>) {
        addMenuItem(item = FragmentMenuItem(MORE_MENU_ITEM, context?.getString(R.string.more) ?: "More", R.drawable.ic_more_horiz_white_24dp))

        moreFragmentBundle.putParcelableArrayList("items", ArrayList(items))
        Navigation.findNavController(context as Activity, R.id.nav_host).addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == MORE_MENU_ITEM)
                destination.addDefaultArguments(moreFragmentBundle)
        }
    }

    private fun addMenuItem(menu: Menu = getMenu(), item: FragmentMenuItem) {
        menu.add(Menu.NONE, item.itemId, Menu.NONE, item.title).setIcon(item.iconId)
    }

    fun hasOverflowMenu() = menuSize > mSize

    fun getSize() = mSize

    fun setSize(size: Int) {
        mSize = size
    }
}
