package com.example.musicplayer.data.remote

import android.util.Log
import com.example.musicplayer.common.SONG_COLLECTION
import com.example.musicplayer.data.entities.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class MusicRemoteDatabase {
     private val firestore = FirebaseFirestore.getInstance()
     private val songCollection = firestore.collection(SONG_COLLECTION)

     suspend fun getAllSongs(): List<Song>{
          return try {
              songCollection.get().await().toObjects(Song::class.java)
          }catch (e:Exception){
               Log.d("theodoi","Không thể lấy")
               emptyList()
          }
     }
}