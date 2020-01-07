package com.example.myapplication

import java.io.Serializable

data class ContactData(var _id: String = "",
                       var facebookId: String = "",
                       var name: String = "myName",
                       var status: String = "",
                       var country_code: Int = 0,
                       var profile_photo: String = "",
                       var photos: ArrayList<String> = ArrayList(),
                       var friends: ArrayList<String> = ArrayList(),
                       var hashtag: ArrayList<String> = ArrayList(),
                       var chatroom: ArrayList<String> = ArrayList()) : Serializable

data class ChatData(var id: String = "",
                    var script: String = "",
                    var date_time: String=""): Serializable

data class ChatroomData(var chatroom_id: String = "",
                        var chatroom_name: String = "",
                        var last_chat: String = "",
                        var chatroom_image: String = "",
                        var people: ArrayList<String> = ArrayList()) : Serializable

data class GalleryData(var selectedPhoto: String,
                       var userContactData: ContactData) : Serializable