package com.example.myapplication.Retrofit

import com.example.myapplication.ContactData
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyService {

    @FormUrlEncoded
    @POST("/register")
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
    fun checkRegistered(@Field("facebookId") facebookId: String): Call<String>

    @FormUrlEncoded
    @POST("/addFriendFb")
    fun addFriendFb(@Field("myFbId") myFbId: String, @Field("newFriendFbId") newFriendFbId: String): Call<String>

    @FormUrlEncoded
    @POST("/initGallery")
    // 나 빼고 다른 user들의 photos + 그 user 정보
    fun getGallery(@Field("id") id: String): Call<Pair<String,ContactData>>

    @FormUrlEncoded
    @POST("/upload")
    fun uploadPhoto(@Field("id") id: String, @Field("photo") photo: String): Call<String>

}