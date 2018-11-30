package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.Singleton
import kotlinx.android.synthetic.main.fragment_report01.*
import kotlinx.android.synthetic.main.fragment_report01.view.*

class Report01Fragment : Fragment() {

    companion object: Singleton<Report01Fragment>() {
        override fun newInstance() = Report01Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report01, container, false)

        view.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }


        view.report_next_step_button.setOnClickListener {
            checkSelected()

        }



        return view
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
        if(!permission) {
            //Todo ALertDialog
            report_injury_title.setTextColor(Color.RED)
            report_injury_layout.setBackgroundResource(R.drawable.border_layout)
            /*val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Fehler")

            // Display a message on alert dialog
            builder.setMessage("Bitte füllen Sie alle Pflichfelder aus")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("YES"){dialog, which ->
                // Do something when user press the positive button
                Toast.makeText(applicationContext,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()

                // Change the app background color
                root_layout.setBackgroundColor(Color.RED)*/
        } else {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report02Fragment)
        }
    }
}