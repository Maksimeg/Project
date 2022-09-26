package com.example.snake

const val  START_GAME_SPEED = 500L
const val  MINIMUM_GAME_SPEED = 200L

object Snake{
    var nextMove: () -> Unit = {}
    var play = true
    private val thread: Thread
    var gameSpeed = START_GAME_SPEED

    init {
        thread = Thread(Runnable{
            while(true){
                Thread.sleep(gameSpeed)
                if (play){
                    nextMove()
                }
            }
        })
        thread.start()
    }
    fun startGame(){
        gameSpeed = START_GAME_SPEED
        play = true
    }
}