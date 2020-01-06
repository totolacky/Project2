package com.example.myapplication.Retrofit

import com.example.myapplication.ContactData
import com.example.myapplication.GalleryData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyService {

    @FormUrlEncoded
    @POST("/register")
    // 회원가입
    fun register(@Field("facebookId") facebookId: String,
                 @Field("name") name: String,
                 @Field("status") status: String?,
                 @Field("country_code") country_code: Int,
                 @Field("profile_photo") profile_photo: String?,
                 @Field("photos") photos: ArrayList<String>?,
                 @Field("friends") friends: ArrayList<String>?,
                 @Field("hashtag") hashtag: ArrayList<String>?,
                 @Field("chatroom") chatroom: ArrayList<String>?): Call<String>

    @FormUrlEncoded
    @POST("/checkRegistered")
    // facebook id 보내면 이미 가입했는지 여부 보내줌
    fun checkRegistered(@Field("facebookId") facebookId: String): Call<String>

    @FormUrlEncoded
    @POST("/addFriendFb")
    // 나와 새 친구의 facebook id를 보내면 서로 친구추가
    fun addFriendFb(@Field("myFbId") myFbId: String, @Field("newFriendFbId") newFriendFbId: String): Call<String>

    @FormUrlEncoded
    @POST("/getFriends")
    // id를 보내면 그 사람의 친구 리스트를 보내줌
    fun getFriends(@Field("id") id: String): Call<ArrayList<String>>

    @FormUrlEncoded
    @POST("/getContactSimple")
    // id를 보내면 그 사람의 단순화된 연락처를 보내줌
    fun getContactSimple(@Field("id") id: String): Call<String>

    @FormUrlEncoded
    @POST("/getChatrooms")
    // 유저 id를 보내면 그 사람의 채팅방 리스트를 보내줌
    fun getChatrooms(@Field("id") id: String): Call<ArrayList<String>>

    @FormUrlEncoded
    @POST("/getChatroom")
    // 채팅방 id를 보내면 채팅방 정보를 보내줌
    fun getChatroom(@Field("id") id: String): Call<String>

    @FormUrlEncoded
    @POST("/createChatroom")
    // 유저 id를 보내면 그 사람의 채팅방 리스트를 보내줌
    fun createChatroom(@Field("myId") myId: String, @Field("yourId") yourId: String): Call<String>

    @FormUrlEncoded
    @POST("/initGallery")
    // 나 빼고 다른 user들의 photos + 그 user 정보
    fun getGallery(@Field("id") id: String): Call<GalleryData>

    @FormUrlEncoded
    @POST("/upload")
    fun uploadPhoto(@Field("id") id: String, @Field("photo") photo: String): Call<String>

}