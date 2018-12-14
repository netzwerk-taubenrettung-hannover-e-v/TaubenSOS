package de.unihannover.se.tauben2.view.report

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.common.util.IOUtils
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentReport02Binding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.fragment_report02.view.*
import java.io.File

class Report02Fragment : Fragment() {

    private val LOG_TAG = this::class.java.simpleName

    private var mCreatedCase: Case? = null


    companion object : Singleton<Report02Fragment>() {
        override fun newInstance() = Report02Fragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentReport02Binding>(inflater, R.layout.fragment_report02, container, false)

        mCreatedCase = arguments?.getParcelable("createdCase")
        mCreatedCase?.let {
            binding.createdCase = it
        }

        binding.root.report_prev_step_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.report01Fragment)
        }

        binding.root.report_send_button.setOnClickListener {
            mCreatedCase?.setToCurrentTime()
            sendCaseToServer(binding.root)
        }

        return binding.root
    }

    private fun sendCaseToServer(view: View) {
        val caseViewModel = getViewModel(CaseViewModel::class.java)
        mCreatedCase?.let { case ->
            caseViewModel?.let {
                val mediaPaths = arguments?.getStringArrayList(Report00Fragment.MEDIA_PATHS_KEY)
                val mediaFiles = mediaPaths?.let { paths -> readAsRaw(paths) }

                //it.sendCase(case, )
                Log.d(LOG_TAG, "Sent case: $case")
                setSnackBar(view, "Case sent successfully.")

                val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
                controller.navigatorProvider.getNavigator(BottomNavigator::class.java).popFromBackStack(3)
                controller.navigate(R.id.newsFragment)
            }
        }
    }

    private fun readAsRaw(filePaths: List<String>): List<ByteArray> {
        return mutableListOf()
    }
}