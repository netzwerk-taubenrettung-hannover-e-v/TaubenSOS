package de.unihannover.se.tauben2.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.model.Permission
import de.unihannover.se.tauben2.view.navigation.FragmentChangeListener
import de.unihannover.se.tauben2.view.navigation.FragmentMenuItem
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity(), FragmentChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        backGroundColor()
        setContentView(R.layout.activity_main)

        setupPermissions()

        // Toolbar Settings
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        bottom_navigation.setMenuItems(
                FragmentMenuItem(0, "News", R.drawable.ic_today_white_24dp) { NewsFragment.newInstance() },
                FragmentMenuItem(1, "Counter", R.drawable.ic_bubble_chart_white_24dp, Permission.AUTHORISED) {CounterFragment.newInstance() },
                FragmentMenuItem(2, "Cases", R.drawable.ic_assignment_white_24dp, Permission.AUTHORISED) { CasesFragment.newInstance() },
                FragmentMenuItem(3, "Graphs", R.drawable.ic_show_chart_white_24dp, Permission.AUTHORISED) {GraphsFragment.newInstance() },
                FragmentMenuItem(4, "Report a Dove", R.drawable.ic_report_white_24dp) { ReportFragment.newInstance() },
                FragmentMenuItem(5, "Emergency Call", R.drawable.ic_call_white_24dp) { EmergencyCallFragment.newInstance() },
                FragmentMenuItem(6, "Contact", R.drawable.ic_contact_mail_white_24dp) { ContactFragment.newInstance()},
                FragmentMenuItem(7, "Logout", R.drawable.ic_exit_to_app_white_24dp) { NewsFragment.newInstance() }
        )

        bottom_navigation.setStartFragmentListener { fragment ->
//            when (fragment) {
//                is CasesFragment -> getViewModel(CaseViewModel::class.java).cases.filter { it.isClosed }.observe(this, fragment)
//            }
            replaceFragment(fragment)
        }
    }

    // Add "Report a Dove"-Btn to the Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun replaceFragment(fragment:Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment, fragment.toString())
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }

    // sets the gradient for the status bar
    fun backGroundColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        // looks weird!
        // window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawableResource(R.drawable.gradient)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permissioon2", "Permission has been denied by user")
                } else {
                    Log.i("Permissioon2", "Permission has been granted by user")
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permissioon2", "Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    //below doesnt work
    //var button2= findViewById(R.id.button2) as Button

    fun openFacebook(){
        val uri = Uri.parse("http://www.google.com")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }


}

