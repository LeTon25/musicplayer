package com.example.musicplayer.ui.Player

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.musicplayer.R
import com.example.musicplayer.common.Status
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.media.isPlaying
import com.example.musicplayer.media.toSong
import com.example.musicplayer.ui.MainViewModel
import com.example.musicplayer.ui.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PlayerFragment : Fragment() {
    private lateinit var binding : FragmentPlayerBinding
    @Inject
    lateinit var glide:RequestManager
    private lateinit var mainViewModel: MainViewModel
    private val songViewModel:SongViewModel by viewModels()

    private var curSong:Song? = null
    private var playbackStateCompat:PlaybackStateCompat? = null

    private var shouldUpdateSeekBar :Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()
        binding.ivPlayPauseDetail.setOnClickListener {
            curSong?.let {
                Log.d("theodoi5","Goi play")
                mainViewModel.playOrToggleSong(it,true)

            }
        }
        binding.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }
        binding.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
        binding.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){
                        setCurPlayerTimeToTextView(progress.toLong())
                    }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekBar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekBar = true
                }
            }

        })
    }
    private fun updateTitleAndSongImage(song:Song){
        val title = "${song.songName} - ${song.songSinger}"
        binding.tvSongName.text = title
        glide.load(song.thumbUrl).into(binding.ivSongImage)
    }
    private fun subscribeToObservers(){
        mainViewModel.singleSongs.observe(viewLifecycleOwner){
            data->
            when (data.status){
                Status.SUCCESS ->{
                    data.data?.let { songs ->
                        if (curSong == null && songs.isNotEmpty()){
                            curSong = songs[0]
                            updateTitleAndSongImage(curSong!!)
                        }
                    }
                }
                Status.ERROR ->{}
                Status.LOADING->{}
            }
        }
        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner){
            song ->
            if (song != null){
                curSong = song.toSong()
                updateTitleAndSongImage(curSong!!)
            }else return@observe
        }
        mainViewModel.playbackState.observe(viewLifecycleOwner){
                playbackStateCompat = it
                binding.ivPlayPauseDetail.setImageResource(
                    if(playbackStateCompat?.isPlaying == true) R.drawable.ic_pause_circle else R.drawable.ic_play_circle
                )
                binding.seekBar.progress = it?.position?.toInt() ?: 0
        }
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner){
                if(shouldUpdateSeekBar){
                    binding.seekBar.progress = it.toInt()
                    setCurPlayerTimeToTextView(it)
                }
        }
        songViewModel.curSongDuration.observe(viewLifecycleOwner){
            binding.seekBar.max = it.toInt()
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.tvSongDuration.text = dateFormat.format(it!!)
        }
    }

    private fun setCurPlayerTimeToTextView(it: Long?) {
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            binding.tvCurTime.text = dateFormat.format(it!!)
    }

}