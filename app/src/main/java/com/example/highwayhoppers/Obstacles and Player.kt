package com.example.highwayhoppers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random
import kotlin.time.times

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val obstacles = mutableListOf<Obstacle>()
    private val obstaclePaint = Paint().apply {
        color = Color.BLACK
    }
    private val playerPaint = Paint().apply {
        color = Color.BLUE
    }
    private val medicPaint = Paint().apply{
        color = Color.WHITE
    }
    private val textPaint = Paint().apply {
        color = Color.parseColor("#FFFF00")
        textSize = 50f
    }
    private val catcherPaint = Paint().apply{
        color = Color.MAGENTA
    }
    private val coinsPaint = Paint().apply{
        color = Color.parseColor("#FFD700")
    }
    private var gameOver = false
    private val random = Random(System.currentTimeMillis())
    private val baseObstacleSpeed = 5
    private var score = 0
    private var lives : Int = 2

    private var screenWidth : Int
    private var screenHeight : Int



    fun setAllowedLives( chances : Int ){
        this.lives = chances
    }


    private val player : Player
    private val police : Catcher

   /*

    private var player : Bitmap? = null
    private var police : Bitmap? = null




    fun setplayerBitmap(bitmap: Bitmap){
        player = bitmap
        invalidate()
    }

    fun setpoliceBitmap(bitmap: Bitmap){
        police = bitmap
        invalidate()
    }


    */

    private val coins = mutableListOf<Coins>()
    private val medic = mutableListOf<Medic>()
    private var coinscollected = 0
    private var paused = false
    var newHighcore : Int = 0

    init {

        val displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        player = Player(screenWidth/2 - (screenWidth * 0.1).toInt() /2
            , 3*(screenHeight/4) - ((screenHeight * 0.1).toInt()) /2 /2
            , (screenWidth * 0.1).toInt()
            , (screenWidth * 0.1).toInt(),
            lives)

        police = Catcher(screenWidth / 2
            , screenHeight - (screenHeight * 0.1).toInt()
            , (screenWidth * 0.09).toInt() )


        // Initialize the game loop
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        if (paused) return

        val coinsToRemove = mutableListOf<Coins>()
        val obstaclesToRemove = mutableListOf<Obstacle>()
        val medicToRemove = mutableListOf<Medic>()
        val obstacleSpeed = baseObstacleSpeed + score/2


        for (i in coins){
            canvas.drawCircle(
                i.x.toFloat() ,
                i.y.toFloat() ,
                i.radius.toFloat() ,
                coinsPaint
            )
            i.y += obstacleSpeed

            if(i.y > height){
                coinsToRemove.add(i)
            }else if(coinGathering(player, i)){
                coinsToRemove.add(i)
            }

            //Checking for coin collection

            if (coinGathering(player, i, )){
                coinscollected += 1
            }


        }
        coins.removeAll(coinsToRemove)


        for (k in medic){
            canvas.drawCircle(
                k.x.toFloat(),
                k.y.toFloat(),
                k.radius.toFloat(),
                medicPaint)
            k.y += obstacleSpeed

            if(k.y > height){
                medicToRemove.add(k)
            }

            if(medicGathering(player ,k)){
                if (player.lives < 2){
                    player.lives += 1
                    police.y = screenHeight - (screenHeight * 0.1).toInt()
                    medicToRemove.add(k)
                }else{
                    medicToRemove.add(k)
                }
            }
            medic.removeAll(medicToRemove)
        }


        for (obstacle in obstacles) {
            canvas.drawRect(
                obstacle.x.toFloat(),
                obstacle.y.toFloat(),
                (obstacle.x + obstacle.width).toFloat(),
                (obstacle.y + obstacle.height).toFloat(),
                obstaclePaint
            )
            obstacle.y += obstacleSpeed

            // Remove obstacle if it moves out of screen
            if (obstacle.y > height) {
                obstaclesToRemove.add(obstacle)
                score += 1
            }

            // Check for collision with player
            if (checkCollision(player, obstacle)) {
                player.lives -= 1
                police.y = (screenHeight - (1.5*(screenHeight * 0.1)).toInt())
                obstaclesToRemove.add(obstacle)
                if (player.lives <= 0) {
                    handleGameOver()
                    return
                } else {
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Lives remaining: ${player.lives}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        obstacles.removeAll(obstaclesToRemove)

        // Draw the player
        canvas.drawRect(
            player.x.toFloat(),
            player.y.toFloat(),
            (player.x + player.width).toFloat(),
            (player.y + player.height).toFloat(),
            playerPaint
        )

        // Drawing the score and lives
        drawScoreAndLives(canvas)

        // Drawing Catcher

        canvas.drawCircle(
            police.x.toFloat(),
            police.y.toFloat(),
            police.radius.toFloat(),
            catcherPaint
        )
        val base_prob : Int = 5
        val dynamic_prob : Int = base_prob + score / 2
        if (random.nextInt(100) < dynamic_prob) {
            generateObstacle()
        }
        if (random.nextInt(100) < 2) {
            generateCoins()
        }

        if (random.nextInt(2000) < 1){
            generateMedics()
        }


        // Redraw
        postInvalidateOnAnimation()
    }


    private fun drawScoreAndLives(canvas: Canvas) {
        (context as? Activity)?.findViewById<TextView>(R.id.scoreboard)?.let { scoreText ->
            scoreText.text = score.toString()
        }

        // Draw lives on canvas
        canvas.drawText("LIVES: ${player.lives}", 50f, 100f, textPaint)
        canvas.drawText("COINS: $coinscollected", 50f , 150f , textPaint)
    }

    private fun checkCollision(player: Player, obstacle: Obstacle): Boolean {
        return player.x < obstacle.x + obstacle.width &&
                player.x + player.width > obstacle.x &&
                player.y < obstacle.y + obstacle.height &&
                player.y + player.height > obstacle.y
    }

    private fun coinGathering(player : Player , coin : Coins) :Boolean{
        return player.x < coin.x + 2*(coin.radius) &&
                player.x + player.width > coin.x &&
                player.y < coin.y + 2*(coin.radius) &&
                player.y + player.height > coin.y
    }

    private fun medicGathering(player  : Player , medic: Medic):Boolean{
        return  player.x < medic.x + 2*(medic.radius) &&
                player.x + player.width > medic.x &&
                player.y <  medic.y + 2*(medic.radius) &&
                player.y + player.height > medic.y
    }

    private fun catcherRelocation(catcher: Catcher){
        catcher.y = 2100
    }

    private fun handleGameOver() {
        gameOver = true
        updatingHighScore()
        (context as? Activity)?.runOnUiThread {
            Toast.makeText(context, "You lost", Toast.LENGTH_SHORT).show()
        }
        postDelayed(
            {
                val intent = Intent(context, HomePage::class.java)
                context.startActivity(intent)

            }, 5000)
    }

    private fun generateCoins(){
        val coinRadius = police.radius / 2
        val lanePositions = listOf(width / 6, width / 2, 4 * (width / 5))
        val randomNumber = Random.nextInt(lanePositions.size)
        var selectedLane : Int
        do {
            selectedLane = lanePositions[randomNumber]
        }while (coinObstacleCollision(selectedLane , 0 , 100 , 100))

        coins.add(Coins(selectedLane , 0 , coinRadius))
    }

    private fun generateMedics(){
        val radius = police.radius / 2
        val lanePositions = listOf(width / 6, width / 2, 4 * (width / 5))
        val randomNumber = Random.nextInt(lanePositions.size)
        var selectedLane = lanePositions[randomNumber]
        medic.add(Medic(selectedLane , 0 , radius))
    }

    fun paused(){
        paused = true
    }
    fun resumed(){
        paused = false
        postInvalidateOnAnimation()

    }
    private fun generateObstacle() {
        val obstacleWidth = player.width
        val obstacleHeight = player.height

        // Define possible lane positions based on view width (assuming horizontal lanes)
        val lanePositions = listOf(width / 6, width / 2, 4 * (width / 5))

        val occupied_lanes = obstacles.map{it.x}.toSet()
        val unused_lanes = lanePositions.filterNot { it in occupied_lanes }

        val goingToBeUsedLane = lanePositions.shuffled().take(unused_lanes.size- 1)
        for (some in goingToBeUsedLane){
            do{
                obstacles.add(Obstacle(some, 0, obstacleWidth, obstacleHeight))
            }while(coinObstacleCollision(some , 0 , obstacleWidth , obstacleHeight))

        }

    }

    private fun coinObstacleCollision(x : Int , y : Int, width: Int , height: Int): Boolean{
        for(i in obstacles){
            if (x < i.x + i.width &&
                i.width > x &&
                y < i.y + i.height &&
                i.height > y){
                    return true
            }
        }

        for(j in coins){
            if(x < j.x + 2*(j.radius) &&
                2*(j.radius) > x &&
                y < j.y + 2*(j.radius) &&
                2*(j.radius) > y){
                return true
            }

        }
        return false
    }

    private fun updatingHighScore(){
        val sharedPreferences = context.getSharedPreferences("Current High Score",Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val prevHighScore = sharedPreferences.getInt("Current High Score", 0 )

        if(score > prevHighScore){
            Toast.makeText(context , "the updated score is $score ", Toast.LENGTH_SHORT).show()
            val intent = Intent(context , HomePage::class.java)
            intent.putExtra("updatedOne", score)
        }
        
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameOver) return false
        val viewWidth = width

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                when {
                    event.x < viewWidth / 3 -> {
                        player.x = viewWidth / 8 - player.width / 2
                        police.x = viewWidth / 8
                    }
                    event.x < 2 * viewWidth / 3 -> {
                        player.x = viewWidth / 2 - player.width / 2
                        police.x = viewWidth / 2
                    }
                    else -> {
                        player.x = 4 * viewWidth / 5 - player.width / 2
                        police.x = 4 * viewWidth / 5
                    }
                }
                invalidate()
            }
        }
        return true
    }
    private fun updatingGameElements(){
        player.width = (screenWidth*0.1).toInt()
        player.height =(screenWidth*0.1).toInt(
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
        updatingGameElements()
    }
}

data class Obstacle(var x: Int, var y: Int, val width: Int, val height: Int)
data class Player(var x: Int, var y: Int, var width: Int, var height: Int, var lives: Int)
data class Catcher(var x : Int , var y : Int , val radius : Int)
data class Coins(var x : Int , var y : Int , val radius : Int)
data class Medic(var x : Int , var y : Int , val radius : Int)

