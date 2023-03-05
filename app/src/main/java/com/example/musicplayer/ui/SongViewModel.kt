package com.example.musicplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.media.MusicService
import com.example.musicplayer.media.MusicServiceConnection
import com.example.musicplayer.media.currentPlaybackPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
):ViewModel(){
    private val playbackState = musicServiceConnection.playbackState

    private var _curSongDuration = MutableLiveData<Long>()
    val curSongDuration:LiveData<Long> get() = _curSongDuration

    private var _curPlayerPosition = MutableLiveData<Long>()
    val curPlayerPosition:LiveData<Long> get() = _curPlayerPosition

    init {
        updateCurrentPlayerPosition()
    }
    private fun updateCurrentPlayerPosition(){
          viewModelScope.launch {
                    while (true){
                        val pos = playbackState.value?.currentPlaybackPosition
                        if (curPlayerPosition.value != pos){
                            _curPlayerPosition.postValue(pos!!)
                            _curSongDuration.postValue(MusicService.currentSongDuration)
                        }
                        delay(100L)
                    }
          }
    }
}