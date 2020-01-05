package com.example.myapplication.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R


class HashtagAdapter(val context: Context, val hashtagList: ArrayList<String?>?, val itemClick: (String) -> Unit) :
    RecyclerView.Adapter<HashtagAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_hashtag_block, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        if(hashtagList == null)
            return -1
        return hashtagList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val listElem : String? = hashtagList?.get(position)
        if(listElem != null) {
            holder.bind(listElem, context)
        }
    }

    inner class Holder(itemView: View, itemClick: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {

        val hashtagText = itemView.findViewById<TextView>(R.id.hashtagText)

        fun bind (tag: String, context: Context) {
            // 이름, 상태 메시지 설정
            hashtagText.text = "# $tag"
            itemView.setOnClickListener{itemClick(tag)}
        }
    }

}