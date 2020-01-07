package com.example.myapplication

import android.app.Fragment
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.Retrofit.MyService
import com.example.myapplication.ui.main.GalleryFragment
import com.example.myapplication.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.app.FragmentManager
import android.app.FragmentTransaction


class MainActivity : AppCompatActivity() {

    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 넘겨준 intent로 현재 user의 id 받아오기
        userId = getIntent().getStringExtra("id")
        Log.d("MainActivity","get intent : "+userId)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager, userId)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = this.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}