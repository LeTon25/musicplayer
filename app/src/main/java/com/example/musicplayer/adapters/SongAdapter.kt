package com.example.musicplayer.adapters

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.musicplayer.R
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.databinding.FragmentListSongBinding
import com.example.musicplayer.databinding.SongItemViewBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
)  :BaseSongAdapter(R.layout.song_item_view)
{
    override val differ: AsyncListDiffer<Song> = AsyncListDiffer(this,diffUtilCallback)
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val view = holder.itemView
        val ivSongImage = view.findViewById<ImageView>(R.id.ivSongImage)
        val tvSongTitle = view.findViewById<TextView>(R.id.tvSongTitle)
        val tvSongSubtitle = view.findViewById<TextView>(R.id.tvSongSubtitle)
        val song= songs[position]
        view.apply {
                setOnClickListener(){
                    myOnItemClickListener?.let{
                        action ->
                        action(song)
                    }
                }
            }
        tvSongTitle.text = song.songName
        tvSongSubtitle.text = song.songSinger
        glide.load(song.thumbUrl).into(ivSongImage)
    }
}

