package de.unihannover.se.tauben2.view.main.fragments.cases

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.databinding.FragmentCaseInfoBinding
import de.unihannover.se.tauben2.model.PicassoVideoRequestHandler
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.Media
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.SquareImageView
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.recycler.RecyclerStringAdapter
import de.unihannover.se.tauben2.view.report.ReportActivity
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_case_info.*
import kotlinx.android.synthetic.main.fragment_case_info.view.*
import android.widget.VideoView
import de.unihannover.se.tauben2.view.main.MainActivity


class CaseInfoFragment: Fragment() {

    private lateinit var mBinding: FragmentCaseInfoBinding
    private var mToolbarMenu: Menu? = null
    private lateinit var mPicassoInstance: Picasso

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_case_info, container, false)
        val v = mBinding.root
        setHasOptionsMenu(true)
        (activity as MainActivity).enableBackButton()

        context?.also {
            mPicassoInstance = Picasso.Builder(it.applicationContext)
                    .addRequestHandler(PicassoVideoRequestHandler()).build()
        }

        getViewModel(UserViewModel::class.java)?.owner?.observe(this, Observer {
            if(it != null && it.status.isSuccessful())
                mBinding.currentUser = it.data
        })

        multiLet(arguments?.getParcelable<Case>("case")?.caseID, getViewModel(CaseViewModel::class.java)) { caseID, caseViewModel ->

            caseViewModel.getCase(caseID).observe(this, LoadingObserver({case ->

                Log.e("CaseInfo", "Last Update: " + getDateTimeString(case.lastUpdated))
                mBinding.c = case

                loadMedia(0, v.image_header)

                case.media.forEachIndexed { i, media ->
                    val image = v.layout_media.getChildAt(i)
                    if (image is SquareImageView) {
                        loadMedia(i, image)
                        if(media.getType().isVideo()) {
                            image.setOnClickListener {
//                                val uri = Uri.parse(case.getMediaURL(case.media[i].mediaID))
//                                view_video.apply {
//                                    visibility = View.VISIBLE
//                                    setVideoURI(uri)
//                                    view_video.start()
//                                }
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(Uri.parse(case.getMediaURL(media.mediaID)), "video/mp4")
                                startActivity(intent)
                            }
                        } else {
                            image.zoomImage(v.image_expanded, v.layout_main, v.layout_constraint, activity as MainActivity)
                            image.addImageZoomListener (
                                {
                                    case.loadMediaFromServerInto(case.media[i], image_expanded, fit = false)
                                }, {
                                    case.loadMediaFromServerInto(case.media[i], image)
                                })
                        }
                    }
                }

                val injuryList = case.injury?.toStringList() ?: listOf()

                v.recycler_injuries.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    adapter = RecyclerStringAdapter(R.layout.injuries_item, R.id.chip_injury, injuryList)
                }

                if(BootingActivity.getOwnerPermission() != Permission.GUEST)
                    mToolbarMenu?.run { setOptionsMenuDeadItems(this) }

                v.btn_state_next.setOnClickListener {
                    mBinding.currentUser?.also { user ->
                        case.nextState(user)
                        caseViewModel.updateCase(case, listOf())
                    }
                }

                v.btn_state_prev.setOnClickListener {
                    mBinding.currentUser?.also { user ->
                        case.previousState(user)
                        caseViewModel.updateCase(case, listOf())
                    }
                }

            }))
        }

        return v
    }

    fun zoomOut() : View{
        return mBinding.root.image_expanded
    }

    private fun loadMedia(index: Int, target: ImageView) {
        mBinding.c?.also { case ->
            var media: Media? = null

            if(case.media.size <= index) {
                case.loadMediaFromServerInto(media, target, null)
                return
            }

            media = case.media[index]

            //TODO Thumbnail loading
            case.loadMediaFromServerInto(media, target, null)
//            if(media.mimeType == "video/mp4")
//                mPicassoInstance.load(PicassoVideoRequestHandler.SCHEME_VIDEO + ":" + case.getImageURL(media))?.into(target)
//            else
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        mToolbarMenu = menu
        mToolbarMenu?.let { setOptionsMenuItems(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val result = super.onOptionsItemSelected(item)

        when(item?.itemId){
            R.id.toolbar_call_button -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBinding.c?.phone)))
            R.id.toolbar_report_dead -> reportAsDead(true)
            R.id.toolbar_report_alive -> reportAsDead(false)
            R.id.toolbar_edit -> {
                val intent = Intent(activity, ReportActivity::class.java)
                intent.putExtra("case", mBinding.c)
                startActivity(intent)
            }
            R.id.toolbar_delete -> {
                multiLet(context, mBinding.c) { cxt, case ->
                    AlertDialog.Builder(cxt)
                            .setTitle("Do you want to delete this case?")
                            .setMessage("The case will not be recoverable.")
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
        }
        return result
    }

    private fun reportAsDead(isDead: Boolean) {

        multiLet(mBinding.c, getViewModel(CaseViewModel::class.java)) { case, viewModel->
            case.apply {
                wasFoundDead = isDead
                media = listOf()
            }
            viewModel.updateCase(case, listOf())
        }
    }

    private fun setOptionsMenuItems(menu: Menu) {
        val permission = BootingActivity.getOwnerPermission()
        menu.apply{
            if(permission == Permission.ADMIN || permission == Permission.AUTHORISED) {
                findItem(R.id.toolbar_call_button)?.isVisible = true
                findItem(R.id.toolbar_delete)?.isVisible = true
                setOptionsMenuDeadItems(menu)
            }
            findItem(R.id.toolbar_edit)?.isVisible = true
            findItem(R.id.toolbar_report_button)?.isVisible = false
        }
    }

    private fun setOptionsMenuDeadItems(menu: Menu) {
        val wasFoundDead = mBinding.c?.wasFoundDead ?: false
        menu.findItem(R.id.toolbar_report_alive)?.isVisible = wasFoundDead
        menu.findItem(R.id.toolbar_report_dead)?.isVisible = !wasFoundDead
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).disableBackButton()
    }

}
