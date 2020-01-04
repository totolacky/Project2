package com.example.myapplication.Retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class RetrofitClient{


    companion object {
        var instance: Retrofit? = null

        @JvmStatic fun getinstance(): Retrofit?{
            if(instance==null){
                instance = Retrofit.Builder().baseUrl("http://192.249.19.251:80/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
            }
            return instance
        }
    }


}