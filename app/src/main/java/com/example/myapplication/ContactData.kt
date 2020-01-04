package com.example.myapplication

import android.graphics.Bitmap

class ContactData(id: Int, name: String, status: String?, country_code: Int, profile_photo: Bitmap?, email: String, photos: ArrayList<Bitmap>?, friends: ArrayList<Int>?, hashtag: ArrayList<String>?){

    var id: Int = 0
    var name: String = "myName"
    var status: String? = null
    var country_code: Int = 0
    var profile_photo: Bitmap? = null
    var email: String = "default@email.com"
    var photos: ArrayList<Bitmap>? = null
    var friends: ArrayList<Int>? = null
    var hashtag: ArrayList<String>? = null

    init {
        this.id = id
        this.name = name
        this.status = status
        this.country_code = country_code
        this.profile_photo = profile_photo
        this.email = email
        this.photos = photos
        this.friends = friends
        this.hashtag = hashtag
    }
}