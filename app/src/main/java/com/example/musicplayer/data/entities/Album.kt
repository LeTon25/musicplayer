package com.example.musicplayer.data.entities

import android.media.browse.MediaBrowser.MediaItem
import android.support.v4.media.MediaMetadataCompat
import com.example.musicplayer.media.extensions.flag

data class Album(val albumID :String? = null,
                 val albumName:String? = null,
                 val albumThumb:String? = null,
                 val singerName:String? = null
                 )
fun Album.toMediaMetadata(): MediaMetadataCompat{
    return  MediaMetadataCompat.Builder().apply {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,albumID)
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM,albumName)
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,albumThumb)
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,singerName)
        flag = MediaItem.FLAG_BROWSABLE
    }.build()


}