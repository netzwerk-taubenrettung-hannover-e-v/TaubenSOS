package de.unihannover.se.tauben2.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.se.tauben2.R.layout.fragment_counter

class CounterFragment : Fragment() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<AdapterList.ViewHolder>? = null

    companion object {
        fun newInstance(): CounterFragment {
            return CounterFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(fragment_counter, container, false)
        return view
    }

}