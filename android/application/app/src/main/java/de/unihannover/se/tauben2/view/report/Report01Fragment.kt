package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReport01Binding
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.Singleton
import kotlinx.android.synthetic.main.fragment_report01.view.*


class Report01Fragment : Fragment() {

    private var mCreatedCase: Case? = null

    companion object : Singleton<Report01Fragment>() {
        override fun newInstance() = Report01Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReport01Binding>(inflater, R.layout.fragment_report01, container, false)

        mCreatedCase = arguments?.getParcelable("createdCase")
        mCreatedCase?.let {
            binding.createdCase = it
        }

        binding.root.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigateUp()
        }

        binding.root.report_next_step_button.setOnClickListener {
            val caseBundle = Bundle()
            caseBundle.putParcelable("createdCase", mCreatedCase)
            Navigation.findNavController(context as Activity, R.id.nav_host)
                    .navigate(R.id.report02Fragment, caseBundle)
        }
        return binding.root
    }

}