package com.example.musicplayer.ui.ListSong

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicplayer.common.Event
import com.example.musicplayer.common.Resource
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.media.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListSongViewModel @Inject constructor(
   private val musicServiceConnection: MusicServiceConnection
): ViewModel(){
    private var _listSong = MutableLiveData<Resource<List<Song>>>()
    val listSong : LiveData<Resource<List<Song>>>
        get() = _listSong
    private var mediaId : String = ""
    private var _isPlaylistChanged = MutableLiveData<Event<Boolean>>()
    val isPlaylistChanged : LiveData<Event<Boolean>>
            get() = _isPlaylistChanged
    init {
        _isPlaylistChanged.postValue(Event(false))
    }
    fun subscribeMediaId(mediaId :String){
        this.mediaId = mediaId
        _listSong.postValue(Resource.Loading(null))
        musicServiceConnection.subscribe(mediaId,object : SubscriptionCallback(){
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
    fun updateIsPlaylistChanged(){
        if (_isPlaylistChanged.value!!.peekContent() == false){
            _isPlaylistChanged.postValue(Event(true))
        }
    }
    override fun onCleared() {

        super.onCleared()
        musicServiceConnection.unsubscribe(mediaId,object :SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
            }
        })
    }

}