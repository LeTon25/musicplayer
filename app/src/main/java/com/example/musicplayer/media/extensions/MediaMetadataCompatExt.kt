package com.example.musicplayer.media.extensions

import android.support.v4.media.MediaMetadataCompat

inline var MediaMetadataCompat.Builder.flag :Int
    get() = throw IllegalAccessError("Can not get from MediaMetadataCompat.Builder")
    set(value) {
        putLong(ALBUM_FLAGS,value.toLong())
    }

const val ALBUM_FLAGS = "ALBUM_FLAGS"