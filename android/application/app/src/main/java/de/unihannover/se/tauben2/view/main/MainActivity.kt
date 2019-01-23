package de.unihannover.se.tauben2.view.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.MapView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.id.toolbar_report_button
import de.unihannover.se.tauben2.R.id.zoom
import de.unihannover.se.tauben2.databinding.ActivityMainBinding
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.view.main.fragments.cases.CaseInfoFragment
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.navigation.FragmentMenuItem
import de.unihannover.se.tauben2.view.report.ReportActivity
import kotlinx.android.synthetic.main.activity_main.*
import de.unihannover.se.tauben2.view.main.fragments.cases.CasesFragment


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    // For navigation //
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mBottomNavigator: BottomNavigator
    private var mNavigateTo: Int? = null

    var zoomMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, de.unihannover.se.tauben2.R.layout.activity_main)

        // Fixing later map loading delay
        Thread {
            try {
                val mv = MapView(applicationContext)
                mv.onCreate(null)
                mv.onPause()
                mv.onDestroy()
            } catch (ignored: Exception) {
            }
        }.start()

        backgroundColor()

        // Toolbar Settings
        setSupportActionBar(toolbar as Toolbar)
        //supportActionBar?.setDisplayShowTitleEnabled(false)

        initBottomNavigation()
    }

    override fun onPause() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        super.onPause()
    }

    private fun initBottomNavigation() {

        if (BootingActivity.getOwnerPermission() == Permission.GUEST)
            mBinding.bottomNavigation.setSize(4)

        mBinding.bottomNavigation.setMenuItems(BootingActivity.getOwnerPermission(),
                FragmentMenuItem(R.id.newsFragment, resources.getQuantityString(R.plurals.news, 2), R.drawable.ic_today),
                FragmentMenuItem(R.id.casesUserFragment, getString(R.string.cases), R.drawable.ic_assignment, onlyThatPermission = true),
                FragmentMenuItem(R.id.counterFragment, getString(R.string.counter), R.drawable.ic_bubble_chart_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.casesFragment, getString(R.string.cases), R.drawable.ic_assignment, Permission.AUTHORISED),
                FragmentMenuItem(R.id.statisticFragment, getString(R.string.graphs), R.drawable.ic_chart, Permission.AUTHORISED),
                FragmentMenuItem(R.id.membersFragment, getString(R.string.users), R.drawable.ic_group_white_24dp, Permission.ADMIN),
                FragmentMenuItem(R.id.contactFragment, getString(R.string.contact), R.drawable.ic_contact_mail_white_24dp),
                FragmentMenuItem(R.id.button_logout, getString(R.string.logout), R.drawable.ic_exit_to_app_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.loginFragment, getString(R.string.login), R.drawable.ic_person_black_24dp, onlyThatPermission = true),
                FragmentMenuItem(R.id.registerFragment, getString(R.string.register), R.drawable.ic_person_add_black_24dp, onlyThatPermission = true),
                FragmentMenuItem(R.id.imprintFragment, getString(R.string.imprint_title), R.drawable.ic_building),
                FragmentMenuItem(R.id.privacyFragment, getString(R.string.privacy_title), R.drawable.ic_security)
        )

        val navController = (nav_host as NavHostFragment).navController
        mNavHostFragment = supportFragmentManager.findFragmentById(de.unihannover.se.tauben2.R.id.nav_host) as NavHostFragment
        mBottomNavigator = BottomNavigator(this, mNavHostFragment.childFragmentManager, de.unihannover.se.tauben2.R.id.nav_host, mBinding.bottomNavigation)

        navController.navigatorProvider.addNavigator(mBottomNavigator)

        navController.setGraph(de.unihannover.se.tauben2.R.navigation.main_navigation)

        mBinding.bottomNavigation.setupWithNavController(navController)

    }

    override fun onStart() {
        super.onStart()
        mNavigateTo?.let {
            Navigation.findNavController(this, de.unihannover.se.tauben2.R.id.nav_host).navigate(it)
            mNavigateTo = null
        }
    }

    override fun onBackPressed() {
        val index = mNavHostFragment.childFragmentManager.fragments.size - 1
        val f = mNavHostFragment.childFragmentManager.fragments[index]

        when (f) {
            is CaseInfoFragment -> if (zoomMode) touchView(f.zoomOut())
            is CasesFragment -> if (zoomMode) touchView(f.zoomOut())
            else -> zoomMode = false
        }

        if(!zoomMode) mBottomNavigator.onBackPressed()
    }

    private fun touchView(view : View) {
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_DOWN, 1f, 1f, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_UP, 1f, 1f, 0));
    }

    // Add "Report a Dove"-Btn to the Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Toolbar Event Listener
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == toolbar_report_button) {

            val intent = Intent(this, ReportActivity::class.java)
            startActivityForResult(intent, 0)
        }

        if (item?.itemId == android.R.id.home) onBackPressed()

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            mNavigateTo = if (BootingActivity.getOwnerPermission() == Permission.GUEST)
                de.unihannover.se.tauben2.R.id.casesUserFragment
            else
                de.unihannover.se.tauben2.R.id.casesFragment
        }
    }

    // sets the gradient for the status bar
    private fun backgroundColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawableResource(de.unihannover.se.tauben2.R.drawable.gradient)
    }

    fun enableBackButton () {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun disableBackButton () {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

}

