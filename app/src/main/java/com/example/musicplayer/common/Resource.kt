package com.example.musicplayer.common

data class Resource<out T>(val status: Status, val data :T?, val message:String?) {
    companion object{
        fun <T>Success(data:T?) = Resource(Status.SUCCESS, data = data,null)
        fun <T>Error(data:T?,message: String?) = Resource(Status.ERROR,data, message = message)
        fun <T>Loading(data : T?) = Resource(Status.LOADING, data =data ,null)
    }
}
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}