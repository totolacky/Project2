package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.text.SimpleDateFormat
import java.util.*


class ChatRoomActivity: AppCompatActivity() {
    internal lateinit var preferences: SharedPreferences
    private lateinit var chating_Text: EditText
    private lateinit var chat_Send_Button: Button

    //리사이클러뷰
    var arrayList = ArrayList<ChatModel>()
    val mAdapter = ChatAdapter(this, arrayList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)


        //어댑터 선언
        chat_recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌

        chat_Send_Button = findViewById(R.id.chat_send_button)
        chating_Text = findViewById(R.id.editText)



        chat_Send_Button.setOnClickListener {
            //아이템 추가 부분
            sendMessage()

        }
    }


    fun sendMessage() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것 밑의 yyyy-MM-dd는 그냥 20xx년 xx월 xx일만 나오게 하는 식
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val getTime = sdf.format(date)


        //example에는 원래는 이미지 url이 들어가야할 자리
        val item = ChatModel(preferences.getString("name",""),chating_Text.text.toString(),"example", getTime)
        mAdapter.addItem(item)
        mAdapter.notifyDataSetChanged()

        //채팅 입력창 초기화
        chating_Text.setText("")
    }

}