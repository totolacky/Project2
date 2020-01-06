package com.example.myapplication

import android.graphics.Bitmap
import java.io.Serializable

data class ContactData(var facebookId: String = "",
                       var name: String = "myName",
                       var status: String? = null,
                       var country_code: Int = 0,
                       var profile_photo: String? = null,
                       var photos: ArrayList<String>? = null,
                       var friends: ArrayList<String>? = null,
                       var hashtag: ArrayList<String>? = null) : Serializable

