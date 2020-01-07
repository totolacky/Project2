package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TestBufferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var nextIntent = Intent(this, ChatRoomActivity::class.java)
        nextIntent.putExtra("myId", "5e139c8aaf8d393ace9217ba")
        nextIntent.putExtra("chatroomId","5e139cb3af8d393ace9217c7")


        startActivity(nextIntent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}