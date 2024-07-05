package com.example.highwayhoppers.network

import com.example.highwayhoppers.ImageApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // serailization


object RetrofitInstance {
    private const val BASE_URL = "http://your_api_base_url/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

