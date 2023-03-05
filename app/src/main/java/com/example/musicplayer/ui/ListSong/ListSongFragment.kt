package com.example.musicplayer.ui.ListSong

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.R
import com.example.musicplayer.adapters.SongAdapter
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
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
                    mainViewModel.playOrToggleSong(song)
        }
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.rcvSong.adapter = songAdapter
        binding.rcvSong.layoutManager = LinearLayoutManager(requireContext())
        
    }

    private fun subscribeToObservers(){
        mainViewModel.listSong.observe(viewLifecycleOwner){
            result ->
            when(result.status){
                Status.SUCCESS ->{
                    binding.loadingBar.visibility = View.GONE
                    result.data?.let {
                        listSong ->
                        songAdapter.songs= listSong
                    }
                }
                Status.ERROR -> {}
                Status.LOADING ->{
                    binding.loadingBar.visibility = View.VISIBLE
                }

            }
        }
    }
}