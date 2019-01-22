package de.unihannover.se.tauben2.view.main.fragments.cases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import de.unihannover.se.tauben2.LiveDataRes
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.filter
import de.unihannover.se.tauben2.getViewModel
import de.unihannover.se.tauben2.model.database.entity.Case
import de.unihannover.se.tauben2.view.LoadingObserver
import de.unihannover.se.tauben2.view.main.fragments.BaseMainFragment
import de.unihannover.se.tauben2.view.main.fragments.MapViewFragment
import de.unihannover.se.tauben2.view.recycler.CasesRecyclerFragment
import de.unihannover.se.tauben2.viewmodel.CaseViewModel
import de.unihannover.se.tauben2.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_cases.view.*

abstract class CasesFragment: BaseMainFragment(R.string.cases) {

    lateinit var v : View

    protected var mFilter: Filter = Filter.ALL

    enum class Filter(@StringRes val titleRes: Int) {
        ALL(R.string.all_cases), CLOSED(R.string.closed_cases), OPEN(R.string.open_cases), MY(R.string.my_cases)
    }

    protected lateinit var recyclerFragment : CasesRecyclerFragment
    protected lateinit var mapsFragment : MapViewFragment

    private var mCurrentObservedData: LiveDataRes<List<Case>>? = null
    private lateinit var mCurrentObserver: LoadingObserver<List<Case>>
    private lateinit var mCurrentMapObserver: LoadingObserver<List<Case>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_cases, container, false)
        recyclerFragment = childFragmentManager.findFragmentById(R.id.recycler_fragment) as CasesRecyclerFragment
        mapsFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapViewFragment

        mCurrentObserver = LoadingObserver(successObserver = recyclerFragment)
        mCurrentMapObserver = LoadingObserver(successObserver = mapsFragment)


        return v
    }

    override fun onStart() {
        super.onStart()
        loadCases(mFilter)
    }

    protected fun loadCases (filter : Filter) {

        getViewModel(CaseViewModel::class.java)?.let { viewModel ->

            val userViewModel = getViewModel(UserViewModel::class.java)

            // Remove old Observers
            mCurrentObservedData?.removeObserver(mCurrentObserver)
            mCurrentObservedData?.removeObserver(mCurrentMapObserver)


            val filterFunc: (Case) -> Boolean = when(filter) {
                Filter.MY -> if(userViewModel?.getOwnerUsername() != null) { case -> case.rescuer == userViewModel.getOwnerUsername()} else { case -> case.phone == userViewModel?.getGuestPhone() }
                Filter.CLOSED -> { case -> case.isClosed == true }
                Filter.OPEN -> { case -> case.isClosed == false }
                else -> { _ -> true }
            }
            mCurrentObservedData = viewModel.cases.filter(filterFunc)

            mCurrentObservedData?.observe(this, mCurrentObserver)
            mCurrentObservedData?.observe(this, mCurrentMapObserver)

            if(mCurrentObservedData?.value?.data?.isEmpty() == true) {
                view?.layout_main?.visibility = View.GONE
            }
            activity?.title = getString(filter.titleRes)
        }
    }

    fun zoomOut() : View{
        return v.image_expanded
    }
}