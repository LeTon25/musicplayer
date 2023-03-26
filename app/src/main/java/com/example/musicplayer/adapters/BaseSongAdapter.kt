package com.example.musicplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.databinding.FragmentListSongBinding
import com.example.musicplayer.databinding.SongItemViewBinding

abstract class BaseSongAdapter(
    private val layout_id:Int
    ) :RecyclerView.Adapter<BaseSongAdapter.SongViewHolder>(){
    var songs : List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)
    protected val diffUtilCallback = object : DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.songID == newItem.songID
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    protected abstract val differ :AsyncListDiffer<Song>


    inner class SongViewHolder( view: View) : RecyclerView.ViewHolder(view){
    }
    var myOnItemClickListener : ((Song) -> Unit)? = null

    fun setOnItemClickListener(action : (Song) ->Unit){
        myOnItemClickListener = action
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(
            layout_id,
            parent,
            false
        )
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    }