package com.xently.media.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.smarteist.autoimageslider.SliderView
import com.xently.media.R
import com.xently.media.utils.ImageSliderAdapter
import com.xently.media.utils.VIDEO_FORMATS_STR
import com.xently.media.utils.Video
import com.xently.media.utils.VideoMediaSource
import com.xently.utilities.ui.fragments.Fragment
import com.xently.utilities.viewext.hideViews
import com.xently.utilities.viewext.showViews
import java.util.regex.Pattern

@Suppress("MemberVisibilityCanBePrivate")
open class MediaFragment : Fragment(R.layout.media_fragment) {
    private var mediaUris: Array<out Uri> = emptyArray()
    private val videoUris: Array<out Uri>
        get() = mediaUris.filter { Pattern.matches(".*($VIDEO_FORMATS_STR)$", it.toString()) }
            .toTypedArray()
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var player: Player? = null
    private lateinit var video: Video

    // Views
    protected lateinit var videoPlayerView: PlayerView
    protected lateinit var sliderView: SliderView
    private lateinit var sliderAdapter: ImageSliderAdapter

    private val viewModel: MediaViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sliderAdapter = ImageSliderAdapter()
        videoPlayerView = (view.findViewById(R.id.media_video_player) as? PlayerView)
            ?: PlayerView(requireContext()).apply {
                id = R.id.media_video_player
                layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        sliderView = (view.findViewById(R.id.media_slider_view) as? SliderView)
            ?: SliderView(requireContext()).apply {
                id = R.id.media_slider_view
                layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        sliderView.setSliderAdapter(sliderAdapter)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.run {
            videoMediaUris.observe(viewLifecycleOwner, Observer {
                if (it.isNullOrEmpty()) hideViews(sliderView) else showViews(videoPlayerView)
            })
            imageMediaUris.observe(viewLifecycleOwner, Observer {
                if (it.isNullOrEmpty()) hideViews(videoPlayerView) else showViews(sliderView)
                sliderAdapter.submitList(it.toList())
            })
        }
    }

    override fun onStart() {
        super.onStart()
        // Android API level 24 and higher supports multiple windows. As your app can be visible,
        // but not active in split window mode, you need to initialize the player in onStart
        // Android API level 24 and higher supports multiple windows. As your app can be visible,
        // but not active in split window mode, you need to initialize the player in onStart
        if (Util.SDK_INT >= 24) initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        // Android API level 24 and lower requires you to wait as long as possible until you
        // grab resources, so you wait until onResume before initializing the player.
        // Android API level 24 and lower requires you to wait as long as possible until you
        // grab resources, so you wait until onResume before initializing the player.
        if (Util.SDK_INT < 24 || player == null) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        // With API Level lower than 24, there is no guarantee of onStop being called, so you
        // have to release the player as early as possible in onPause
        // With API Level lower than 24, there is no guarantee of onStop being called, so you
        // have to release the player as early as possible in onPause
        if (Util.SDK_INT < 24) releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        // With API Level 24 and higher (which brought multi- and split-window mode), onStop
        // is guaranteed to be called. In the paused state, your activity is still visible,
        // so you wait to release the player until onStop.
        // With API Level 24 and higher (which brought multi- and split-window mode), onStop
        // is guaranteed to be called. In the paused state, your activity is still visible,
        // so you wait to release the player until onStop.
        if (Util.SDK_INT >= 24) releasePlayer()
    }

    fun setVideo(video: Video) {
        this.video = video
        this.player = video.player
    }

    fun setMediaUris(vararg uris: Uri) {
        mediaUris = uris
        viewModel.setImageUris(uris)
    }

    fun setMediaUris(vararg uris: String) = setMediaUris(*uris.map { Uri.parse(it) }.toTypedArray())

    private fun hideSystemUI() {
        videoPlayerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun initializePlayer() {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(requireContext())
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd())
            val player = SimpleExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector) // used for adaptive streaming over http (DASH)
                .build()
            setVideo(Video(player))
        }
        player?.apply {
            videoPlayerView.player = this
            playWhenReady = this@MediaFragment.playWhenReady
            seekTo(currentWindow, playbackPosition)
            // Register the listener before the play is prepared
            // addListener(playbackStateListener)
            if (this is SimpleExoPlayer) {
                // tells the player to acquire all the resources for the given mediaSource, and
                // additionally tells it not to reset the position or state because you already
                // set these in the two previous lines.
                prepare(
                    VideoMediaSource(requireContext(), video, videoUris).mediaSource,
                    false,
                    false
                )
            }
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            // tidy up to avoid dangling references from the player which could cause a memory leak.
            // player!!.removeListener(playbackStateListener)
            player!!.release()
            player = null
        }
    }

}