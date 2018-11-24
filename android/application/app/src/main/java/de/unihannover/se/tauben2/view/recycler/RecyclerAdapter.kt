package de.unihannover.se.tauben2.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerAdapter<Data : RecyclerItem>(var data: List<Data> = listOf()) :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), getItemLayoutId(viewType), parent, false))

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = data[position].getType().id

    @LayoutRes
    abstract fun getItemLayoutId(viewType: Int): Int

}