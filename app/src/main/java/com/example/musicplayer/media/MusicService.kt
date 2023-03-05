package com.example.musicplayer.media

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.example.musicplayer.common.MEDIA_ROOT_ID
import com.example.musicplayer.common.NETWORK_ERROR
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var  dateSource : DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    private lateinit var notificationManager: MusicNotificationManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope( Dispatchers.Main +serviceJob )

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    lateinit var musicPlayerEventListener: MusicPlayerEventListener

    var isForegroundService = false

    private var isPlayerInitialized = false

    private var curPlayingSong:MediaMetadataCompat? = null
    companion object{
        var currentSongDuration = 0L
           private set
    }
    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            musicSource.fetchMediaData()
        }
        val activityIntent=  packageManager.getLaunchIntentForPackage(packageName).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        mediaSession  = MediaSessionCompat(this,"Music_Service").apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSession.sessionToken

        notificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicNotificationListener(this),
        ){
                currentSongDuration = exoPlayer.duration
        }

        val musicPlaybackPreparer =MusicPlaybackPreparer(musicSource){
            curPlayingSong = it
            preparePlayer(musicSource.songs,
                        it!!,
                    true
                )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener =MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        notificationManager.showNotification(exoPlayer)
    }
    private fun preparePlayer(songs:List<MediaMetadataCompat>,
                               itemToPlay : MediaMetadataCompat,
                              playNow : Boolean
                              ){
         val curSongIndex = if(curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(musicSource.asMediaSource(dateSource))
        exoPlayer.seekTo(curSongIndex,0L)
        exoPlayer.playWhenReady =playNow
    }
    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession){
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.songs[windowIndex].description
        }

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }
    override fun onDestroy() {
        super.onDestroy()

        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MEDIA_ROOT_ID,null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when(parentId){
            MEDIA_ROOT_ID ->{
                    val resultSent  = musicSource.whenReady { isInitialized ->
                    if (isInitialized){
                        result.sendResult(musicSource.asMediaItems())
                        if (!isPlayerInitialized){
                            preparePlayer(musicSource.songs,musicSource.songs[0],false)
                            isPlayerInitialized = true
                        }
                    }else{
                        mediaSession.sendSessionEvent(NETWORK_ERROR,null)
                        result.sendResult(null)
                    }

                    }
                if (!resultSent){
                    result.detach()
                }
            }
        }
    }
}