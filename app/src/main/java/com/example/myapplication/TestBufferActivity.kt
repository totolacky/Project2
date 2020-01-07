package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TestBufferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var nextIntent = Intent(this, MainActivity::class.java)
        nextIntent.putExtra("id", "5e14511f8688fa47667cba36")
        nextIntent.putExtra("chatroomId","5e139cb3af8d393ace9217c7")

        startActivity(nextIntent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}