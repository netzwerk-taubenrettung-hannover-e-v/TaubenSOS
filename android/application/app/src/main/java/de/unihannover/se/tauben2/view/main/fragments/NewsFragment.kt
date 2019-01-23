package de.unihannover.se.tauben2.view.main.fragments

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.recycler.NewsRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_news.view.*

class NewsFragment : BaseMainFragment(R.string.news_title) {

    private lateinit var recyclerFragment: NewsRecyclerFragment

    private var mCurrentObservedData: LiveDataRes<List<News>>? = null
    private lateinit var mCurrentObserver: LoadingObserver<List<News>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_news, container, false)

        if(BootingActivity.getOwnerPermission() == Permission.GUEST)
            view.create_news_button.hide()

        recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment_news) as NewsRecyclerFragment

        mCurrentObserver = LoadingObserver(successObserver = recyclerFragment)

        loadNews()

        view.create_news_button.setOnClickListener {
            Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.editNewsFragment)
        }

        activity?.title = resources.getQuantityString(R.plurals.news, 2)

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.findItem(R.id.toolbar_reload)?.apply {
            isVisible = true
            setOnMenuItemClickListener {
                view?.let { v ->
                    getViewModel(NewsViewModel::class.java)?.reloadNewsFromServer{ setSnackBar(v, getString(R.string.reload_successful)) }
                    return@setOnMenuItemClickListener true
                }
                false
            }
        }
    }

    private fun loadNews() {

        getViewModel(NewsViewModel::class.java)?.let { viewModel ->

            // Remove old Observer
            mCurrentObservedData?.removeObserver(mCurrentObserver)

            mCurrentObservedData = viewModel.news

            mCurrentObservedData?.observe(this, mCurrentObserver)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return false
    }

}