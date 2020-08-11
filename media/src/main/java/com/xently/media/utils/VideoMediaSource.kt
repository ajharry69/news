package com.xently.media.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.xently.media.utils.VideoSource.PROGRESSIVE

class VideoMediaSource(
    private val context: Context,
    private val video: Video,
    uris: Array<out Uri>
) {
    val mediaSource: MediaSource

    init {
        mediaSource = buildMediaSource(*uris)
    }

    private fun buildMediaSource(vararg uris: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(context, video.userAgent)
        val mediaSourceFactory: MediaSourceFactory = when (video.source) {
            VideoSource.DASH -> DashMediaSource.Factory(dataSourceFactory)
            PROGRESSIVE -> ProgressiveMediaSource.Factory(dataSourceFactory)
        }
        val mediaSources: Array<MediaSource> = uris.map {
            mediaSourceFactory.createMediaSource(it)
        }.toTypedArray()
        return ConcatenatingMediaSource(*mediaSources)
    }
}