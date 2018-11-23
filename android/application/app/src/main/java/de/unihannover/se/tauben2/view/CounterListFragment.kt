package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.se.tauben2.R
import de.unihannover.se.tauben2.R.layout.fragment_counter_list

class CounterListFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<AdapterList.ViewHolder>? = null

    companion object {
        fun newInstance(): CounterFragment {
            return CounterFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter_list, container, false)

        var rv = view.findViewById(R.id.recycler_view) as RecyclerView
        layoutManager = LinearLayoutManager(activity)
        rv.layoutManager = layoutManager

        adapter = AdapterList()
        rv.adapter = adapter

        return view
    }

}