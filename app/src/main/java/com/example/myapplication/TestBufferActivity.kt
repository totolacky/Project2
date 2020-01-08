package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class TestBufferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Log.d("bitmap",Util.getStringFromBitmap(BitmapFactory.decodeResource(resources,R.drawable.def_icon)))

        var nextIntent = Intent(this, MainActivity::class.java)
        val myId = "5e15331e0d7cd152d7181002"
        val fId = "5e1532c20d7cd152d7180ff7"
        nextIntent.putExtra("id", fId)
        //nextIntent.putExtra("chatroomId","5e139cb3af8d393ace9217c7")

        startActivity(nextIntent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}