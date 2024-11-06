package com.trustio.importantdocuments.ui.screens.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.data.remote.response.section.SectionsResponseItem
import com.trustio.importantdocuments.databinding.CollectionItemBinding

class CollectionAdapter(val isChoose:Boolean=false) :RecyclerView.Adapter<CollectionAdapter.CollectionVh>() {
    private val list =ArrayList<SectionsResponseItem>()
    private var clickListener : (SectionsResponseItem) -> Unit = {}

    fun setItemClickListener(listener:(SectionsResponseItem) -> Unit){
        this.clickListener = listener
    }
    inner class  CollectionVh(private val binding: CollectionItemBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(data :SectionsResponseItem){
            binding.apply {
                binding.shapeableImageView.visibility = isChoose.let { if(!it) View.VISIBLE else View.GONE }
                collectionName.text=data.name.toString()
                root.setOnClickListener {
                    clickListener.invoke(data)
                }
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