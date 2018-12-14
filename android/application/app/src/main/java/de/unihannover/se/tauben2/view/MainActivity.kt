package de.unihannover.se.tauben2.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.MapView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.id.toolbar_report_button
import de.unihannover.se.tauben2.databinding.ActivityMainBinding
import de.unihannover.se.tauben2.model.Permission
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.navigation.FragmentMenuItem
import de.unihannover.se.tauben2.view.report.ReportActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // For navigation //
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mBottomNavigator: BottomNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Fixing later map loading delay
        Thread {
            try {
                val mv = MapView(applicationContext)
                mv.onCreate(null)
                mv.onPause()
                mv.onDestroy()
            } catch (ignored: Exception) {}
        }.start()

        backgroundColor()

        setupPermissions()

        // Toolbar Settings
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        initBottomNavigation()
    }

    private fun initBottomNavigation() {

        binding.bottomNavigation.setMenuItems(
                FragmentMenuItem(R.id.newsFragment, getString(R.string.news), R.drawable.ic_today_white_24dp),
                FragmentMenuItem(R.id.counterFragment, getString(R.string.counter), R.drawable.ic_bubble_chart_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.casesFragment, getString(R.string.cases), R.drawable.ic_assignment_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.graphsFragment, getString(R.string.graphs), R.drawable.ic_show_chart_white_24dp, Permission.AUTHORISED),
                //FragmentMenuItem(R.id.report00Fragment, getString(R.string.report_pigeon), R.drawable.ic_report_white_24dp),
                FragmentMenuItem(R.id.membersFragment, getString(R.string.users), R.drawable.ic_group_white_24dp, Permission.ADMIN),
                //FragmentMenuItem(R.id.emergencyCallFragment, "Emergency Call", R.drawable.ic_call_white_24dp),
                FragmentMenuItem(R.id.contactFragment, getString(R.string.contact), R.drawable.ic_contact_mail_white_24dp),
                FragmentMenuItem(0, getString(R.string.logout), R.drawable.ic_exit_to_app_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.loginFragment, getString(R.string.login), R.drawable.ic_person_black_24dp),
                FragmentMenuItem(R.id.registerFragment, getString(R.string.register), R.drawable.ic_person_add_black_24dp)
        )

        val navController = (nav_host as NavHostFragment).navController
        mNavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        mBottomNavigator = BottomNavigator(this, mNavHostFragment.childFragmentManager, R.id.nav_host, binding.bottomNavigation)

        navController.navigatorProvider.addNavigator(mBottomNavigator)

        navController.setGraph(R.navigation.main_navigation)

        binding.bottomNavigation.setupWithNavController(navController)

//        binding.bottomNavigation.setOnNavigationItemSelectedListener {
//            NavigationUI.onNavDestinationSelected(it, navController)
//            true
//        }
    }

    override fun onBackPressed() {
//        if(binding.bottomNavigation.isCurrentTabMore())
//            super.onBackPressed()
//        else
            mBottomNavigator.onBackPressed()
    }

    // Add "Report a Dove"-Btn to the Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Toolbar Event Listener
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == toolbar_report_button) {

            //Navigation.findNavController(findViewById(R.id.content)).navigate(R.id.reportActivity)
            //Navigation.findNavController(this, R.id.nav_host).navigate(R.id.report00Fragment)
            // binding.bottomNavigation.selectMoreTab()
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    // sets the gradient for the status bar
    private fun backgroundColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawableResource(R.drawable.gradient)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(getString(R.string.permission), getString(R.string.permission_denied))
                } else {
                    Log.i(getString(R.string.permission), getString(R.string.permission_granted))
                }
            }
            2 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(getString(R.string.permission), getString(R.string.permission_denied))
                } else {
                    Log.i(getString(R.string.permission), getString(R.string.permission_granted))
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(getString(R.string.error), getString(R.string.record_permission_denied))
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }
}

