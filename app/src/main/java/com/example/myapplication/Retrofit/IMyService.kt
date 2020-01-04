package com.example.myapplication.Retrofit

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMyService {

    @FormUrlEncoded
    @POST("register")
    fun registerUser(@Field("email") email: String, @Field("name") name: String): Observable<String>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(@Field("email") email: String): Observable<String>

}