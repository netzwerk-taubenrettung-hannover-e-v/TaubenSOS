package de.unihannover.se.tauben2.view.main.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import de.unihannover.se.tauben2.*
import de.unihannover.se.tauben2.databinding.FragmentEditNewsBinding
import de.unihannover.se.tauben2.model.database.entity.News
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_edit_news.view.*
import java.util.*

class EditNewsFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var mBinding: FragmentEditNewsBinding
    private var mNewsViewModel: NewsViewModel? = null
    private var mSelectedDate: Calendar = Calendar.getInstance()
    private var mToUpdate = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_news, container, false)
        val view = mBinding.root

        mNewsViewModel = getViewModel(NewsViewModel::class.java)



        val timePickerDialog = context?.let {
            TimePickerDialog(it, this,
                    mSelectedDate.get(Calendar.HOUR_OF_DAY),
                    mSelectedDate.get(Calendar.MINUTE), true)
        }

        val datePickerDialog = context?.let {
            DatePickerDialog(it, this,
                    mSelectedDate.get(Calendar.YEAR), mSelectedDate.get(Calendar.MONTH),
                    mSelectedDate.get(Calendar.DAY_OF_MONTH))
        }?.apply {
            setOnCancelListener {
                timePickerDialog?.cancel()
            }
        }

        view.txt_event_start.setOnClickListener {
            timePickerDialog?.show()
            datePickerDialog?.show()
        }

        view.btn_send.setOnClickListener {
            sendNewsToServer()

            Navigation.findNavController(it.context as Activity, R.id.nav_host)
                    .navigate(R.id.newsFragment)

        }


        mToUpdate = arguments?.getInt("news")?.let { feedID ->
            mNewsViewModel?.news?.filter { it.feedID == feedID }?.observe(this, LoadingObserver( onSuccess =  {
                if(it.size == 1) {
                    mBinding.n = it[0].apply {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = eventStart*1000
                        datePickerDialog?.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                        timePickerDialog?.updateTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                        mSelectedDate.timeInMillis = eventStart*1000
                    }

                }
            }))
             true
        } ?: false

        val userViewModel = getViewModel(UserViewModel::class.java)

        if(!mToUpdate && userViewModel?.getOwnerUsername() != null) {
            mBinding.n = News(null, userViewModel.getOwnerUsername(), System.currentTimeMillis()/1000, "", -1, "")
        }
        return view
    }

    override fun onDateSet(dp: DatePicker?, year: Int, month: Int, day: Int) {
        mSelectedDate.set(year, month, day)
    }

    override fun onTimeSet(dp: TimePicker?, hour: Int, min: Int) {
        mSelectedDate.set(Calendar.HOUR_OF_DAY, hour)
        mSelectedDate.set(Calendar.MINUTE, min)
        mBinding.n?.eventStart = mSelectedDate.timeInMillis/1000
        mBinding.invalidateAll()

    }

    private fun sendNewsToServer() {

        multiLet(mNewsViewModel, mBinding.n){ it, news->
            if(mToUpdate)
                it.updateNews(news)
            else {
                news.setToCurrentTime()
                it.sendNews(news)
            }
        }
    }
}
