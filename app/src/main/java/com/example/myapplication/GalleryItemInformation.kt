package com.example.myapplication


import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.MyService
import com.example.myapplication.ui.main.Global
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class GalleryItemInformation : AppCompatActivity() {

    private val TAG = "GalleryItemInformation"
    private var thisItem = GalleryItem(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var pos = intent.getIntExtra("POS", 0)
        thisItem = Global.myGalleryHolder.getDataById(pos)

        Log.d("thisItem",""+thisItem.userInfo)

        var myId = intent.getStringExtra("MYID")
        Log.d("gallry item info",myId)

        var userId = thisItem.userInfo!!._id

        var userPhoto : ImageView = findViewById(R.id.thumbnail)
        userPhoto.setImageBitmap(thisItem.image)

        var userName: TextView = findViewById(R.id.user_name)
        userName.text = thisItem.userInfo!!.name

        var userStatus: TextView = findViewById(R.id.user_status)
        userStatus.text = thisItem.userInfo!!.status

        var userProfile: ImageView = findViewById(R.id.user_image)
        userProfile.setImageBitmap(Util.getBitmapFromString(thisItem.userInfo!!.profile_photo))

        var userFlag: ImageView = findViewById(R.id.user_flag)
        val flagId = "flag_"+thisItem.userInfo!!.country_code
        val resId = applicationContext.resources.getIdentifier(flagId,"drawable",applicationContext.packageName)
        userFlag.setImageBitmap(BitmapFactory.decodeResource(applicationContext.resources,resId))

        val userHashtags: TextView = findViewById(R.id.hashtags)
        var hashtagsString = ""
        var hashtagsList = thisItem.userInfo!!.hashtag
        for(i in hashtagsList){
            hashtagsString += "   #"
            hashtagsString += i
        }
        userHashtags.text = hashtagsString


        val messageButton: ImageButton = findViewById(R.id.message)
        messageButton.setOnClickListener {
            Toast.makeText(getApplicationContext(), "You can send message to "+userName.text, Toast.LENGTH_SHORT).show()

            // 채팅방으로 옮기기
            var tmpThread = thread(start = true){
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myService: MyService = retrofit.create(MyService::class.java)

                Log.d("GalleryItemInformation","myId = "+myId+" userId = "+userId)

                var response = myService.createChatroom(myId, userId).execute()
                if(response.body()==null) Log.d("messageButton","response body null")
                else{
                    var chatroomId = response.body()
                    Log.d("messageButton",chatroomId)

                    val intent = Intent(this, ChatRoomActivity::class.java)
                    intent.putExtra("myId", myId)
                    intent.putExtra("chatroomId", chatroomId)
                    startActivity(intent)
                }
            }
            tmpThread.join()
        }

        val newFriendButton: ImageButton = findViewById(R.id.newfriend)
        newFriendButton.setOnClickListener {
            Toast.makeText(getApplicationContext(), "You become a friend with "+userName.text, Toast.LENGTH_SHORT).show()

            // 친구추가하기
            var tmpThread = thread(start = true){
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myService: MyService = retrofit.create(MyService::class.java)

                var response = myService.addFriend(myId, userId).execute()
                if(response.body()==null) Log.d("newFriendButton","response body null")
                else{
                    Log.d("newFriendButton",response.body())

                    // contact refresh????????????????
                }
            }
            tmpThread.join()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
