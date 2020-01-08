package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Retrofit.MyService
import com.example.myapplication.ui.main.HashtagAdapter
import kotlinx.android.synthetic.main.activity_signup_0.*
import kotlinx.android.synthetic.main.activity_signup_0.nextButton
import kotlinx.android.synthetic.main.activity_signup_1.*
import kotlinx.android.synthetic.main.activity_signup_2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import kotlin.concurrent.thread

class SignupActivity : AppCompatActivity() {

    private val IMG_REQUEST_CODE = 10

    private val maxPagenum = 3
    var pageNum = 0  // checks which registration page the user is in
    lateinit var contactData: ContactData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pageNum = intent.getIntExtra("pageNum",0)
        contactData = intent.getSerializableExtra("contactData") as ContactData

        setContentView(R.layout.activity_signup_0)

        when (pageNum) {
            0 -> onCreateP0()
            1 -> onCreateP1()
            2 -> onCreateP2()
            else -> onCreateP0()
        }
    }

    fun onCreateP0() {
        setContentView(R.layout.activity_signup_0)

        nameEditText.text = Editable.Factory.getInstance().newEditable(contactData.name)

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent,IMG_REQUEST_CODE)
        }

        select_language.setOnClickListener {
            //Toast.makeText(applicationContext, "Select_language clicked",Toast.LENGTH_LONG).show()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Where are you from?")

            val defaultLang = resources.getString(R.string.default_lang)
            val languages = arrayOf("Others","Korea","America","Russia","Thailand","Germany","China","Saudi Arabia","Netherlands","Czechia","Swiss")
            builder.setItems(languages) { dialog, which ->
                when (which) {
                    0 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_0))
                        language_text.text = "Others"
                    }
                    1 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_1))
                        language_text.text = "Korea"
                    }
                    2 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_2))
                        language_text.text = "America"
                    }
                    3 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_3))
                        language_text.text = "Russia"
                    }
                    4 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_4))
                        language_text.text = "Thailand"
                    }
                    5 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_5))
                        language_text.text = "Germany"
                    }
                    6 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_6))
                        language_text.text = "China"
                    }
                    7 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_7))
                        language_text.text = "Saudi Arabia"
                    }
                    8 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_8))
                        language_text.text = "Netherlands"
                    }
                    9 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_9))
                        language_text.text = "Czechia"
                    }
                    10 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_10))
                        language_text.text = "Swiss"
                    }
                }
                contactData.country_code = which
            }

            builder.create().show()
        }
        nextButton.setOnClickListener {
            //Toast.makeText(applicationContext, "Next button clicked",Toast.LENGTH_LONG).show()
            contactData.name = nameEditText.text.toString()
            moveOn()
        }
    }

    fun onCreateP1() {
        setContentView(R.layout.activity_signup_1)

        nextButton.setOnClickListener {
            //Toast.makeText(applicationContext, "Next button clicked",Toast.LENGTH_LONG).show()
            contactData.status = statusEditText.text.toString()
            moveOn()
        }
    }

    fun onCreateP2() {
        setContentView(R.layout.activity_signup_2)

        val hashtagList: ArrayList<String?>? = ArrayList()

        addHashtagButton.setOnClickListener {

            val addedHashtag = hashtagEditText.text.toString()
            hashtagEditText.setText("")
            hashtagList?.add(addedHashtag)

            // onClick 설정
            val mAdapter = HashtagAdapter(this, hashtagList) { prof ->
                //Toast.makeText(this,"clicked",Toast.LENGTH_LONG).show()
                // view가 click되었을 때 실행할 것들
            }

            hRecyclerView.adapter = mAdapter

            val lm = LinearLayoutManager(this)
            hRecyclerView.layoutManager = lm
            hRecyclerView.setHasFixedSize(true)
        }

        nextButton.setOnClickListener {
            //Toast.makeText(applicationContext, "Finish button clicked",Toast.LENGTH_LONG).show()
            contactData.hashtag = hashtagList as ArrayList<String>
            moveOn()
        }
    }

    private fun moveOn() {
        pageNum += 1

        val nextIntent: Intent

        if (pageNum != maxPagenum){
            // Pass data to next signup activity
            nextIntent = Intent(this, SignupActivity::class.java)
            nextIntent.putExtra("pageNum",pageNum)
            nextIntent.putExtra("contactData",contactData)
        } else {
            // Register account in server
            var id: String? = null

            var retrofit = Retrofit.Builder()
                .baseUrl(Config.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            thread(start = true){
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myService: MyService = retrofit.create(MyService::class.java)

                id = myService.register(
                    contactData.facebookId,
                    contactData.name,
                    contactData.status,
                    contactData.country_code,
                    contactData.profile_photo,
                    contactData.photos,
                    contactData.friends,
                    contactData.hashtag,
                    contactData.chatroom
                ).execute().body()
            }.join()

            // Set next activity
            nextIntent = Intent(this, MainActivity::class.java)
            nextIntent.putExtra("id",id)
        }

        startActivity(nextIntent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && data.data != null) {
            //profileImage.setImageURI(data.data)
            val bm = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            profileImage.setImageBitmap(bm)
            contactData.profile_photo = when (bm) {
                null -> ""
                else -> Util.getStringFromBitmap(bm)!!
            }
        }
    }
}