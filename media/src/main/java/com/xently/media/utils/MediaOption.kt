package com.xently.media.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.SimpleExoPlayer

data class MediaOption(
    val context: Context,
    val image: Image = Image(emptyArray<Uri>()),
    val video: Video = Video(SimpleExoPlayer.Builder(context).build())
)