package com.example.musicplayer.data.entities

import android.support.v4.media.MediaMetadataCompat

data class Song(
    val audioUrl:String?= null,
    val songID:String? = null,
    val songName:String? = null,
    val songSinger:String? = null,
    val thumbUrl:String? = null
)

fun Song.convertToMediaMetaDataCompat() : MediaMetadataCompat{
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,this.songSinger)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,this.audioUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,this.songID)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE,this.songName)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,this.songName)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,this.thumbUrl)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,this.songSinger)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,this.songSinger)
        .build()
}