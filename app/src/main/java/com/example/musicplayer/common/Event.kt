package com.example.musicplayer.common

open class Event<out T>(private val data: T?) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled():T?{
        if (!hasBeenHandled){
            hasBeenHandled = true
            return  data
        }else{
            return null
        }
    }

    fun peekContent() = data

}