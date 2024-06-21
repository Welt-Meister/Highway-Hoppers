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
import androidx.core.content.ContextCompat
import com.example.highwayhoppers.R
import com.example.highwayhoppers.R.drawable

class Game_Page : AppCompatActivity() {

    private lateinit var gameView: GameView
    private lateinit var pausebutton : Button
    private var isPaused = false

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
}