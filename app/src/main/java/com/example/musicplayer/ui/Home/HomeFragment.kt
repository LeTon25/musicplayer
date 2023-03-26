package com.example.musicplayer.ui.Home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapters.AlbumAdapter
import com.example.musicplayer.adapters.SongAdapter
import com.example.musicplayer.common.MEDIA_ID_ARG
import com.example.musicplayer.common.Status
import com.example.musicplayer.databinding.FragmentHomeBinding
import com.example.musicplayer.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding :FragmentHomeBinding
    @Inject
    lateinit var albumAdapter: AlbumAdapter

    @Inject
    lateinit var songAdapter: SongAdapter

    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subcribeToObservers()
        songAdapter.setOnItemClickListener { song ->
            Log.d("theodoi3","Goi play")
            mainViewModel.playOrToggleSong(song)
            mainViewModel.updateCurrentPlaylist()
        }
        albumAdapter.setOnItemClickListener {
            album ->
            val bundle = Bundle()
            bundle.putString(MEDIA_ID_ARG,album.albumID)
            findNavController().navigate(R.id.listSongFragment,bundle)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.rcvSong.adapter = songAdapter
        binding.rcvSong.layoutManager = LinearLayoutManager(requireContext())

        binding.rcvAlbum.adapter = albumAdapter
        binding.rcvAlbum.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }

    private fun subcribeToObservers() {
        mainViewModel.singleSongs.observe(viewLifecycleOwner){
                result ->
            when(result.status){
                Status.SUCCESS ->{
                    result.data?.let { songs ->
                        songAdapter.songs = songs
                    }
                    binding.loadingSongBar.visibility = View.GONE
                }
                Status.ERROR -> {}
                Status.LOADING ->{
                    binding.loadingSongBar.visibility = View.VISIBLE
                }
            }
        }
        mainViewModel.albums.observe(viewLifecycleOwner){
            result ->
            when(result.status){
                Status.SUCCESS->{
                    result.data?.let{
                        albums ->
                        albumAdapter.albums = albums
                    }
                    binding.loadingAlbumBar.visibility = View.GONE
                }
                Status.ERROR ->{}
                Status.LOADING ->{
                    binding.loadingAlbumBar.visibility = View.VISIBLE
                }
            }
        }
        mainViewModel.networkError.observe(viewLifecycleOwner){
            result ->
            result.getContentIfNotHandled()?.let {
                isNetworkError ->
                if (isNetworkError.data == true){
                    Toast.makeText(requireContext(),"Can not connect to the network",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}