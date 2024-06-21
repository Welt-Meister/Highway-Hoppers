package com.example.highwayhoppers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomePage : AppCompatActivity() {

    lateinit var button_1 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.homepage)

        button_1 = findViewById(R.id.startbutton)
        var high = button_1.text


        button_1.setOnClickListener {
            val intent = Intent(this , Game_Page::class.java).apply{
                putExtra("Current High Score" , high )
            }
            startActivity(intent)
        }

        val intent = intent
        var updatesHS = intent.getStringExtra("updatesOne")


    }
}