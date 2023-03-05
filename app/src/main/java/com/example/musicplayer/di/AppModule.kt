package com.example.musicplayer.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.R
import com.example.musicplayer.adapters.SwipeSongAdapter
import com.example.musicplayer.media.MusicServiceConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ):MusicServiceConnection{
        return MusicServiceConnection(context)
    }

    @Singleton
    @Provides
    fun provideSwipeSongAdapter() :SwipeSongAdapter{
        return SwipeSongAdapter()

    }
    @Provides
    fun provideGile(
        @ApplicationContext context: Context
    ) : RequestManager{
        return Glide
            .with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
            )
    }
}