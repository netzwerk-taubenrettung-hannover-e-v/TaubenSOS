package de.unihannover.se.tauben2.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCaseInfoBinding
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.recycler.RecyclerStringAdapter
import de.unihannover.se.tauben2.view.report.ReportActivity
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import kotlinx.android.synthetic.main.fragment_case_info.view.*


class CaseInfoFragment: Fragment()/*, Observer<Location?>*/ {

    private lateinit var mBinding: FragmentCaseInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_case_info, container, false)
        val v = mBinding.root

        arguments?.getParcelable<Case>("case")?.let { argumentCase ->
            getViewModel(CaseViewModel::class.java)?.cases?.filter { it.caseID == argumentCase.caseID }?.observe(this, LoadingObserver({
                if(it.size == 1) {
                    val case = it[0]
                    mBinding.c = case

                    val firstImage = Picasso.get().load(if (case.media.isEmpty()) null else case.media[0])
                    firstImage.into(v.image_header)

                    for (i in 0 until v.layout_media.childCount) {
                        val image = v.layout_media.getChildAt(i)
                        if (image is SquareImageView) {
                            Picasso.get().load(if (case.media.size >= i + 1) case.media[i] else null)
                                    .into(image)
                            image.zoomImage(v.image_expanded, v.layout_main, v.layout_constraint)
                        }
                    }

                    val injuryList = case.injury?.toStringList() ?: listOf()
                    v.recycler_injuries.apply {
                        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        adapter = RecyclerStringAdapter(R.layout.injuries_item, R.id.chip_injury, injuryList)
                    }

                    v.btn_edit.setOnClickListener {
                        // send case to ReportActivity
                        val intent = Intent(activity, ReportActivity::class.java)
                        intent.putExtra("case", case)
                        startActivity(intent)
                    }

                    v.btn_delete.setOnClickListener {
                        context?.let { cxt ->
                            AlertDialog.Builder(cxt).setTitle("Do you want to delete this case?").setMessage("The case will not be recoverable.")
                                    .setPositiveButton(R.string.delete) { _, _ ->
                                        getViewModel(CaseViewModel::class.java)?.deleteCase(case)
                                        val controller = Navigation.findNavController(context as Activity, R.id.nav_host)
                                        controller.navigatorProvider.getNavigator(BottomNavigator::class.java).popFromBackStack()
                                        controller.navigate(R.id.casesFragment)
                                    }.setNegativeButton(R.string.cancel) { di, _ ->
                                        di.cancel()
                                    }.show()
                        }
                    }

                    v.btn_take.setOnClickListener {
                        getViewModel(CaseViewModel::class.java)?.let { viewModel ->
                            if(case.rescuer == null)
                                case.rescuer = "Taubenhans"
                            else
                                case.isClosed = true
                            case.media = listOf()
                            viewModel.updateCase(case, listOf())
                        }
                    }
                }
            }))
        }

        setHasOptionsMenu(true)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.findItem(R.id.toolbar_call_button)?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.toolbar_call_button)
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBinding.c?.phone)))
        return super.onOptionsItemSelected(item)
    }

//    override fun onChanged(location: Location?) {
//        if(location == null)
//            return
//        view?.let {
//            mBinding.c?.let {case ->
//                val caseLoc = Location("").apply {
//                    latitude = case.latitude
//                    longitude = case.longitude
//                }
//                val res = ((Math.round(location.distanceTo(caseLoc)/10))/100.0).toString() + " km"
////                it.distance_text.text = res
//            }
//        }
//    }

//    override fun onResume() {
//        super.onResume()
//        getViewModel(LocationViewModel::class.java)?.observeCurrentLocation(this,s this)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        getViewModel(LocationViewModel::class.java)?.stopObservingCurrentLocation(this)
//    }

}
