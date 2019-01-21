package de.unihannover.se.tauben2.view.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.text.method.Touch
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.MapView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.id.toolbar_report_button
import de.unihannover.se.tauben2.databinding.ActivityMainBinding
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.view.main.fragments.cases.CaseInfoFragment
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.navigation.FragmentMenuItem
import de.unihannover.se.tauben2.view.report.ReportActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.main.fragments.cases.CasesFragment
import de.unihannover.se.tauben2.view.main.fragments.cases.CasesAdminFragment
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment


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

        setupPermissions()

        // Toolbar Settings
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
                FragmentMenuItem(R.id.newsFragment, getString(R.string.events), R.drawable.ic_today_white_24dp),
                FragmentMenuItem(R.id.casesUserFragment, getString(R.string.cases), R.drawable.ic_assignment_white_24dp, onlyThatPermission = true),
                FragmentMenuItem(R.id.counterFragment, getString(R.string.counter), R.drawable.ic_bubble_chart_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.casesFragment, getString(R.string.cases), R.drawable.ic_assignment_white_24dp, Permission.AUTHORISED),
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
        if(!zoomMode) mBottomNavigator.onBackPressed()
        else {
            val index = mNavHostFragment.childFragmentManager.fragments.size - 1
            var f = mNavHostFragment.childFragmentManager.fragments[index]
            if (f is CaseInfoFragment) {
                TouchView(f.zoomOut())
            }
            if (f is CasesFragment) {
                TouchView(f.zoomOut())
            }
        }
    }

    fun TouchView(view : View) {
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_DOWN, 1f, 1f, 0));
        view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_UP, 1f, 1f, 0));
    }

    // Add "Report a Dove"-Btn to the Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(de.unihannover.se.tauben2.R.menu.toolbar_menu, menu)
        return true
    }

    // Toolbar Event Listener
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == toolbar_report_button) {

            val intent = Intent(this, ReportActivity::class.java)
            startActivityForResult(intent, 0)
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(getString(de.unihannover.se.tauben2.R.string.permission), getString(de.unihannover.se.tauben2.R.string.permission_denied))
                } else {
                    Log.i(getString(de.unihannover.se.tauben2.R.string.permission), getString(de.unihannover.se.tauben2.R.string.permission_granted))
                }
            }
            2 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(getString(de.unihannover.se.tauben2.R.string.permission), getString(de.unihannover.se.tauben2.R.string.permission_denied))
                } else {
                    Log.i(getString(de.unihannover.se.tauben2.R.string.permission), getString(de.unihannover.se.tauben2.R.string.permission_granted))
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }
}

