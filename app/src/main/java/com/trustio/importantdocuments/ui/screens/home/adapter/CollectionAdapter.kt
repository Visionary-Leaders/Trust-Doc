package com.trustio.importantdocuments.ui.screens.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
import com.trustio.importantdocuments.databinding.CollectionItemBinding

class CollectionAdapter :RecyclerView.Adapter<CollectionAdapter.CollectionVh>() {
    private val list =ArrayList<SectionsResponseItem>()
    inner class  CollectionVh(private val binding: CollectionItemBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(data :SectionsResponseItem){
            binding.apply {
                collectionName.text=data.name.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionVh {
        return CollectionVh(CollectionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun submitList(list:List<SectionsResponseItem>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CollectionVh, position: Int) {
        holder.onBind(list[position])
    }
}