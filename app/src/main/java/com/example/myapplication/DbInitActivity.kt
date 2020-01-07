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

    fun getPhotoString(i: String): ArrayList<String>{
        var testPhoto: ArrayList<String> = ArrayList()
        var imageStr = "userimage" + i
        var resID = getResources().getIdentifier(imageStr, "drawable", getPackageName())
        var resBitmap = BitmapFactory.decodeResource(getResources(), resID)
        var resString = Util.getStringFromBitmap(resBitmap)
        testPhoto.add(resString!!)
        return testPhoto
    }

    // db에 테스트 계정들 올리기
    fun testData(){

        var testUsers: ArrayList<ContactData> = ArrayList()

        testUsers.add(ContactData("","0","Jane","안녕하세요",
            1,"",getPhotoString("0"),ArrayList(),arrayListOf("HYU","Lion"),ArrayList()))
        testUsers.add(ContactData("","1","Tom","I love beer",
            5,"",getPhotoString("1"),ArrayList(),arrayListOf("Beer","Friends"),ArrayList()))
        testUsers.add(ContactData("","2","Mike","I am cute",
            2,"",getPhotoString("2"),ArrayList(),arrayListOf("Cuty"),ArrayList()))
        testUsers.add(ContactData("","3","Henry","я ненавижу сервер",
            3,"",getPhotoString("3"),ArrayList(),arrayListOf("Sky","Sunset"),ArrayList()))
        testUsers.add(ContactData("","4","Alice","ฉันเกลียดเซิร์ฟเวอร์",
            4,"",getPhotoString("4"),ArrayList(),arrayListOf("Palace"),ArrayList()))
        testUsers.add(ContactData("","5","Julia","guten Morgen",
            5,"",getPhotoString("5"),ArrayList(),arrayListOf("Flower"),ArrayList()))
        testUsers.add(ContactData("","6","Daniel","我讨厌服务器",
            6,"",getPhotoString("6"),ArrayList(),arrayListOf("Mountain","River","Trip"),ArrayList()))
        testUsers.add(ContactData("","7","Steve","صباح الخير",
            7,"",getPhotoString("7"),ArrayList(),arrayListOf("Cute"),ArrayList()))
        testUsers.add(ContactData("","8","Sophie","힙찔이",
            1,"",getPhotoString("8"),ArrayList(),arrayListOf("Hiphop"),ArrayList()))
        testUsers.add(ContactData("","9","Timothy","Merry Christmas",
            2,"",getPhotoString("9"),ArrayList(),arrayListOf("Christmas"),ArrayList()))
        testUsers.add(ContactData("","10","Julien","",
            9,"",getPhotoString("10"),ArrayList(),arrayListOf("Village","River"),ArrayList()))
        testUsers.add(ContactData("","11","Kevin","Beeeeeer",
            5,"",getPhotoString("11"),ArrayList(),arrayListOf("House","Unleitung"),ArrayList()))
        testUsers.add(ContactData("","12","Jake","I love Holland",
            8,"",getPhotoString("12"),ArrayList(),arrayListOf("Windmill","River"),ArrayList()))
        testUsers.add(ContactData("","13","Brian","I love snow",
            10,"",getPhotoString("13"),ArrayList(),arrayListOf("Snow","Toblerone"),ArrayList()))

        var tmpThread: Thread

        for(i in 0..13) {
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

        for (i in 0..13) {
            for (j in 0..13) {
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