package de.unihannover.se.tauben2.view.recycler

import android.app.Activity
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardNewsBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.navigation.BottomNavigationDrawerFragment
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.card_news.*
import kotlinx.android.synthetic.main.card_news.view.*

class NewsRecyclerFragment : RecyclerFragment<News>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_news

    private lateinit var news: News

    override fun onBindData(binding: ViewDataBinding, data: News) {
        val vm = getViewModel(NewsViewModel::class.java)

        if (binding is CardNewsBinding) {
            binding.n = data
            /*binding.root.news_more_button.setOnClickListener {
                if(data.feedID!=null){
                    vm?.setNewsPost(data.feedID)
                    val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
                    bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
                }
                else{
                    setSnackBar(it, "Error: news post data is broken", null)
                }*/


            binding.root.let{v->
                if(BootingActivity.getOwnerPermission() == Permission.GUEST){
                    v.news_edit_button.visibility = GONE
                    v.news_delete_button.visibility = GONE
                }
                else {
                    v.news_edit_button.visibility = VISIBLE
                    v.news_delete_button.visibility = VISIBLE
                    v.news_edit_button.setOnClickListener {
                        Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.editNewsFragment)
                    }
                    v.news_delete_button.setOnClickListener {
                        vm?.deleteNews(data)
                    }
                }

            }

        }
    }
}

