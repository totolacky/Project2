package com.example.myapplication

import android.graphics.Bitmap
import java.io.Serializable

data class ContactData (
    var facebookId: Int = 0,
    var name: String = "myName",
    var status: String? = null,
    var country_code: Int = 0,
    var profile_photo: Bitmap? = null,
    var photos: ArrayList<Bitmap>? = null,
    var friends: ArrayList<Int>? = null,
    var hashtag: ArrayList<String>? = null
) : Serializable