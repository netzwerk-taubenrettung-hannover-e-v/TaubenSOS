package de.unihannover.se.tauben2.viewmodel

import android.content.Context
import androidx.lifecycle.Observer
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.model.network.Resource

class NewsViewModel(context: Context) : BaseViewModel(context) {
    val news: LiveDataRes<List<News>> = repository.getNews()
    lateinit var newsPost: LiveDataRes<News>

    fun sendNews(news: News) = repository.sendNews(news)

    fun updateNews(news: News) = repository.updateNews(news)

    fun deleteNews(news: News) = repository.deleteNews(news)

    fun reloadNewsFromServer(successFunction : () -> Any) {
        val result = repository.getNews()
        result.observeForever(object : Observer<Resource<List<News>>> {
            override fun onChanged(t: Resource<List<News>>?) {
                if(t?.status?.isSuccessful() == true) {
                    successFunction()
                    result.removeObserver(this)
                }
                if(t?.hasError() == true)
                    result.removeObserver(this)

            }

        })
    }
}