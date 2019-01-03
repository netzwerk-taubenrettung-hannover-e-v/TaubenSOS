package de.unihannover.se.tauben2.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.view.recycler.NewsRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.NewsViewModel

class NewsFragment : Fragment() {

    private lateinit var recyclerFragment : NewsRecyclerFragment

    private var mCurrentObservedData: LiveDataRes<List<News>>? = null
    private lateinit var mCurrentObserver: LoadingObserver<List<News>>

    companion object: Singleton<NewsFragment>() {
        override fun newInstance() = NewsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_news, container, false)
        recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as NewsRecyclerFragment

        mCurrentObserver = LoadingObserver(successObserver = recyclerFragment)

        loadNews()

        return view
    }

    private fun loadNews () {

        getViewModel(NewsViewModel::class.java)?.let { viewModel ->

            // Remove old Observers
            mCurrentObservedData?.removeObserver(mCurrentObserver)

            //mCurrentObservedData = viewModel.news

            mCurrentObservedData?.observe(this, mCurrentObserver)
        }
    }

}