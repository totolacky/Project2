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
import kotlin.concurrent.thread

class DbInitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        testData()
    }

    // db에 테스트 계정들 올리기
    fun testData(){

        var testPhotoStrings1: ArrayList<String> = ArrayList()
        for(i in 1..9) {
            var imageStr = "image0" + i
            var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
            var resString = Util.getStringFromBitmap(resBitmap)
            testPhotoStrings1.add(resString!!)
        }

        var testPhotoStrings2: ArrayList<String> = ArrayList()
        for(i in 10..20) {
            var imageStr = "image" + i
            var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
            var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
            var resString = Util.getStringFromBitmap(resBitmap)
            testPhotoStrings2.add(resString!!)
        }

        var testFriendIds1: ArrayList<String> = ArrayList()

        var testFriendIds2: ArrayList<String> = ArrayList()

        var testUsers: ArrayList<ContactData> = ArrayList()
        testUsers.add(ContactData("","0","Tom","",1,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","1","Mike","feel like Mike",2,"",testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","2","Henry","feel like Henry",0,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","3","Alice","feel sooooo Alice",1,"",testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","4","Julia","hehe",1,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","5","Daniel","",2,"",testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","6","Steve","",1,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","7","Sophie","hi",0,"",testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","8","Timothy","heyy",2,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","9","Julien","yo",1,"",testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","10","Kevin","hehehehehe",1,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","11","Jake","",2,"",testPhotoStrings2,ArrayList(),ArrayList(),ArrayList()))
        testUsers.add(ContactData("","12","Jane","",0,"",testPhotoStrings1,ArrayList(),ArrayList(),ArrayList()))

        var tmpThread: Thread

        for(i in 0..12) {
            tmpThread = thread(start = true){
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myService: MyService = retrofit.create(MyService::class.java)

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
                ).execute()
//                    enqueue(object : Callback<String> {
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        Log.e("test register", t.message)
//                    }
//
//                    override fun onResponse(call: Call<String>, response: Response<String>) {
//                        Log.d("test id", response.body())
//                    }
//                })
            }
            tmpThread.join()
        }

        for (i in 0..12) {
            for (j in 0..12) {
                tmpThread = thread(start = true){
                    var retrofit = Retrofit.Builder()
                        .baseUrl(Config.serverUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    var myService: MyService = retrofit.create(MyService::class.java)

                    myService.addFriendFb(""+i,""+j).execute()
                }
                tmpThread.join()
            }
        }
    }
}