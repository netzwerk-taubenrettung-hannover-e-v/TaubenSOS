package de.unihannover.se.tauben2.view.main.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.recycler.NewsRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_news.view.*

class NewsFragment : Fragment() {

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

        return view
    }

    private fun loadNews() {

        getViewModel(NewsViewModel::class.java)?.let { viewModel ->

            // Remove old Observer
            mCurrentObservedData?.removeObserver(mCurrentObserver)

            mCurrentObservedData = viewModel.news.filter { it.eventEnd?.let { end -> end > System.currentTimeMillis()/1000 } ?: true }

            mCurrentObservedData?.observe(this, mCurrentObserver)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return false
    }

}