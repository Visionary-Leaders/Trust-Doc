package com.trustio.importantdocuments.ui.screens.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.databinding.FileItemBinding
import com.trustio.importantdocuments.utils.checkFileType
import com.trustio.importantdocuments.utils.convertBytesToMb

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchVh>() {
    private val fileList = ArrayList<Bookmark>()
    private lateinit var listener: (Bookmark) -> Unit

    fun setItemClickListener(listener:(Bookmark) -> Unit){
        this.listener=listener
    }
    inner class SearchVh(private val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: Bookmark) {
            binding.apply {
                fileTitle.text = item.fileName
                fileType.text = "Section: ${item.sectionName}"
                fileSize.text = "%.2f MB".format(item.fileSize.convertBytesToMb())
                checkFileType(item,binding)
                root.setOnClickListener {
                    listener(item)
                }  }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVh {
        return SearchVh(FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun submitList(newList: List<Bookmark>) {
        fileList.clear()
        fileList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: SearchVh, position: Int) {
        holder.onBind(fileList[position])
    }
}