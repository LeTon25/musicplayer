package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.RequestManager
import com.example.musicplayer.adapters.SwipeSongAdapter
import com.example.musicplayer.data.entities.Song
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.media.isPlaying
import com.example.musicplayer.media.toSong
import com.example.musicplayer.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var glide:RequestManager

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    private lateinit var binding :ActivityMainBinding

    private var curSong : Song? = null

    private var playbackStateCompat:PlaybackStateCompat? = null

    private  val mainViewModel:MainViewModel by viewModels()

    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vpSong.adapter = swipeSongAdapter
        /*
        binding.vpSong.registerOnPageChangeCallback(object :OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackStateCompat?.isPlaying == true){
                    Log.d("theodoi1","Goi play")
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                }else{
                    curSong = swipeSongAdapter.songs[position]
                    glide.load(curSong!!.thumbUrl).into(binding.ivCurSongImage)
                }
            }
        })
        */
        binding.ivPlayPause.setOnClickListener {
            curSong?.let{
                Log.d("theodoi2","Goi play")
                mainViewModel.playOrToggleSong(it,true)
            }
        }

        subscribeToObservers()
    }
    private fun hideBottomBar(){
        binding.vpSong.isVisible = false
        binding.ivPlayPause.isVisible = false
        binding.ivCurSongImage.isVisible = false
    }
    private fun showBottomBar(){
        binding.vpSong.isVisible = true
        binding.ivPlayPause.isVisible = true
        binding.ivCurSongImage.isVisible = true
    }
    private fun switchViewPagerToCurrentSong(song:Song){
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1){
            binding.vpSong.currentItem = newItemIndex
            curSong= song
        }
    }

    private fun subscribeToObservers(){
        mainViewModel.currentPlaylist.observe(this){
            it?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if(songs.isNotEmpty()){
                                glide.load(curSong ?:songs[0].thumbUrl).into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curSong ?: return@observe)
            }
        }

        mainViewModel.currentPlayingSong.observe(this){
            if (it == null) return@observe
            curSong = it.toSong()
            glide.load(curSong!!.thumbUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curSong ?: return@observe)

        }


        mainViewModel.playbackState.observe(this){
            playbackStateCompat = it
            binding.ivPlayPause.setImageResource(
              if (playbackStateCompat?.isPlaying == true  )R.drawable.ic_pause else R.drawable.ic_play
            )
        }


        val navHost = supportFragmentManager.findFragmentById(R.id.navHostFragments)   as NavHostFragment
        navController = navHost.findNavController()
        swipeSongAdapter.setOnItemClickListener {
                navController.navigate(
                    R.id.actionGoToPlayerFragment
                )

        }

        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                when (destination.id ){
                    R.id.listSongFragment -> {
                        showBottomBar()
                    }
                    R.id.playerFragment -> {
                        hideBottomBar()
                    }
                    R.id.homeFragment -> {
                        showBottomBar()
                    }
                }
            }

        })

    }
}