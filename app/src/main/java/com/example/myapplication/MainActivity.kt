package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.ui.main.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayout


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
        viewPager.offscreenPageLimit = 5
        val tabs: TabLayout = this.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)?.setIcon(R.drawable.ic_contact)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_chat)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_photo)


    }
}