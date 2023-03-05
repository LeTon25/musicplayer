package com.example.musicplayer.adapters

import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import com.example.musicplayer.R
import com.example.musicplayer.data.entities.Song

class SwipeSongAdapter : BaseSongAdapter(R.layout.swipe_item_view) {

    override val differ: AsyncListDiffer<Song> = AsyncListDiffer(this,diffUtilCallback  )

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
       val song = songs[position]
        val view = holder.itemView
        val tvSongTitle = view.findViewById<TextView>(R.id.tvSongTitle)
        val title = "${song.songName} - ${song.songSinger}"
        tvSongTitle.text = title
        view.apply {
            setOnClickListener() {
                Log.d("theodoi","daclick")
                    myOnItemClickListener?.let {
                        it(song)
                    }
            }
        }

    }
}