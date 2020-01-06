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
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)


        // 테스트 계정 올리기
        //testData()

        // id를 각 fragment에 전송
        /*var bundle: Bundle = Bundle()
        bundle.putString("id",userId)
        if(bundle!=null) {
            Log.d("MainActivity","send bundle (not null)")
            var fragment: GalleryFragment = GalleryFragment()
            fragment.setArguments(bundle)

            var fragmentManager: FragmentManager = getFragmentManager()
            var fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.fragment_gallery, fragment)
            fragmentTransaction.commit()
        }*/

    }


    // db에 테스트 계정들 올리기
    fun testData(){

        var testPhotoStrings1: ArrayList<String> = ArrayList()
        for(i in 1..5) {
            var imageStr = "image0" + i
            var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
            var resString = Util.getStringFromBitmap(resBitmap)
            testPhotoStrings1.add(resString!!)
        }

        var testPhotoStrings2: ArrayList<String> = ArrayList()
        for(i in 10..15) {
            var imageStr = "image" + i
            var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
            var resString = Util.getStringFromBitmap(resBitmap)
            testPhotoStrings2.add(resString!!)
        }

        var testFriendIds1: ArrayList<String> = ArrayList()
        for(i in 0..12 step 2) testFriendIds1.add(i.toString())

        var testFriendIds2: ArrayList<String> = ArrayList()
        for(i in 1..12 step 2) testFriendIds2.add(i.toString())

        var testUsers: ArrayList<ContactData> = ArrayList()
        testUsers.add(ContactData("0","Tom",null,0,null,testPhotoStrings1,testFriendIds2,null))
        testUsers.add(ContactData("1","Mike","feel like Mike",0,null,testPhotoStrings2,testFriendIds1,null))
        testUsers.add(ContactData("2","Henry","feel like Henry",0,null,testPhotoStrings1,testFriendIds2,null))
        testUsers.add(ContactData("3","Alice","feel sooooo Alice",0,null,testPhotoStrings2,testFriendIds1,null))
        testUsers.add(ContactData("4","Julia","hehe",0,null,testPhotoStrings1,testFriendIds2,null))
        testUsers.add(ContactData("5","Daniel",null,0,null,testPhotoStrings2,testFriendIds1,null))
        testUsers.add(ContactData("6","Steve",null,0,null,testPhotoStrings1,testFriendIds2,null))
        testUsers.add(ContactData("7","Sophie","hi",0,null,testPhotoStrings2,testFriendIds1,null))
        testUsers.add(ContactData("8","Timothy","heyy",0,null,testPhotoStrings1,testFriendIds2,null))
        testUsers.add(ContactData("9","Julien","yo",0,null,testPhotoStrings2,testFriendIds1,null))
        testUsers.add(ContactData("10","Kevin","hehehehehe",0,null,testPhotoStrings1,testFriendIds2,null))
        testUsers.add(ContactData("11","Jake",null,0,null,testPhotoStrings2,testFriendIds1,null))
        testUsers.add(ContactData("12","Jane","null?",0,null,testPhotoStrings1,testFriendIds2,null))


        var retrofit = Retrofit.Builder()
            .baseUrl(Config.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var myService: MyService = retrofit.create(MyService::class.java)

        for(i in 0..12) {
            var contactData = testUsers[i]
            myService.register(
                contactData.facebookId,
                contactData.name,
                contactData.status,
                contactData.country_code,
                contactData.profile_photo,
                contactData.photos,
                contactData.friends,
                contactData.hashtag
            ).enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("test register", t.message)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("test id", response.body())
                }
            })
        }
    }




}