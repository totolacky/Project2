package com.example.myapplication.Retrofit

import android.graphics.Bitmap
import com.example.myapplication.ContactData
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyService {

    @FormUrlEncoded
    @POST("/register")
    fun registerUser(@Field("id") id: String, @Field("name") name: String): Call<String>

    @FormUrlEncoded
    @POST("/login")
    fun loginUser(@Field("id") id: String): Call<ContactData>

    @FormUrlEncoded
    @POST("/initGallery")
    // 나 빼고 다른 user들의 photos + 그 user 정보
    fun getGallery(@Field("id") id: String): Call<Pair<String,ContactData>>

}