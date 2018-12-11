package de.unihannover.se.tauben2.view.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.se.tauben2.R
import kotlinx.android.synthetic.main.fragment_recyler_view.view.*

abstract class RecyclerFragment<Data : RecyclerItem>(val hasDivider: Boolean = false): Fragment(), Observer<List<Data>> {

    private val viewAdapter: RecyclerAdapter<Data>

    init {
        viewAdapter = object : RecyclerAdapter<Data>() {
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                onBindData(holder.binding, data[position])
                onDataLoaded(holder.binding.root, position)
            }

            override fun getItemLayoutId(viewType: Int) = getRecylcerItemLayoutId(viewType)
        }
    }

    open fun onDataLoaded(itemView: View, position: Int) {}

    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val layout = inflater.inflate(R.layout.fragment_recyler_view, container, false)

        viewManager = LinearLayoutManager(activity)

        layout.recylcer_view.setHasFixedSize(true)
        layout.recylcer_view.layoutManager = viewManager
        layout.recylcer_view.adapter = viewAdapter

        return layout
    }

    override fun onChanged(t: List<Data>?) {
        t?.let {
            updateData(it)
        }
    }

    private fun updateData(data: List<Data>){
        viewAdapter.data = data
        notifyDataSetChanged()
    }

    protected fun notifyDataSetChanged() = viewAdapter.notifyDataSetChanged()

    fun getData() = viewAdapter.data

    @LayoutRes
    abstract fun getRecylcerItemLayoutId(viewType: Int): Int

    /**
     * Called every time data has changed
     * @param binding the viewdatabinding-class
     * @param data the new data
     */
    abstract fun onBindData(binding: ViewDataBinding, data: Data)
}