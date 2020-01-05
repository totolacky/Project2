package com.example.myapplication

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup_0.*

class SignupActivity : AppCompatActivity() {

    private val maxPagenum = 3

    // checks which registration page the user is in
    var pageNum = intent.getIntExtra("pageNum",0)
    var contactData: ContactData? = intent.getSerializableExtra("contactData") as ContactData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set layout
        val layoutId = "activity_signup_" + pageNum
        val resId = resources.getIdentifier(layoutId,"layout",packageName)
        setContentView(resId)

        when (pageNum) {
            0 -> {
                nameEditText.text = Editable.Factory.getInstance().newEditable(contactData.name)
                select_language.setOnClickListener {
                    val builder = AlertDialog.Builder(applicationContext)
                    builder.setTitle("Which language do you use?")

                    val defaultLang = resources.getString(R.string.default_lang)
                    val languages = arrayOf(defaultLang,"Korean","English")
                    builder.setItems(languages) { dialog, which ->
                        when (which) {
                            0 -> {
                                flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_0))
                                language_text.text = "Unknown"
                                contactData.country_code = 0
                            }
                            1 -> {
                                flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_1))
                                language_text.text = "Korean"
                                contactData.country_code = 1
                            }
                            2 -> {
                                flag_signup.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.flag_2))
                                language_text.text = "English"
                                contactData.country_code = 2
                            }
                        }
                    }

                    builder.create().show()
                }
                nextButton.setOnClickListener {
                    contactData.name = nameEditText.text.toString()
                    moveOn()
                }
            }
            1 -> {
                return
            }
            else -> {
                return
            }
        }

        // Create new activity
        moveOn()
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
}