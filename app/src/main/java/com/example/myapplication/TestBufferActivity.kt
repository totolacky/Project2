package com.example.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.MyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestBufferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testData()
    }

    // db에 테스트 계정들 올리기
    fun testData(){

        var testPhotoStrings1: ArrayList<String> = ArrayList()
        for(i in 1..3) {
            var imageStr = "image0" + i
            var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
            var resString = Util.getStringFromBitmap(resBitmap)
            testPhotoStrings1.add(resString!!)
        }

        var testPhotoStrings2: ArrayList<String> = ArrayList()
        for(i in 10..13) {
            var imageStr = "image" + i
            var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
            var resString = Util.getStringFromBitmap(resBitmap)
            testPhotoStrings2.add(resString!!)
        }

        var testFriendIds1: ArrayList<String> = ArrayList()

        var testFriendIds2: ArrayList<String> = ArrayList()

        var testUsers: ArrayList<ContactData> = ArrayList()
        testUsers.add(ContactData("0","Tom",null,0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("1","Mike","feel like Mike",0,null,testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("2","Henry","feel like Henry",0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("3","Alice","feel sooooo Alice",0,null,testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("4","Julia","hehe",0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("5","Daniel",null,0,null,testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("6","Steve",null,0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("7","Sophie","hi",0,null,testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("8","Timothy","heyy",0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("9","Julien","yo",0,null,testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("10","Kevin","hehehehehe",0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("11","Jake",null,0,null,testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("12","Jane","null?",0,null,testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))

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
                contactData.hashtag,
                contactData.chatroom
            ).enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e("test register", t.message)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("test id", response.body())
                }
            })
        }

        for (i in 0..12) {
            for (j in 0..12) {
                myService.addFriendFb(""+i,""+j).enqueue(object: Callback<String> {
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
}