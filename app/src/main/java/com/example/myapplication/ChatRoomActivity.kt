package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Retrofit.MyService
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat_room.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class ChatRoomActivity: AppCompatActivity() {

    // Id setup
    var myId = ""
    var chatroomId = ""

    // RecyclerView setup
    lateinit var chatroomData: ChatroomData
    var arrayList: ArrayList<ChatData> = ArrayList()
    lateinit var cAdapter : ChatAdapter
    var prof_images: HashMap<String, Pair<String,Bitmap>> = HashMap()

    // Socket setup
    private lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        // TODO: ChatRoomActivity 호출 시 이 두 가지 제공할 것
        myId = intent.getStringExtra("myId")
        chatroomId = intent.getStringExtra("chatroomId")

        initChatroom()

        //어댑터 선언
        cAdapter = ChatAdapter(this, arrayList,prof_images,myId)
        chat_recyclerview.adapter = cAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌

        connectSocket()

        chat_send_button.setOnClickListener {
            sendMessage()
        }
    }

    fun initChatroom() {
        var retrofit = Retrofit.Builder()
            .baseUrl(Config.serverUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var threads: ArrayList<Thread> = ArrayList()

        // Get chatroomData
        threads.add(thread(start = true){
            var myService: MyService = retrofit.create(MyService::class.java)
            var result_body = myService.getChatroom(chatroomId).execute().body()

            if(result_body == null) {
                Log.d("ChatRoomActivity","[Error] getChatroom result is null.")
                chatroomData = ChatroomData()
            } else {
                chatroomData = Util.getChatroomDataFramJson(result_body)
            }
        })

        // Get chat log
        threads.add(thread(start = true){
            var myService: MyService = retrofit.create(MyService::class.java)
            var result_body = myService.getChatLog(chatroomId).execute().body()

            if(result_body == null) {
                Log.d("ChatRoomActivity","[Error] getChatLog result is null.")
            } else {
                for (log in result_body){
                    arrayList.add(Util.getChatDataFramJson(log))
                }
            }
        })

        // Join threads
        for (t in threads) { t.join() }
        threads = ArrayList()

        // Set names and profile images
        Log.d("Argh",""+chatroomData.people)

        for (partId in chatroomData.people) {
            threads.add(thread(start = true){
                var myService: MyService = retrofit.create(MyService::class.java)
                var result_body = myService.getContactSimple(partId).execute().body()

                if(result_body == null) {
                    Log.d("ChatRoomActivity","[Error] cannot get profile of $partId")
                } else {
                    val contactData = Util.getContactDataFramSimpleJson(result_body)
                    val name = contactData.name
                    var prof_image = when (contactData.profile_photo) {
                        ""      -> BitmapFactory.decodeResource(resources,R.drawable.def_icon)
                        else    -> Util.getBitmapFromString(contactData.profile_photo)
                    }
                    prof_images.put(partId,Pair(name,prof_image!!))
                    Log.d("ChatRoomActivity","aaa")
                }
            })
        }

        // Join threads
        for (t in threads) { t.join() }
        threads = ArrayList()

        Log.d("ChatRoomActivity","+"+prof_images)
    }

    fun sendMessage() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것 밑의 yyyy-MM-dd는 그냥 20xx년 xx월 xx일만 나오게 하는 식
        val sdf = SimpleDateFormat("hh:mm")

        val getTime = sdf.format(date)

        //example에는 원래는 이미지 url이 들어가야할 자리
        val item = ChatData(myId,chat_edit_text.text.toString(),getTime)
        cAdapter.addItem(item)
        cAdapter.notifyDataSetChanged()
        //chat_recyclerview.scrollToPosition(RecyclerView.SCROLL_INDICATOR_END)

        //채팅 입력창 초기화
        chat_edit_text.setText("")

        // 서버에 전송
        var data = JSONObject()
        data.put("id",item.id)
        data.put("script",item.script)
        data.put("date_time",item.date_time)
        mSocket.emit("clientMessage",data)
    }

    /* -----------------------
     * Socket 통신 관련 함수들
     * ----------------------- */
    fun connectSocket() {
        try {
            mSocket = IO.socket(Config.serverUrl)
            mSocket.connect()
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            mSocket.on("serverMessage", onMessageReceived)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    // Socket서버에 connect 되면 발생하는 이벤트
    private val onConnect = Emitter.Listener {
        val data = JSONObject()
        data.put("myId",myId)
        data.put("chatroomId",chatroomId)
        data.put("people",JSONArray(chatroomData.people))
        mSocket!!.emit("clientConnect", data)
    }

    // 서버로부터 전달받은 'chat-message' Event 처리.
    private val onMessageReceived = Emitter.Listener { args ->
        // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
        try {
            val receivedData = args[0] as JSONObject
            Log.d("ChatRoomActivity", "message recieved - "+receivedData.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}










































/*
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat_room.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class ChatRoomActivity: AppCompatActivity() {
    internal lateinit var preferences: SharedPreferences
    private lateinit var chating_Text: EditText
    private lateinit var chat_Send_Button: Button

    private var hasConnection: Boolean = false
    private var thread2: Thread? = null
    private var startTyping = false
    private var time = 2

    //private var mSocket: Socket = IO.socket("http://b8d76a8d.ngrok.io/")
    private var mSocket: Socket = IO.socket(Config.serverUrl)

    //리사이클러뷰
    var arrayList = arrayListOf<ChatModel>()
    val mAdapter = ChatAdapter(this, arrayList)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        //어댑터 선언
        chat_recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)

        chat_Send_Button = findViewById<Button>(R.id.chat_send_button)
        chating_Text = findViewById<EditText>(R.id.editText)

        if (hasConnection) {

        } else {
            //소켓연결
            mSocket.connect()

            //서버에 신호 보내는거같음 밑에 에밋 리스너들 실행
            //socket.on은 수신
            mSocket.on("connect user", onNewUser)
            mSocket.on("chat message", onNewMessage)

            val userId = JSONObject()
            try {
                userId.put("username", preferences.getString("name", "") + " Connected")
                userId.put("roomName", "room_example")
                Log.e("username",preferences.getString("name", "") + " Connected")

                //socket.emit은 메세지 전송임
                mSocket.emit("connect user", userId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        hasConnection = true

        chat_Send_Button.setOnClickListener {
            //아이템 추가 부분
            sendMessage()
        }
    }

    internal var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            val script: String
            val profile_image: String
            val date_time: String
            try {
                Log.e("asdasd", data.toString())
                name = data.getString("name")
                script = data.getString("script")
                profile_image = data.getString("profile_image")
                date_time = data.getString("date_time")


                val format = ChatModel(name, script, profile_image, date_time)
                mAdapter.addItem(format)
                mAdapter.notifyDataSetChanged()
                Log.e("new me",name )
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    //어플 키자마자 서버에  connect user 하고 프로젝트에 on new user 실행
    internal var onNewUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val length = args.size

            if (length == 0) {
                return@Runnable
            }
            //Here i'm getting weird error..................///////run :1 and run: 0
            var username = args[0].toString()
            try {
                val `object` = JSONObject(username)
                username = `object`.getString("username")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        })
    }


    fun sendMessage() {
        preferences = getSharedPreferences("USERSIGN", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val getTime = sdf.format(date)

        val message = chating_Text.getText().toString().trim({ it <= ' ' })
        if (TextUtils.isEmpty(message)) {
            return
        }
        chating_Text.setText("")
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", preferences.getString("name", ""))
            jsonObject.put("script", message)
            jsonObject.put("profile_image", "example")
            jsonObject.put("date_time", getTime)
            jsonObject.put("roomName", "room_example")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("챗룸", "sendMessage: 1 " + mSocket.emit("chat message", jsonObject))
        Log.e("sendmmm",preferences.getString("name", "") )

    }

}














/*
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat_room.*
import java.text.SimpleDateFormat
import java.util.*


class ChatRoomActivity: AppCompatActivity() {
    internal lateinit var preferences: SharedPreferences
    private lateinit var chating_Text: EditText
    private lateinit var chat_Send_Button: Button

    //리사이클러뷰
    var arrayList = ArrayList<ChatModel>()
    val mAdapter = ChatAdapter(this, arrayList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        //어댑터 선언
        chat_recyclerview.adapter = mAdapter
        //레이아웃 매니저 선언
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)//아이템이 추가삭제될때 크기측면에서 오류 안나게 해줌

        chat_Send_Button = findViewById(R.id.chat_send_button)
        chating_Text = findViewById(R.id.editText)


        chat_Send_Button.setOnClickListener {
            //아이템 추가 부분
            sendMessage()
        }
    }


    fun sendMessage() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것 밑의 yyyy-MM-dd는 그냥 20xx년 xx월 xx일만 나오게 하는 식
        val sdf = SimpleDateFormat("hh:mm")

        val getTime = sdf.format(date)

        //example에는 원래는 이미지 url이 들어가야할 자리
        val item = ChatModel(preferences.getString("name",""),chating_Text.text.toString(),"example", getTime)
        mAdapter.addItem(item)
        mAdapter.notifyDataSetChanged()

        //채팅 입력창 초기화
        chating_Text.setText("")
    }

}*/