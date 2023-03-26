package com.example.musicplayer.media

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicplayer.common.Event
import com.example.musicplayer.common.NETWORK_ERROR
import com.example.musicplayer.common.Resource

class MusicServiceConnection(
    context :Context
) {
    private var _isConnected = MutableLiveData<Event<Resource<Boolean>>>()

    val isConnected :LiveData<Event<Resource<Boolean>>>
              get() = _isConnected

    private var _playbackState = MutableLiveData<PlaybackStateCompat?>()

    val playbackState :LiveData<PlaybackStateCompat?>
        get() = _playbackState


    private var _networkError = MutableLiveData<Event<Resource<Boolean>>>()

    val networkError :LiveData<Event<Resource<Boolean>>>
        get() = _networkError


    private var _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()

    val curPlayingSong :LiveData<MediaMetadataCompat?>
        get() = _curPlayingSong

    lateinit var mediaController : MediaControllerCompat

    private val mediaBrowerConnectionCallback = MediaBrowerConnectionCallback(context)

    private val mediaBrower = MediaBrowserCompat(
        context,
        ComponentName(
            context,
            MusicService::class.java
        ),
        mediaBrowerConnectionCallback,
        null
    ).apply {
        connect()
    }
    val transportControls
        get() = mediaController.transportControls

    fun subscribe(parentId:String,callback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrower.subscribe(parentId,callback)
    }
    fun unsubscribe(parentId:String,callback:MediaBrowserCompat.SubscriptionCallback){
        mediaBrower.unsubscribe(parentId,callback)
    }
    private inner class MediaBrowerConnectionCallback(
        private val context: Context
    ):MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()
            mediaController = MediaControllerCompat(context,mediaBrower.sessionToken)
            mediaController.registerCallback(MediaControllerCallback())
            _isConnected.postValue(Event(
                Resource.Success(true)
            ))

        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            _isConnected.postValue(
                Event(
                Resource.Error(false,"Không thể kết nối tới service")
            ))
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            _isConnected.postValue(
                Event(
                    Resource.Error(false,"")
                )
            )
        }
    }
    private inner class MediaControllerCallback : MediaControllerCompat.Callback(){
        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event){
                NETWORK_ERROR ->
                     _networkError.postValue(
                         Event(
                             Resource.Error(null,"Không thể kết nối internet")
                         )
                     )

            }
        }
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            _curPlayingSong.postValue(metadata)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowerConnectionCallback.onConnectionSuspended()
        }
    }


}