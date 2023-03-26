package com.example.musicplayer.media

import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.example.musicplayer.common.MEDIA_ALBUMS_ID
import com.example.musicplayer.common.MEDIA_ROOT_ID
import com.example.musicplayer.common.MEDIA_SINGLE_ID
import com.example.musicplayer.media.extensions.flag
import com.google.android.exoplayer2.MediaItem

class BrowserTree(
    musicSource: MusicSource
) {
    private val mediaIdToChildren = mutableMapOf<String,MutableList<MediaMetadataCompat>>()

   init {
       /*  start to build a browser tree  */
       val rootlist = mediaIdToChildren[MEDIA_ROOT_ID] ?: mutableListOf()

       val singleMetaData = MediaMetadataCompat.Builder().apply {
           putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MEDIA_SINGLE_ID)
           putString(MediaMetadataCompat.METADATA_KEY_TITLE,"Single Song")
           flag = android.media.browse.MediaBrowser.MediaItem.FLAG_BROWSABLE
       }.build()

       val albumMetaData =MediaMetadataCompat.Builder().apply {
           putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, MEDIA_ALBUMS_ID)
           putString(MediaMetadataCompat.METADATA_KEY_TITLE,"Albums")
           flag = android.media.browse.MediaBrowser.MediaItem.FLAG_BROWSABLE
       }.build()
       rootlist += singleMetaData
       rootlist += albumMetaData
       mediaIdToChildren[MEDIA_ROOT_ID] = rootlist
       /* build for album root */
       musicSource.albums.forEach {
               album ->
           buildAlbumRoot(album)
       }
       /* add song to  single song root and add album root */
       musicSource.songs.forEach {
            mediaItem ->
                val albumId =   mediaItem.getString("AlbumId")
                 if (albumId == ""){
                     addMediaToSingleRoot(mediaItem)
                 }else{
                     addMediaToAlbumRoot(mediaItem,albumId)
                 }
       }


   }

    private fun addMediaToAlbumRoot(mediaItem: MediaMetadataCompat,albumId:String) {
            val rootList = mediaIdToChildren[albumId] ?: mutableListOf()
            rootList += mediaItem
            mediaIdToChildren[albumId] = rootList
    }

    private fun addMediaToSingleRoot(mediaItem: MediaMetadataCompat) {
        val  rootList = mediaIdToChildren[MEDIA_SINGLE_ID] ?: mutableListOf()
        rootList += mediaItem
        mediaIdToChildren[MEDIA_SINGLE_ID] = rootList
    }

    private fun buildAlbumRoot(mediaItem : MediaMetadataCompat) {
        val  rootList = mediaIdToChildren[MEDIA_ALBUMS_ID] ?: mutableListOf()
        rootList += mediaItem
        mediaIdToChildren[MEDIA_ALBUMS_ID]= rootList
    }
    fun get(mediaId:String) :List<MediaMetadataCompat> {
         return mediaIdToChildren[mediaId]!!
     }
}