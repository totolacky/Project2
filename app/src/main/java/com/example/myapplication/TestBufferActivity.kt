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
        nextIntent.putExtra("id", "5e14c37d7504b44b68b80120")
        nextIntent.putExtra("chatroomId","5e139cb3af8d393ace9217c7")

        startActivity(nextIntent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}