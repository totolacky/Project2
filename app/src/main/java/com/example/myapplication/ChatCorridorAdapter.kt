package com.example.myapplication.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ChatroomData
import com.example.myapplication.R
import com.example.myapplication.Util

class ChatCorridorAdapter(val context: Context, val chatroomList: ArrayList<ChatroomData?>?, var userId: String, val itemClick: (ChatroomData) -> Unit) :
    RecyclerView.Adapter<ChatCorridorAdapter.Holder>() {

    val id = userId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_chatroom, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        if(chatroomList == null)
            return -1
        return chatroomList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val listElem : ChatroomData? = chatroomList?.get(position)
        if(listElem != null) {
            holder.bind(listElem, context)
        }
    }

    inner class Holder(itemView: View, itemClick: (ChatroomData) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val chatroom_image = itemView.findViewById<ImageView>(R.id.chatroom_image)
        val chatroom_name = itemView.findViewById<TextView>(R.id.chatroom_name)
        val chatroom_lastchat = itemView.findViewById<TextView>(R.id.chatroom_lastchat)

        fun bind (prof: ChatroomData, context: Context) {
            var people = prof.people

            // 톡방 이름, 마지막 대화 설정
            if (prof.chatroom_name == "") {
                for (i in 0 until people.size) {
                    if (people[i].equals(id))
                        continue
                    prof.chatroom_name += Util.getNameFromId(people[i])+" "
                }
            }
            chatroom_name?.text = prof.chatroom_name
            chatroom_lastchat?.text = prof.last_chat

            // 프로필 사진 설정
            var chatroom_photo= BitmapFactory.decodeResource(context.resources,R.drawable.flag_0)
            if (prof.chatroom_image != "") {
                chatroom_photo = Util.getBitmapFromString(prof.chatroom_image!!)!!
            } else {
                for (i in 0 until people.size) {
                    if (!people[i].equals(id)){
                        chatroom_photo = Util.getProfileImageFromId(people[i])
                        break
                    }
                }
            }
            chatroom_image.setImageBitmap(Util.resizingBitmap(Util.squareBitmap(chatroom_photo),120))

            // OnClickListener 설정
            itemView.setOnClickListener{itemClick(prof)}
        }
    }

}