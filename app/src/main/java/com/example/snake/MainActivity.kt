package com.example.snake

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import com.example.snake.Snake.startGame
import com.example.snake.Snake.gameSpeed
import com.example.snake.Snake.play
import android.widget.ImageView
import android.widget.FrameLayout
import android.widget.LinearLayout

const val height = 10
const val width = 8

class MainActivity : AppCompatActivity() {
    private val allTail = mutableListOf<Tail>()
    private var currentDirection = Directions.BOTTOM
    private val items by lazy {
        ImageView(this).apply {
            this.layoutParams = FrameLayout.LayoutParams(100,100)
            this.setImageResource(R.drawable.items)

        }
    }
    private val head by lazy {
        ImageView(this).apply {
                this.layoutParams = FrameLayout.LayoutParams(100, 100)
                this.setImageResource(R.drawable.headd)
            }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        screen.layoutParams = LinearLayout.LayoutParams(
            width * 100, height * 100)

        startGame()
        newItems()
        Snake.nextMove = { move(Directions.BOTTOM)}

        Up.setOnClickListener {Snake.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.UP, Directions.BOTTOM) } }
        Bottom.setOnClickListener {Snake.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.BOTTOM, Directions.UP) } }
        Right.setOnClickListener {Snake.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.RIGHT, Directions.LEFT) } }
        Left.setOnClickListener {Snake.nextMove = { checkIfCurrentDirectionIsNotOpposite(Directions.LEFT, Directions.RIGHT) } }
        Pause.setOnClickListener {
            if (play) {
                Pause.setImageResource(R.drawable.play)
            } else {
                Pause.setImageResource(R.drawable.pause)
            }
            Snake.play = !play
        }

    }
    private fun checkIfCurrentDirectionIsNotOpposite(rightDirection: Directions, oppositeDirection: Directions) {
        if (currentDirection == oppositeDirection) {
            move(currentDirection)
        } else {
            move(rightDirection)
        }
    }
    private fun newItems(){
        val viewCoordinate = generateItems()
        (items.layoutParams as FrameLayout.LayoutParams).topMargin = viewCoordinate.Top
        (items.layoutParams as FrameLayout.LayoutParams).leftMargin = viewCoordinate.Left
        screen.removeView(items)
        screen.addView(items)
    }
    private fun generateItems(): viewCoordinate {
        val viewCoordinate = viewCoordinate (
            (0 until height).random() * 100,
            (0 until width).random() * 100
        )
        for (partTail in allTail){
            if (partTail.viewCoordinate == viewCoordinate){
                return generateItems()
            }
        }
        if (head.top == viewCoordinate.Top && head.left == viewCoordinate.Left) {
            return generateItems()
        }
        return viewCoordinate
    }
    private fun checkIfSnakeEatsItems(){
        if  ((head.top==items.top) && (head.left==items.left)){
            newItems()
            addTail(head.top, head.left)
            increaseDifficult()
        }
    }

    private fun increaseDifficult() {
        if ((gameSpeed <= MINIMUM_GAME_SPEED) ){
            return
        }
        if (allTail.size % 5 == 0) {
            gameSpeed -= 100
        }

    }
    private fun addTail(top: Int, left: Int) {
        val tail = drawPartOfTail(top, left)
        allTail.add(Tail(viewCoordinate(top, left), tail))
    }
    private fun drawPartOfTail(top: Int, left: Int): ImageView {
        val tailImage = ImageView(this)
        tailImage.setImageResource(R.drawable.tail)
        tailImage.layoutParams = FrameLayout.LayoutParams(100, 100)
        (tailImage.layoutParams as FrameLayout.LayoutParams).topMargin = top
        (tailImage.layoutParams as FrameLayout.LayoutParams).leftMargin = left
        screen.addView(tailImage)
        return tailImage

    }

    private fun move (directions: Directions){
        when(directions) {
            Directions.UP -> moveHeadAndRotate(Directions.UP, 180f, -100)
            Directions.BOTTOM -> moveHeadAndRotate(Directions.BOTTOM, 0f, 100)
            Directions.RIGHT -> moveHeadAndRotate(Directions.RIGHT, 270f, 100)
            Directions.LEFT -> moveHeadAndRotate(Directions.LEFT, 90f, -100)
        }
        runOnUiThread{
            if (checkSmash()){
                play = false
                showScore()
                return@runOnUiThread
            }
            makeTailMove()
            checkIfSnakeEatsItems()
            screen.removeView(head)
            screen.addView(head)
        }
    }
    private fun moveHeadAndRotate(direction: Directions, angle: Float, coordinates: Int) {
        head.rotation = angle
        when (direction) {
            Directions.UP, Directions.BOTTOM -> {
                (head.layoutParams as FrameLayout.LayoutParams).topMargin += coordinates
            }
            Directions.LEFT, Directions.RIGHT -> {
                (head.layoutParams as FrameLayout.LayoutParams).leftMargin += coordinates
            }
        }
        currentDirection = direction
    }

    private fun showScore() {
        AlertDialog.Builder(this).setTitle("Your Score ${allTail.size} items")
            .setPositiveButton("ok") { _, _ ->
                this.recreate()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun checkSmash ():Boolean{
        for (tailPart in allTail){
            if (tailPart.viewCoordinate.Left == head.left && tailPart.viewCoordinate.Top == head.top){
                return true
            }
        }
        if(head.top < 0 || head.left < 0 || head.left >= width * 100 || head.top >= height * 100){
            return true
        }
        return false
    }
    private fun makeTailMove(){
        var tempTailPart : Tail? = null
        for (index in 0 until allTail.size) {
            val tailPart = allTail[index]
            screen.removeView(tailPart.imageView)
            if (index==0){
                tempTailPart = tailPart
                allTail[index]= Tail(viewCoordinate(head.top, head.left), drawPartOfTail(head.top, head.left))
            }
            else {
                val anotherTempPartOfTail = allTail[index]
                tempTailPart?.let {
                    allTail[index] = Tail(it.viewCoordinate, drawPartOfTail(it.viewCoordinate.Top,it.viewCoordinate.Left))
                }
                tempTailPart = anotherTempPartOfTail
            }
        }
    }
}
enum class Directions {
    UP,
    RIGHT,
    BOTTOM,
    LEFT
}