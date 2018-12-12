package de.unihannover.se.tauben2.view.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class RecyclerStringAdapter (@LayoutRes val layoutRes: Int, @IdRes val textViewRes: Int, var data: List<String> = listOf()) :
        RecyclerView.Adapter<RecyclerStringAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutRes, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.root.findViewById<TextView>(textViewRes).text = data[position]
    }
}
