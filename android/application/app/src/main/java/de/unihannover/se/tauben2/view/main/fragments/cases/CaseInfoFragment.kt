package de.unihannover.se.tauben2.view.main.fragments.cases

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCaseInfoBinding
import de.unihannover.se.tauben2.getDateTimeString
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.PicassoVideoRequestHandler
import de.unihannover.se.tauben2.model.database.Media
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.multiLet
import de.unihannover.se.tauben2.view.InfoImageView
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.SquareImageView
import de.unihannover.se.tauben2.view.VideoPlayerActivity
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.main.MainActivity
import de.unihannover.se.tauben2.view.main.fragments.BaseInfoFragment
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.view.recycler.RecyclerStringAdapter
import de.unihannover.se.tauben2.view.report.ReportActivity
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_case_info.*
import kotlinx.android.synthetic.main.fragment_case_info.view.*


class CaseInfoFragment : BaseInfoFragment(R.string.case_info) {

    private lateinit var mBinding: FragmentCaseInfoBinding
    private var mToolbarMenu: Menu? = null
    private lateinit var mPicassoInstance: Picasso
    private var mCaseId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_case_info, container, false)
        val v = mBinding.root

        setHasOptionsMenu(true)

        context?.also {
            mPicassoInstance = Picasso.Builder(it.applicationContext)
                    .addRequestHandler(PicassoVideoRequestHandler()).build()
        }

        mCaseId = arguments?.getParcelable<Case>("case")?.caseID

        return v
    }

    override fun onStart() {
        super.onStart()

        getViewModel(UserViewModel::class.java)?.owner?.observe(this, Observer {
            if (it != null && it.status.isSuccessful())
                mBinding.currentUser = it.data
        })

        view?.let {v ->
            multiLet(mCaseId, getViewModel(CaseViewModel::class.java)) { caseID, caseViewModel ->

                caseViewModel.getCase(caseID).observe(this, LoadingObserver({ case ->

                    Log.e("CaseInfo", "Last Update: " + getDateTimeString(case.lastUpdated))
                    mBinding.c = case

                    loadMedia(0, v.image_header)

                    case.media.forEachIndexed { i, media ->
                        val image = v.layout_media.getChildAt(i)
                        if (image is InfoImageView) {
                            loadMedia(i, image.getImage())
                            if (media.getType().isVideo()) {
                                image.setPlayable(true)
                                image.setOnClickListener {
                                    //                                val uri = Uri.parse(case.getMediaURL(case.media[i].mediaID))
                                    //                                view_video.apply {
                                    //                                    visibility = View.VISIBLE
                                    //                                    setVideoURI(uri)
                                    //                                    view_video.start()
                                    //                                }
                                    val intent = Intent(context, VideoPlayerActivity::class.java)
                                    intent.putExtra("url", case.getMediaURL(media.mediaID))
                                    startActivity(intent)
                                }
                            } else {
                                image.getImage().zoomImage(v.image_expanded, v.layout_main, v.layout_constraint, activity as MainActivity)
                                image.getImage().addImageZoomListener(
                                        {
                                            case.loadMediaFromServerInto(case.media[i], image_expanded, fit = false)
                                        }, {
                                    case.loadMediaFromServerInto(case.media[i], image.getImage())
                                })
                            }
                        }
                    }

                    val injuryList = case.injury?.toStringList() ?: listOf()

                    v.recycler_injuries.apply {
                        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        adapter = RecyclerStringAdapter(R.layout.injuries_item, R.id.chip_injury, injuryList)
                    }

                    if (BootingActivity.getOwnerPermission() != Permission.GUEST)
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
        }
    }

    fun zoomOut(): View {
        return mBinding.root.image_expanded
    }

    private fun loadMedia(index: Int, target: ImageView) {
        mBinding.c?.also { case ->
            var media: Media? = null

            if (case.media.size <= index) {
                case.loadMediaFromServerInto(media, target, null)
                return
            }

            media = case.media[index]

            case.loadMediaFromServerInto(media, target, null)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        mToolbarMenu = menu
        mToolbarMenu?.let { setOptionsMenuItems(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val result = super.onOptionsItemSelected(item)

        when (item?.itemId) {
            R.id.toolbar_call_button -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mBinding.c?.phone)))
            R.id.toolbar_navigate -> startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=${mBinding.c?.latitude},${mBinding.c?.longitude} (${mBinding.c?.getPigeonBreed()?.getTitle()} (${getString(R.string.priority, mBinding.c?.priority?.toString())}))")))
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
                            .setTitle(getString(R.string.delete_case_question))
                            .setMessage(getString(R.string.delete_case_info))
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

        multiLet(mBinding.c, getViewModel(CaseViewModel::class.java)) { case, viewModel ->
            case.apply {
                wasFoundDead = isDead
                media = listOf()
            }
            viewModel.updateCase(case, listOf())
        }
    }

    private fun setOptionsMenuItems(menu: Menu) {
        val permission = BootingActivity.getOwnerPermission()
        menu.apply {
            if (permission == Permission.ADMIN || permission == Permission.AUTHORISED) {
                findItem(R.id.toolbar_call_button)?.isVisible = true
                findItem(R.id.toolbar_delete)?.isVisible = true
                setOptionsMenuDeadItems(menu)
            }
            findItem(R.id.toolbar_navigate)?.isVisible = true
            findItem(R.id.toolbar_edit)?.isVisible = true
            findItem(R.id.toolbar_report_button)?.isVisible = false
        }
    }

    private fun setOptionsMenuDeadItems(menu: Menu) {
        val wasFoundDead = mBinding.c?.wasFoundDead ?: false
        menu.findItem(R.id.toolbar_report_alive)?.isVisible = wasFoundDead
        menu.findItem(R.id.toolbar_report_dead)?.isVisible = !wasFoundDead
    }

}
