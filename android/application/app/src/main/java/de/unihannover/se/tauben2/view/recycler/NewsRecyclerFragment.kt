package de.unihannover.se.tauben2.view.recycler

import androidx.databinding.ViewDataBinding
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardNewsBinding
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.viewmodel.NewsViewModel

class NewsRecyclerFragment : RecyclerFragment<News>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_news

    private lateinit var news: News

    override fun onBindData(binding: ViewDataBinding, data: News) {
        val vm = getViewModel(NewsViewModel::class.java)

        if (binding is CardNewsBinding) {
            binding.c = data
        }
    }
}

