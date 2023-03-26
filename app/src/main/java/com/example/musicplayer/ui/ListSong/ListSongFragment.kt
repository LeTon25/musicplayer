package com.example.musicplayer.ui.ListSong

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.adapters.SongAdapter
import com.example.musicplayer.common.MEDIA_ID_ARG
import com.example.musicplayer.common.Status
import com.example.musicplayer.databinding.FragmentListSongBinding
import com.example.musicplayer.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ListSongFragment : Fragment() {
    private lateinit var binding: FragmentListSongBinding
    lateinit var mainViewModel : MainViewModel
    @Inject
    lateinit var songAdapter: SongAdapter

    private val listSongViewModel : ListSongViewModel by viewModels()
    private var mediaId =  ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        mediaId = requireArguments().getString(MEDIA_ID_ARG).toString()
        listSongViewModel.subscribeMediaId(mediaId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListSongBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       subscribeToObservers()

        songAdapter.setOnItemClickListener { song ->
            Log.d("theodoi4","Goi play")
            mainViewModel.playOrToggleSong(song)
                    listSongViewModel.updateIsPlaylistChanged()
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.rcvSong.adapter = songAdapter
        binding.rcvSong.layoutManager = LinearLayoutManager(requireContext())
        
    }


    private fun subscribeToObservers(){
      listSongViewModel.listSong.observe(viewLifecycleOwner){
        result ->
          when(result.status){
              Status.SUCCESS -> {
                  result.data?.let{
                      songAdapter.songs = it
                  }
              }
              Status.LOADING -> {}
              Status.ERROR -> {}
          }

      }
      listSongViewModel.isPlaylistChanged.observe(viewLifecycleOwner){
          it.getContentIfNotHandled()?.let {
              isChanged ->
              if (isChanged == true)
                  mainViewModel.updateCurrentPlaylist(listSongViewModel.listSong.value?.data!!)
          }
      }
    }
}