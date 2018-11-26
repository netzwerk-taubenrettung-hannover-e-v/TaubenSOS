package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.view.navigation.FragmentChangeListener
import kotlinx.android.synthetic.main.fragment_report00.*

class Report00Fragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(): Report00Fragment {
            return Report00Fragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_report00, container, false)

        view.findViewById<View>(R.id.report_next_step_button).setOnClickListener(this)

        return view
    }

    override fun onClick(view: View?) {

        when (view) {

            report_next_step_button -> {
                (activity as FragmentChangeListener).replaceFragment(Report01Fragment.newInstance())
            }
        }
    }

}