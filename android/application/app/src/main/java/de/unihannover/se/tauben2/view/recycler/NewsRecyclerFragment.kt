package de.unihannover.se.tauben2.view.recycler

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.databinding.CardNewsBinding
import de.unihannover.se.tauben2.getDateTimeString
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.Permission
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.setSnackBar
import de.unihannover.se.tauben2.view.main.BootingActivity
import de.unihannover.se.tauben2.view.navigation.BottomNavigationDrawerFragment
import de.unihannover.se.tauben2.view.navigation.BottomNavigator
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import kotlinx.android.synthetic.main.card_news.*
import kotlinx.android.synthetic.main.card_news.view.*

class NewsRecyclerFragment : RecyclerFragment<News>() {

    override fun getRecyclerItemLayoutId(viewType: Int) = R.layout.card_news

    override fun onChanged(t: List<News>?) {
        super.onChanged(t?.sortedBy { it.eventStart ?: it.timestamp })
    }

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


            binding.root.let{ v->
                if(BootingActivity.getOwnerPermission() == Permission.GUEST){
                    v.news_edit_button.visibility = GONE
                    v.news_delete_button.visibility = GONE
                }
                else {
                    v.news_edit_button.visibility = VISIBLE
                    v.news_delete_button.visibility = VISIBLE
                    v.news_edit_button.setOnClickListener {
                        val bundle = Bundle().apply {
                            putInt("news", data.feedID ?: -1)
                        }
                        Navigation.findNavController(context as Activity, R.id.nav_host).navigate(R.id.editNewsFragment, bundle)
                    }
                    v.news_delete_button.setOnClickListener {
                        context?.let {cxt ->
                            AlertDialog.Builder(cxt)
                                    .setTitle(getString(R.string.delete_news_question))
                                    .setMessage(getString(R.string.delete_news_info))
                                    .setPositiveButton(R.string.delete) { _, _ ->
                                        vm?.deleteNews(data)
                                    }.setNegativeButton(R.string.cancel) { di, _ ->
                                        di.cancel()
                                    }.show()
                        }
                    }
                }

            }

        }
    }
}

