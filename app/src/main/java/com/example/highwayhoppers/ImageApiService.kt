package com.example.highwayhoppers

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface ImageApiService {
    @GET("Player_Image")
    @Streaming
    suspend fun getPlayerImage(): Response<ResponseBody>

    @GET("Obstacle_Image")
    @Streaming
    suspend fun getObstacleImage(): Response<ResponseBody>

    @GET("Catcher_Image")
    @Streaming
    suspend fun getCatcherImage() : Response<ResponseBody>

}