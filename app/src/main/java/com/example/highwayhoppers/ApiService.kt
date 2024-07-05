package com.example.highwayhoppers.network

import com.example.highwayhoppers.model.GameSettings
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("game-settings")
    fun getGameSettings():Call<GameSettings>


    companion object {
        private const val Base_URL = "https://api.example.com/"
        private var instance: ApiService? = null

        fun getInstance():ApiService{
            if (instance == null) {
                instance = Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)

            }
            return instance!!
        }
    }

}



