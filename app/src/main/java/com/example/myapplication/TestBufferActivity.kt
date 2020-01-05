package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TestBufferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nextIntent = Intent(this, SignupActivity::class.java)
        nextIntent.putExtra("pageNum",0)
        nextIntent.putExtra("contactData",ContactData())

        startActivity(nextIntent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_in_left)
        finish()
    }
}