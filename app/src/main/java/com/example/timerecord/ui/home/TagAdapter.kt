package com.example.timerecord.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timerecord.R

class TagAdapter(private val tagList: MutableList<String>) :
        RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

private var selectedPosition = -1

inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tagTextView: TextView = itemView.findViewById(R.id.tag_text_view)

    init {
        itemView.setOnClickListener {
            selectedPosition = adapterPosition
            notifyDataSetChanged()
        }
    }
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
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

override fun getItemCount(): Int {
    return tagList.size
}

fun getSelectedPosition(): Int {
    return selectedPosition
}

fun removeSelectedTag() {
    if (selectedPosition != -1) {
        tagList.removeAt(selectedPosition)
        selectedPosition = -1
        notifyDataSetChanged()
    }
}

fun addTag(tag: String) {
    tagList.add(tag)
    notifyDataSetChanged()
}
}