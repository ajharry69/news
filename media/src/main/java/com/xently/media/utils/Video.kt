package com.xently.media.utils

import com.google.android.exoplayer2.Player
import com.xently.media.utils.VideoSource.PROGRESSIVE

data class Video(
    val player: Player,
    val source: VideoSource = PROGRESSIVE,
    val userAgent: String = "xently"
)