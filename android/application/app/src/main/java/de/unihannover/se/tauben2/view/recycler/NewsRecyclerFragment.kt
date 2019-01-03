package de.unihannover.se.tauben2.view.recycler

import androidx.databinding.ViewDataBinding
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardNewsBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.navigation.BottomNavigationDrawerFragment
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.card_news.*

class NewsRecyclerFragment : RecyclerFragment<News>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_news

    private lateinit var news: News

    override fun onBindData(binding: ViewDataBinding, data: News) {
        val vm = getViewModel(NewsViewModel::class.java)

        if (binding is CardNewsBinding) {
            //binding.c = data
            //bitte erst testen ob es kompiliert bevor ihr etwas pusht danke
        }

        news_more_button.setOnClickListener {
            val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
            bottomNavDrawerFragment.show(activity!!.supportFragmentManager, bottomNavDrawerFragment.tag)
        }
    }
}

