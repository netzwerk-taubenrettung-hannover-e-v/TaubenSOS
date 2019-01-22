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
import de.unihannover.se.tauben2.view.main.MainActivity
import de.unihannover.se.tauben2.viewmodel.NewsViewModel
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_edit_news.view.*
import java.util.*

class EditNewsFragment : BaseInfoFragment(R.string.news_edit), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var mBinding: FragmentEditNewsBinding
    private var mNewsViewModel: NewsViewModel? = null
    private var mSelectedStartDate: Calendar = Calendar.getInstance()
    private var mSelectedEndDate: Calendar = Calendar.getInstance()
    private var mToUpdate = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_news, container, false)
        val view = mBinding.root

        (activity as MainActivity).enableBackButton()

        mNewsViewModel = getViewModel(NewsViewModel::class.java)

        val startTimePickerDialog = context?.let {
            TimePickerDialog(it, this,
                    mSelectedStartDate.get(Calendar.HOUR_OF_DAY),
                    mSelectedStartDate.get(Calendar.MINUTE), true)
        }

        val startDatePickerDialog = context?.let {
            DatePickerDialog(it, this,
                    mSelectedStartDate.get(Calendar.YEAR), mSelectedStartDate.get(Calendar.MONTH),
                    mSelectedStartDate.get(Calendar.DAY_OF_MONTH))
        }?.apply {
            setOnCancelListener { startTimePickerDialog?.cancel() }
        }


        val endTimePickerDialog = context?.let {
            TimePickerDialog(it, TimePickerDialog.OnTimeSetListener { _, hour, min ->
                mSelectedEndDate.set(Calendar.HOUR_OF_DAY, hour)
                mSelectedEndDate.set(Calendar.MINUTE, min)
                mBinding.n?.eventEnd = mSelectedEndDate.timeInMillis/1000
                mBinding.invalidateAll()
            }, mSelectedEndDate.get(Calendar.HOUR_OF_DAY), mSelectedEndDate.get(Calendar.MINUTE), true)
        }

        val endDatePickerDialog = context?.let {
            DatePickerDialog(it, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                mSelectedEndDate.set(year, month, day)
            }, mSelectedEndDate.get(Calendar.YEAR), mSelectedStartDate.get(Calendar.MONTH), mSelectedEndDate.get(Calendar.DAY_OF_MONTH))
        }?.apply {
            setOnCancelListener { endTimePickerDialog?.cancel() }
        }

        view.txt_event_start.setOnClickListener {
            startTimePickerDialog?.show()
            startDatePickerDialog?.show()
        }

        view.txt_event_end.setOnClickListener {
            endTimePickerDialog?.show()
            endDatePickerDialog?.show()
        }

        view.btn_send.setOnClickListener {
            sendNewsToServer()

            Navigation.findNavController(it.context as Activity, R.id.nav_host)
                    .navigate(R.id.newsFragment)

        }

        view.checkbox_is_Event.setOnCheckedChangeListener { _, checked ->
            if(checked) {
                mBinding.n?.apply {
                    if(eventStart == null) {
                        eventStart = System.currentTimeMillis()/1000
                        eventEnd = System.currentTimeMillis()/1000 + 3600 // adds one hour
                        mBinding.invalidateAll()
                    } else if(eventEnd == null) {
                        eventEnd = eventStart?.let { it + 3600 } ?: System.currentTimeMillis()/1000 + 3600 // adds one hour
                        mBinding.invalidateAll()
                    }
                }
                view.layout_event_info.visibility = View.VISIBLE
            }
            else
                view.layout_event_info.visibility = View.GONE
        }

        mToUpdate = arguments?.getInt("news")?.let { feedID ->
            mNewsViewModel?.news?.filter { it.feedID == feedID }?.observe(this, LoadingObserver( onSuccess =  {
                if(it.size == 1) {
                    mBinding.n = it[0].apply {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = (eventStart ?: System.currentTimeMillis()/1000)*1000
                        startDatePickerDialog?.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                        startTimePickerDialog?.updateTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                        mSelectedStartDate.timeInMillis = (eventStart ?: System.currentTimeMillis()/1000)*1000

                        val calEnd = Calendar.getInstance()
                        calEnd.timeInMillis = (eventEnd ?: cal.timeInMillis / 1000 + 3600)*1000
                        endDatePickerDialog?.updateDate(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH))
                        endTimePickerDialog?.updateTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE))
                        mSelectedEndDate.timeInMillis = (eventEnd ?: cal.timeInMillis / 1000 + 3600)*1000
                    }

                }
            }))
             true
        } ?: false

        val userViewModel = getViewModel(UserViewModel::class.java)

        if(!mToUpdate && userViewModel?.getOwnerUsername() != null) {
            mBinding.n = News(null, userViewModel.getOwnerUsername(), null, null,"", -1, "")
        }
        return view
    }

    override fun onDateSet(dp: DatePicker?, year: Int, month: Int, day: Int) {
        mSelectedStartDate.set(year, month, day)
    }

    override fun onTimeSet(tp: TimePicker?, hour: Int, min: Int) {
        mSelectedStartDate.set(Calendar.HOUR_OF_DAY, hour)
        mSelectedStartDate.set(Calendar.MINUTE, min)
        mBinding.n?.eventStart = mSelectedStartDate.timeInMillis/1000
        mBinding.invalidateAll()
    }

    private fun sendNewsToServer() {

        multiLet(mNewsViewModel, mBinding.n){ it, news->
            if(view?.checkbox_is_Event?.isChecked != true) {
                news.eventStart = null
                news.eventEnd = null
            }
            if(mToUpdate)
                it.updateNews(news)
            else {
                news.setToCurrentTime()
                it.sendNews(news)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).disableBackButton()
    }
}
