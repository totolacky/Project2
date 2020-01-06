package com.example.myapplication

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.myapplication.ui.main.SectionsPagerAdapter
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
//        val fab: FloatingActionButton = findViewById(R.id.fab)
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own fgssaction", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }


        testData()


    }



    // db에 테스트 계정들 올리기
    fun testData(){
        ContactData("0","Tom",null,0,null,null,null,null)
        ContactData("1","Mike","feel like Mike",0,null,null,null,null)
        ContactData("2","Henry","feel like Henry",0,null,null,null,null)
        ContactData("3","Alice","feel sooooo Alice",0,null,null,null,null)
        ContactData("4","Julia","hehe",0,null,null,null,null)
        ContactData("5","Daniel",null,0,null,null,null,null)
        ContactData("6","Steve",null,0,null,null,null,null)
        ContactData("7","Sophie","hi",0,null,null,null,null)
        ContactData("8","Timothy","heyy",0,null,null,null,null)
        ContactData("9","Julien","yo",0,null,null,null,null)
        ContactData("10","Kevin","hehehehehe",0,null,null,null,null)
        ContactData("11","Jake",null,0,null,null,null,null)
        ContactData("12","Jane","null?",0,null,null,null,null)
    }




}