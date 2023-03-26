package com.example.musicplayer.data.remote

import android.util.Log
import com.example.musicplayer.common.ALBUM_COLLECTION
import com.example.musicplayer.common.SONG_COLLECTION
import com.example.musicplayer.data.entities.Album
import com.example.musicplayer.data.entities.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class MusicRemoteDatabase {
     private val firestore = FirebaseFirestore.getInstance()
     private val songCollection = firestore.collection(SONG_COLLECTION)
     private val albumCollection = firestore.collection(ALBUM_COLLECTION)
     suspend fun getAllSongs(): List<Song>{
          return try {
              songCollection.get().await().toObjects(Song::class.java)
          }catch (e:Exception){
               Log.d("theodoi","Không thể lấy")
               emptyList()
          }
     }

     suspend fun getAllTheAlbums():List<Album>{
          return try {
               albumCollection.get().await().toObjects(Album::class.java)
          }catch (e: Exception){
               Log.d("theodoi",e.toString())
               emptyList()
          }
     }
}