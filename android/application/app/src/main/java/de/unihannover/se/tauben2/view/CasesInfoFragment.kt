package de.unihannover.se.tauben2.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCaseInfoBinding
import de.unihannover.se.tauben2.model.entity.Case
import de.unihannover.se.tauben2.view.recycler.RecyclerStringAdapter
import de.unihannover.se.tauben2.view.report.ReportActivity
import kotlinx.android.synthetic.main.fragment_case_info.view.*


class CasesInfoFragment: Fragment()/*, Observer<Location?>*/ {

    private lateinit var mBinding: FragmentCaseInfoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_case_info, container, false)
        val v = mBinding.root

        arguments?.getParcelable<Case>("case")?.let { case ->
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

        }
        return v
    }
}
