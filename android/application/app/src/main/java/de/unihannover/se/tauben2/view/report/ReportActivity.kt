package de.unihannover.se.tauben2.view.report

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.ActivityMainBinding
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import kotlinx.android.synthetic.main.activity_report.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.MapView
import de.unihannover.se.tauben2.model.database.entity.Case
import kotlinx.android.synthetic.main.toolbar_report.*
import kotlinx.android.synthetic.main.toolbar_report.view.*

class ReportActivity : FragmentActivity() {

    // To get rid of this shit, I need the navigation order
    private var currentPosition = 0

    private lateinit var binding: ActivityMainBinding
    var case : Case? = null

    // For navigation //
    private lateinit var mNavHostFragment: NavHostFragment
    private lateinit var mBottomNavigator: BottomNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        case = intent.getParcelableExtra("case")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(R.layout.activity_report)

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
        initNavigation()

        // exit activity on toolbar button
        toolbar.exit_btn.setOnClickListener {
            finish()
        }

        createStepIndicator()
        if (case != null) editCase()
    }

    // sets the gradient for the status bar
    private fun backgroundColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawableResource(R.drawable.gradient)
    }

    private fun initNavigation () {

        val navController = (report_nav_host as NavHostFragment).navController
        mNavHostFragment = supportFragmentManager.findFragmentById(R.id.report_nav_host) as NavHostFragment
        mBottomNavigator = BottomNavigator(this, mNavHostFragment.childFragmentManager, R.id.report_nav_host, binding.bottomNavigation)

        navController.navigatorProvider.addNavigator(mBottomNavigator)
        navController.setGraph(R.navigation.report_navigation)
    }

    override fun onBackPressed() {
        prev_btn.callOnClick()
    }

    fun onFragmentChange () {
        val step = currentPosition + 1
        val label = mNavHostFragment.navController.currentDestination?.label
        step_indicator.text  = baseContext.getString(R.string.step, step, label)
    }

    private fun editCase () {
        toolbar_title.text = baseContext.getString(R.string.edit_case, case?.caseID)
    }

    // STEP INDICATOR
    private fun createStepIndicator () {

        for (i in 0 until mNavHostFragment.navController.graph.count()) {
            val circle = ImageView(this)
            circle.setImageResource(R.drawable.ic_lens_white_24dp)
            report_step_indicator_layout.addView(circle)
            if (i == 0) increaseCircleSize(circle) else reduceCircleSize(circle)
        }
    }

    fun stepForward () {
        reduceCircleSize(report_step_indicator_layout.getChildAt(currentPosition))
        increaseCircleSize(report_step_indicator_layout.getChildAt(++currentPosition))
    }

    fun stepBack () {
        reduceCircleSize(report_step_indicator_layout.getChildAt(currentPosition))
        increaseCircleSize(report_step_indicator_layout.getChildAt(--currentPosition))
    }

    private fun reduceCircleSize (circle : View) {

        val size = (8 * resources.displayMetrics.density).toInt()
        (circle as ImageView).setColorFilter(ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null))
        circle.requestLayout()
        circle.layoutParams.height = size
        circle.layoutParams.width = size
    }

    private fun increaseCircleSize (circle : View) {

        val size = (12 * resources.displayMetrics.density).toInt()
        (circle as ImageView).setColorFilter(ResourcesCompat.getColor(resources, R.color.White, null))
        circle.requestLayout()
        circle.layoutParams.height = size
        circle.layoutParams.width = size
    }
}