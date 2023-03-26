package com.example.musicplayer.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.musicplayer.R
import com.example.musicplayer.data.entities.Album
import com.example.musicplayer.databinding.AlbumItemViewBinding
import com.example.musicplayer.databinding.FragmentHomeBinding
import javax.inject.Inject

class AlbumAdapter @Inject constructor(
     private val glide:RequestManager
) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
    val diffUtilCallback =object : DiffUtil.ItemCallback<Album>(){
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.albumID == newItem.albumID
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    val differ : AsyncListDiffer<Album> = AsyncListDiffer(this,diffUtilCallback)
    var albums : List<Album>
       get() = differ.currentList
       set(value) {
           differ.submitList(value)
       }

    inner class AlbumViewHolder(private val binding:AlbumItemViewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.albumName.text= album.albumName
            glide.load(album.albumThumb).into(binding.albumThumb)
        }
    }
    var myOnItemClickListener : ((Album) -> Unit)? = null
        private set

    fun setOnItemClickListener(action : (Album) ->Unit){
         myOnItemClickListener = action
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view  =AlbumItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
                val currentAlbum = albums[position]
                holder.bind(currentAlbum)
                holder.itemView.apply {
                    setOnClickListener {
                        myOnItemClickListener?.let {
                            action ->
                            action(currentAlbum)
                        }
                    }
                }
    }

    override fun getItemCount(): Int {
       return albums.size
    }
}