package com.trustio.importantdocuments.ui.screens.search

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.R
import com.trustio.importantdocuments.app.App
import com.trustio.importantdocuments.data.local.room.entity.Bookmark
import com.trustio.importantdocuments.databinding.FileItemBinding
import com.trustio.importantdocuments.utils.checkFileType
import com.trustio.importantdocuments.utils.convertBytesToMb
class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchVh>() {
    private val fileList = ArrayList<Bookmark>()
    private lateinit var listener: (Bookmark) -> Unit
    private var query: String = ""

    fun setItemClickListener(listener:(Bookmark) -> Unit){
        this.listener = listener
    }

    // submitList yangilash
    fun submitList(newList: List<Bookmark>, query: String = "") {
        this.query = query // queryni saqlaymiz
        fileList.clear()
        fileList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class SearchVh(private val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(item: Bookmark) {
            binding.apply {
                binding.fileTitle.isSelected = true

                // highlightText funksiyasiga query ni berish
                val nameSpan = item.fileName.highlightText(query)

                fileTitle.text = nameSpan
                fileType.text = "Section: ${item.sectionName}".highlightText(query)
                fileSize.text = "%.2f MB".format(item.fileSize.convertBytesToMb())
                checkFileType(item, binding)

                root.setOnClickListener {
                    listener(item)
                }
            }
        }
    }

    // highlightText methodini queryga qarab spannable qilish
    fun String.highlightText(query: String): SpannableString {
        val spannable = SpannableString(this)

        if (query.isNotEmpty()) {
            var start = 0
            while (start < this.length) {
                start = this.indexOf(query, start, ignoreCase = true)
                if (start == -1) break // Agar query topilmasa, hech narsa qilmaymiz
                val end = start + query.length

                // Rangni qo'shish
                spannable.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(App.currentContext()!!, R.color.violet_400)
                    ),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Qalin qilish
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                start = end // Keyingi topiladigan joyni izlash
            }
        }

        return spannable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchVh {
        return SearchVh(FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: SearchVh, position: Int) {
        holder.onBind(fileList[position])
    }
}
