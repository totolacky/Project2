package com.example.myapplication.ui.main

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ContactData
import com.example.myapplication.R
import com.example.myapplication.Util

class ContactAdapter(val context: Context, val contactList: ArrayList<ContactData?>?, val itemClick: (ContactData) -> Unit) :
    RecyclerView.Adapter<ContactAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_contact_block, parent, false)
        return Holder(view, itemClick)
    }

    override fun getItemCount(): Int {
        if(contactList == null)
            return -1
        return contactList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val listElem : ContactData? = contactList?.get(position)
        if(listElem != null) {
            holder.bind(listElem, context)
        }
    }

    inner class Holder(itemView: View, itemClick: (ContactData) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val elem_name = itemView.findViewById<TextView>(R.id.elem_name)
        val elem_status = itemView.findViewById<TextView>(R.id.elem_status)
        val elem_photo = itemView.findViewById<ImageView>(R.id.elem_image)
        val elem_flag = itemView.findViewById<ImageView>(R.id.elem_flag)

        fun bind (prof: ContactData, context: Context) {
            // 이름, 상태 메시지 설정
            elem_name?.text = prof.name
            elem_status?.text = prof.status

            // 상태 메시지 없으면 이름 가운데로 옮기기
            if (prof.status == null) {
                elem_name.height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            // 국기 설정
            val flagId = "flag_"+prof.country_code
            val resId = context.resources.getIdentifier(flagId,"drawable",context.packageName)
            elem_flag?.setImageBitmap(BitmapFactory.decodeResource(context.resources,resId))

            // 프로필 사진 설정
            var prof_photo = BitmapFactory.decodeResource(context.resources,R.drawable.def_icon)
            if (prof.profile_photo != null) {
                prof_photo = prof.profile_photo
            }
            elem_photo.setImageBitmap(Util.resizingBitmap(Util.squareBitmap(prof_photo),120))

            itemView.setOnClickListener{itemClick(prof)}
        }
    }

}