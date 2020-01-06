package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Util.squareBitmap
import com.example.myapplication.ui.main.HashtagAdapter
import kotlinx.android.synthetic.main.activity_signup_0.*
import kotlinx.android.synthetic.main.activity_signup_0.nextButton
import kotlinx.android.synthetic.main.activity_signup_1.*
import kotlinx.android.synthetic.main.activity_signup_2.*
import java.io.Serializable


class SignupActivity : AppCompatActivity() {

    private val IMG_REQUEST_CODE = 10

    private val maxPagenum = 3
    var pageNum = 0  // checks which registration page the user is in
    lateinit var contactData: Serializable

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

        nameEditText.text = Editable.Factory.getInstance().newEditable((contactData as ContactData).name)

        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent,IMG_REQUEST_CODE)
        }

        select_language.setOnClickListener {
            Toast.makeText(applicationContext, "Select_language clicked",Toast.LENGTH_LONG).show()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Which language do you use?")

            val defaultLang = resources.getString(R.string.default_lang)
            val languages = arrayOf(defaultLang,"Korean","English")
            builder.setItems(languages) { dialog, which ->
                when (which) {
                    0 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_0))
                        language_text.text = "Unknown"
                        (contactData as ContactData).country_code = 0
                    }
                    1 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_1))
                        language_text.text = "Korean"
                        (contactData as ContactData).country_code = 1
                    }
                    2 -> {
                        flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_2))
                        language_text.text = "English"
                        (contactData as ContactData).country_code = 2
                    }
                }
            }

            builder.create().show()
        }
        nextButton.setOnClickListener {
            Toast.makeText(applicationContext, "Next button clicked",Toast.LENGTH_LONG).show()
            (contactData as ContactData).name = nameEditText.text.toString()
            moveOn()
        }
    }

    fun onCreateP1() {
        setContentView(R.layout.activity_signup_1)

        nextButton.setOnClickListener {
            Toast.makeText(applicationContext, "Next button clicked",Toast.LENGTH_LONG).show()
            (contactData as ContactData).status = statusEditText.text.toString()
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
                Toast.makeText(this,"clicked",Toast.LENGTH_LONG).show()
                // view가 click되었을 때 실행할 것들
            }

            hRecyclerView.adapter = mAdapter

            val lm = LinearLayoutManager(this)
            hRecyclerView.layoutManager = lm
            hRecyclerView.setHasFixedSize(true)
        }

        nextButton.setOnClickListener {
            Toast.makeText(applicationContext, "Finish button clicked",Toast.LENGTH_LONG).show()
            (contactData as ContactData).hashtag = hashtagList as ArrayList<String>
            moveOn()
        }
    }

    private fun moveOn() {
        pageNum += 1

        if (pageNum == maxPagenum){
            val nextIntent = Intent(this, MainActivity::class.java)
            nextIntent.putExtra("contactData",contactData)

            startActivity(nextIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            val nextIntent = Intent(this, SignupActivity::class.java)
            nextIntent.putExtra("pageNum",pageNum)
            nextIntent.putExtra("contactData",contactData)

            startActivity(nextIntent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.slide_in_left)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && data.data != null) {
            //profileImage.setImageURI(data.data)
            val bm = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
            profileImage.setImageBitmap(bm)
            (contactData as ContactData).profile_photo = Util.getStringFromBitmap(bm)
        }
    }
}