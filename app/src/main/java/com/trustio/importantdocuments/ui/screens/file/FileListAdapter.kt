package com.trustio.importantdocuments.ui.screens.file

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.data.remote.response.file.FileItem
import com.trustio.importantdocuments.databinding.FileItemBinding
import com.trustio.importantdocuments.databinding.FileItemMiddleBinding
import com.trustio.importantdocuments.utils.convertBytesToMb
import com.trustio.importantdocuments.utils.customIntToString
import com.trustio.importantdocuments.utils.setAnimation

class FileListAdapter(var type: Int,val bookmarkList:ArrayList<Bookmark>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val list = ArrayList<FileItem>()
    private lateinit var listener: (FileItem) -> Unit
    private lateinit var menuListener : (FileItem) -> Unit
    fun setMeuClickListener(listener:(FileItem) -> Unit){
        this.menuListener=listener
    }
    fun setItemClickListener(listener:(FileItem) -> Unit){
        this.listener=listener
    }
    inner class FileListVh(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(data: FileItem) {
            binding.apply {
                fileTitle.text = data.file_name
                if (data.section.toString().startsWith("2456")) {
                    fileType .text = customIntToString(data.section)
                }
                fileSize.text = "%.2f MB".format(data.file_size.convertBytesToMb())
                when (data.file_type) {

                    "jpg" -> {
                        binding.fileImg.setImageResource(R.drawable.img_small_ic)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_yellow))
                    }

                    "png" -> {

                        binding.fileImg.setImageResource(R.drawable.img_small_ic)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_yellow))
                    }

                    "pdf" -> {

                        binding.fileImg.setImageResource(R.drawable.ic_doc_small)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_green))
                    }

                    "application/pdf" -> {

                        binding.fileImg.setImageResource(R.drawable.ic_doc_small)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_img_bg_green))
                    }

                    else -> {

                        binding.fileImg.setImageResource(R.drawable.nothing_ic)
                        binding.circleBg.setCardBackgroundColor(root.context.getColor(R.color.color_hidden_bg))
                    }
                }
                root.setOnClickListener {
                    listener(data)
                }
                dotsMore.setOnClickListener {
                    menuListener(data)
                }

            }
        }
    }

    inner class FileMediaVh(val binding: FileItemMiddleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: FileItem) {
            binding.apply {
                fileTitle.text =
                    "${data.file_name} - %.1f MB".format(data.file_size.convertBytesToMb())

                when (data.file_type) {
                    "pdf" -> {
                        fileImg.setImageResource(R.drawable.ic_doc)
                    }

                    "jpg" -> {
                        fileImg.setImageResource(R.drawable.ic_image)
                    }

                    "png" -> {
                        fileImg.setImageResource(R.drawable.ic_image)
                    }

                    "application/pdf" -> {
                        fileImg.setImageResource(R.drawable.ic_doc)

                    }

                    else -> {

                    }
                }
                dotsMore.setOnClickListener {
                    menuListener(data)
                }

                root.setOnClickListener {
                    listener(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            0 -> {
                FileListVh(
                    FileItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                FileMediaVh(
                    FileItemMiddleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    fun submitList(newList: List<FileItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return type // 0 yoki 1 qiymati `type`ga asoslangan holda qaytadi
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            0 -> {
                val b = (holder as FileListVh)
                val binding = (holder as FileListVh).binding
//                setAnimation(holder.itemView.context, binding.root)

                b.onBind(list[position])
            }

            else -> {
                val b = (holder as FileMediaVh)
                val binding = holder.binding
//                setAnimation(holder.itemView.context, binding.root)
                b.onBind(list[position])
            }
        }
    }
}