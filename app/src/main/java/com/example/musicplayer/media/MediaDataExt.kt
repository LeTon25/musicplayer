package com.example.musicplayer.media

import android.support.v4.media.MediaMetadataCompat
import com.example.musicplayer.data.entities.Song

fun MediaMetadataCompat.toSong(): Song? {
    return description?.let {
        Song(
            audioUrl = it.mediaUri.toString(),
            songID =it.mediaId.toString(),
            songName = it.title.toString(),
            songSinger = it.subtitle.toString(),
            thumbUrl = it.iconUri.toString()
        )
    }
}