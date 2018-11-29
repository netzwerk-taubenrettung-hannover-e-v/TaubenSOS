package de.unihannover.se.tauben2.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.maps.MapView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.id.toolbar_report_button
import de.unihannover.se.tauben2.model.Permission
import de.unihannover.se.tauben2.view.navigation.FragmentChangeListener
import de.unihannover.se.tauben2.view.navigation.FragmentMenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_report01.*



class MainActivity : AppCompatActivity(), FragmentChangeListener {

    private val CALL_NUMBER = "0175 8266832"

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fixing Later Map loading Delay
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

        bottom_navigation.setMenuItems(
                FragmentMenuItem(R.id.newsFragment, "News", R.drawable.ic_today_white_24dp),
                FragmentMenuItem(R.id.counterFragment, "Counter", R.drawable.ic_bubble_chart_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.casesFragment, "Cases", R.drawable.ic_assignment_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.graphsFragment, "Graphs", R.drawable.ic_show_chart_white_24dp, Permission.AUTHORISED),
                FragmentMenuItem(R.id.report00Fragment, "Report a Dove", R.drawable.ic_report_white_24dp),
                FragmentMenuItem(R.id.emergencyCallFragment, "Emergency Call", R.drawable.ic_call_white_24dp),
                FragmentMenuItem(R.id.contactFragment, "Contact", R.drawable.ic_contact_mail_white_24dp),
                FragmentMenuItem(0, "Logout", R.drawable.ic_exit_to_app_white_24dp),
                FragmentMenuItem(R.id.loginFragment, "Login", R.drawable.ic_person_black_24dp),
                FragmentMenuItem(R.id.registerFragment, "Register", R.drawable.ic_person_add_black_24dp)
        )

        NavigationUI.setupWithNavController(bottom_navigation , (nav_host as NavHostFragment).navController)
//
//
//        bottom_navigation.setStartFragmentListener { fragment ->
//            //            when (fragment) {
////                is CasesFragment -> getViewModel(CaseViewModel::class.java).cases.filter { it.isClosed }.observe(this, fragment)
////            }
//            replaceFragment(fragment)
//        }
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
            Navigation.findNavController(this, R.id.nav_host).navigate(R.id.report00Fragment)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun replaceFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTag = fragment.javaClass.name
//
//        val popped = fragmentManager.popBackStackImmediate(fragmentTag, 0)
//
//        val fragmentTransaction = fragmentManager.beginTransaction()
//
//        fragmentTransaction.replace(R.id.main_fragment, fragment, fragmentTag)
//        if (!popped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
//            fragmentTransaction.addToBackStack(fragment.toString())
//        }
//        fragmentTransaction.commit()
//
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }

    // sets the gradient for the status bar
    private fun backgroundColor() {
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

                    Log.i("Permissioon", "Permission has been denied by user")
                } else {
                    Log.i("Permissioon", "Permission has been granted by user")
                }
            }
            2 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("Permissioon", "Permission has been denied by user")
                } else {
                    Log.i("Permissioon", "Permission has been granted by user")
                }
            }
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Error", "Permission to record denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    fun checkSelected() {
        if(report_injury_checkBox_00.isChecked) { goFoward(true) }
        else if(report_injury_checkBox_01.isChecked) { goFoward(true) }
        else if(report_injury_checkBox_02.isChecked) { goFoward(true) }
        else if(report_injury_checkBox_03.isChecked) { goFoward(true) }
        else if(report_injury_checkBox_04.isChecked) { goFoward(true) }
        else if(report_injury_checkBox_05.isChecked) { goFoward(true) }
        else if(report_injury_checkBox_06.isChecked) { goFoward(true) }
        else { goFoward(false) }
    }

    fun goFoward(permission: Boolean) {
        /*if(!permission) {
            //Todo ALertDialog
            report_injury_title.setTextColor(Color.RED)
            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Fehler")

            // Display a message on alert dialog
            builder.setMessage("Bitte füllen Sie alle Pflichfelder aus")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button
                Toast.makeText(applicationContext,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()

                // Change the app background color
                root_layout.setBackgroundColor(Color.RED)
            }
        }*/
    }

    fun openPhone() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Error", "Permission to call denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 2)
        }
        try {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("05751 918602"))
            startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.i("Error", "Something is wrong")
        }
    }

    fun openMail() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Error", "Permission to call denied")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 3)
        }
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:developer@example.com"))
            startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.i("Error", "Something is wrong")
        }
    }

    //below doesnt work
    //var button2= findViewById(R.id.button2) as Button

    fun openFacebook(view: View) {

        try {
            val info = packageManager.getApplicationInfo("com.facebook.katana", 0)
            Log.i("App", "installed")

            //start facebook app
            val uri = Uri.parse("fb://page/141319162866697")
            val facebookIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(facebookIntent)

        } catch (e: PackageManager.NameNotFoundException) {
            Log.i("App", "not installed")

            //start browser
            val uri = Uri.parse("https://www.facebook.com/netzwerk.taubenrettung")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }

    }

}

