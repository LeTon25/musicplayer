package com.example.musicplayer.ui

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.common.MEDIA_ALBUMS_ID
import com.example.musicplayer.common.MEDIA_SINGLE_ID
import com.example.musicplayer.common.Resource
import com.example.musicplayer.data.entities.Album
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.media.MusicServiceConnection
import com.example.musicplayer.media.isPlayEnabled
import com.example.musicplayer.media.isPlaying
import com.example.musicplayer.media.isPrepared
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
):ViewModel(){

    private var _singleSongs = MutableLiveData<Resource<List<Song>>>()
      val singleSongs :LiveData<Resource<List<Song>>>
              get() = _singleSongs
    private var _albums = MutableLiveData<Resource<List<Album>>>()
        val albums :LiveData<Resource<List<Album>>>
            get() = _albums
    val currentPlayingSong = musicServiceConnection.curPlayingSong
    private var _currentPlayList = MutableLiveData<List<Song>>()
    val  currentPlaylist : LiveData<List<Song>>
            get() = _currentPlayList

    val playbackState = musicServiceConnection.playbackState

    val networkError = musicServiceConnection.networkError

    init {
        _singleSongs.postValue(Resource.Loading(null))
        musicServiceConnection.subscribe(MEDIA_SINGLE_ID,object :MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    mediaItem ->
                    Song(
                        audioUrl = mediaItem.description.mediaUri.toString(),
                        songID =  mediaItem.mediaId,
                        songName =  mediaItem.description.title.toString(),
                        songSinger =  mediaItem.description.subtitle.toString(),
                        thumbUrl = mediaItem.description.iconUri.toString()
                    )
                }
                _singleSongs.postValue(Resource.Success(items))
                updateCurrentPlaylist(items)
            }
        })

        _albums.postValue(Resource.Loading(null))
        musicServiceConnection.subscribe(MEDIA_ALBUMS_ID,object :MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
                val items = children.map {
                    mediaItem ->
                    Album(
                        albumID = mediaItem.mediaId,
                        albumName =  mediaItem.description.title.toString(),
                        albumThumb = mediaItem.description.iconUri.toString(),
                        singerName = mediaItem.description.subtitle.toString()
                    )
                }

                _albums.postValue(Resource.Success(items))

            }
        })
    }
    fun skipToNextSong(){
        musicServiceConnection.transportControls.skipToNext()
    }
    fun skipToPreviousSong(){
        musicServiceConnection.transportControls.skipToPrevious()
    }
    fun seekTo(pos:Long){
        musicServiceConnection.transportControls.seekTo(pos)
    }
    fun playOrToggleSong(song :Song , toggle :Boolean= false){
        val isPrepared =  musicServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && song.songID == musicServiceConnection.curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)){
            musicServiceConnection.playbackState.value?.let{
                playBackState->
                when{
                    playBackState.isPlaying ->
                          if (toggle)  musicServiceConnection.transportControls.pause()
                    playBackState.isPlayEnabled->
                         musicServiceConnection.transportControls.play()
                    else -> Unit
                }

            }
        }else{
            musicServiceConnection.transportControls.playFromMediaId(song.songID,null)
        }


    }
    fun updateCurrentPlaylist(songs : List<Song>){
        _currentPlayList.postValue(songs)
    }
    fun updateCurrentPlaylist(){
        _currentPlayList.postValue(_singleSongs.value?.data!!)
    }
    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_SINGLE_ID,object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>,
                options: Bundle
            ) {
                super.onChildrenLoaded(parentId, children, options)
            }
        })
        musicServiceConnection.unsubscribe(MEDIA_ALBUMS_ID,object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
            }
        })
    }

}