package com.example.musicplayer.media

import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.core.net.toUri
import com.example.musicplayer.data.entities.convertToMediaMetaDataCompat
import com.example.musicplayer.data.remote.MusicRemoteDatabase
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicSource @Inject constructor(private val musicRemoteDatabase: MusicRemoteDatabase) {

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData()  {
        state = State.STATE_INITIALIZING
        getAllSongs()
        state = State.STATE_INITAILIZED
    }
    suspend fun getAllSongs()= withContext(Dispatchers.Main){
        val allSong = musicRemoteDatabase.getAllSongs()
        songs = allSong.map { song ->
            song.convertToMediaMetaDataCompat()
        }
    }
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>();


    private var state: State = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITAILIZED || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.STATE_INITAILIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        if (state == State.STATE_ERROR || state == State.STATE_INITIALIZING) {
            onReadyListeners += action
            return false
        } else {
            action(state == State.STATE_INITAILIZED)
            return true
        }

    }

    fun asMediaSource(defaultDataSource: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource();
        songs.forEach { song ->
            val medisSource = ProgressiveMediaSource.Factory(defaultDataSource)
                .createMediaSource(MediaItem.fromUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)))
            concatenatingMediaSource.addMediaSource(medisSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()


}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITAILIZED,
    STATE_ERROR
}