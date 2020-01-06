package com.example.myapplication

import java.io.Serializable

data class ChatroomData(var chatroom_id: String = "",
                        var chatroom_name: String = "myName",
                        var last_chat: String = "",
                        var chatroom_image: String = "",
                        var people: ArrayList<String> = ArrayList()) : Serializable
