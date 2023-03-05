package com.example.musicplayer.ui

import android.os.Bundle
import android.provider.MediaStore.Images.Media
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.common.MEDIA_ROOT_ID
import com.example.musicplayer.common.Resource
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

    private var _listSong = MutableLiveData<Resource<List<Song>>>()
      val listSong :LiveData<Resource<List<Song>>>
              get() = _listSong
    val currentPlayingSong = musicServiceConnection.curPlayingSong

    val playbackState = musicServiceConnection.playbackState
    init {
        _listSong.postValue(Resource.Loading(null))
        musicServiceConnection.subscribe(MEDIA_ROOT_ID,object :MediaBrowserCompat.SubscriptionCallback(){
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
                _listSong.postValue(Resource.Success(items))
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
    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID,object : MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>,
                options: Bundle
            ) {
                super.onChildrenLoaded(parentId, children, options)
            }
        })
    }
}