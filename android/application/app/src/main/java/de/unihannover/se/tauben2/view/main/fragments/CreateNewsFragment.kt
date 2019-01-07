package de.unihannover.se.tauben2.view.main.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.FragmentCreateNewsBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.Singleton
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.fragment_create_news.*
import kotlinx.android.synthetic.main.fragment_create_news.view.*

class CreateNewsFragment : Fragment(){

    companion object: Singleton<NewsFragment>() {
        override fun newInstance() = NewsFragment()
    }

    private lateinit var mBinding: FragmentCreateNewsBinding
    private lateinit var mCreatedNews: News

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_news, container, false)
        val view = mBinding.root
        view.btn_send_news.setOnClickListener {
            sendNewsToServer()
            Navigation.findNavController(it.context as Activity, R.id.nav_host).navigate(R.id.newsFragment, CreateNewsFragment.bundle)
        }

        return view
    }

    protected fun sendNewsToServer() {

        getViewModel(NewsViewModel::class.java)?.let {
            // New News

            mCreatedNews = News(null, "test", 123555, txt_news_body.text.toString(), 25884, txt_news_title.text.toString())
            if (mCreatedNews.feedID == null) {
                Log.d("SENT NEWS", "news sent: $mCreatedNews")
                it.sendNews(mCreatedNews)
            }
            /*// Edit News
            else {
                it.updateNews(mCreatedNews)
                Log.d("EDIT CASE", "case edited: $mCreatedCase")
            }*/
        }
    }
}