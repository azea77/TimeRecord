package com.example.timerecord.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timerecord.R

class TagAdapter(
    private var tagList: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private var selectedPosition = -1
    private var onTagClickListener: ((String) -> Unit)? = null

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTextView: TextView = itemView.findViewById(R.id.tag_text_view)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position
                    notifyDataSetChanged()
                    onTagClickListener?.invoke(tagList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_item, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]
        holder.tagTextView.text = tag

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.purple_200))
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(android.R.color.background_dark))
        }
    }

    override fun getItemCount() = tagList.size

    fun addTag(tag: String) {
        if (!tagList.contains(tag)) {
            tagList.add(tag)
            notifyItemInserted(tagList.size - 1)
        }
    }

    fun removeSelectedTag() {
        if (selectedPosition != -1) {
            val position = selectedPosition
            tagList.removeAt(position)
            selectedPosition = -1
            notifyItemRemoved(position)
        }
    }

    fun getSelectedTag(): String? {
        return if (selectedPosition != -1 && selectedPosition < tagList.size) {
            tagList[selectedPosition]
        } else {
            null
        }
    }

    fun updateTags(newTags: List<String>) {
        tagList.clear()
        tagList.addAll(newTags)
        selectedPosition = -1
        notifyDataSetChanged()
    }

    fun setOnTagClickListener(listener: (String) -> Unit) {
        onTagClickListener = listener
    }
}