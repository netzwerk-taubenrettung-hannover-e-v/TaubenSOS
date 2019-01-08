package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.News

class NewsViewModel(context: Context) : BaseViewModel(context) {
    val news: LiveDataRes<List<News>> = repository.getNews()
    lateinit var newsPost: LiveDataRes<News>

    fun sendNews(news: News) = repository.sendNews(news)

    fun updateNews(news: News) = repository.updateNews(news)

    fun deleteNews(news: News) = repository.deleteNews(news)
}