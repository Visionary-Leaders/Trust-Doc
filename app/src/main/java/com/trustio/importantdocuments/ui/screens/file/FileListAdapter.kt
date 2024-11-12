package com.trustio.importantdocuments.ui.screens.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.databinding.FileItemBinding
import com.trustio.importantdocuments.utils.convertBytesToMb

class FileListAdapter :RecyclerView.Adapter<FileListAdapter.FileListVh>() {
    private val list= ArrayList<FileItem>()
    inner class FileListVh(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun onBind(data:FileItem){
            binding.apply {
                fileTitle.text=data.file_name
                fileType.text="File Type: ${data.file_type}"
                fileSize.text = "%.2f MB".format(data.file_size.convertBytesToMb())
                when(data.file_type){

                    "jpg" -> {
                        binding.fileImg.setImageResource(R.drawable.img_small_ic)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_yellow))
                    }
                    "png" ->{

                        binding.fileImg.setImageResource(R.drawable.img_small_ic)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_yellow))
                    }
                    "pdf" ->{

                        binding.fileImg.setImageResource(R.drawable.ic_doc_small)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_green))
                    }
                    "application/pdf" -> {

                        binding.fileImg.setImageResource(R.drawable.ic_doc_small)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_green))
                    }
                    else ->{

                        binding.fileImg.setImageResource(R.drawable.nothing_ic)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_hidden_bg))
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListVh {
        return FileListVh(FileItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun submitList(newList: List<FileItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FileListVh, position: Int) {
        holder.onBind(list[position])
    }
}