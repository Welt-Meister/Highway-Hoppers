package com.example.highwayhoppers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.highwayhoppers.R
import com.example.highwayhoppers.R.drawable
import com.example.highwayhoppers.model.GameSettings
import com.example.highwayhoppers.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Game_Page : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var pausebutton : Button
    private var isPaused = false
    private var allowedLives: Int = 2


    private lateinit var imageViewPlayer:  ImageView
    private lateinit var imageViewObstacle: ImageView
    private lateinit var imageViewCatcher: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gamepage)
        val sharedPrefereces = getSharedPreferences("Current High Score", Context.MODE_PRIVATE)
        val currentHighScore = sharedPrefereces.getInt("Current High Score" , 0)


        gameView = findViewById(R.id.gameview)
        pausebutton = findViewById<Button>(R.id.pauseButton)

        pausebutton.setOnClickListener {
            isPaused = !isPaused
            if(isPaused){
                gameView.paused()
                pausebutton.text = "Resume"

            }else {
                gameView.resumed()
                pausebutton.text = "Pause"
            }
        }
    }
    private fun fetchGameSettings(){
        val apiService = ApiService.getInstance()
        val call = apiService.getGameSettings()

        call.enqueue(object : Callback<GameSettings>{
            override fun onResponse(call: Call<GameSettings>, response: Response<GameSettings>) {
                if(response.isSuccessful){
                    val gamelives = response.body()
                    allowedLives = gamelives?.allowedHits  ?: 2
                    startGame()
                }else {
                    Toast.makeText(this@Game_Page, "The API call is not good" , Toast.LENGTH_LONG).show()
                    startGame()
                }
            }

            override fun onFailure(call: Call<GameSettings>, t: Throwable) {
                Toast.makeText(this@Game_Page , "this API call is not good" , Toast.LENGTH_LONG ).show()
                startGame()
            }
        })
    }
    private fun startGame(){
        gameView.setAllowedLives(allowedLives)
    }
}