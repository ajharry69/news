package com.xently.media.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xently.media.utils.IMAGE_FORMATS_STR
import com.xently.media.utils.VIDEO_FORMATS_STR
import java.util.regex.Pattern

class MediaViewModel : ViewModel() {
    private val mediaUris = MutableLiveData<Array<out Uri>>()
    private val _videoMediaUris = MutableLiveData<Array<Uri>>()
    val videoMediaUris: LiveData<Array<Uri>>
        get() = _videoMediaUris
    private val _imageMediaUris = MutableLiveData<Array<Uri>>()
    val imageMediaUris: LiveData<Array<Uri>>
        get() = _imageMediaUris

    init {
        mediaUris.observeForever { uris ->
            _imageMediaUris.value =
                uris.filter { Pattern.matches(".*($IMAGE_FORMATS_STR)$", it.toString()) }
                    .toTypedArray()
            _videoMediaUris.value =
                uris.filter { Pattern.matches(".*($VIDEO_FORMATS_STR)$", it.toString()) }
                    .toTypedArray()
        }
    }

    fun setImageUris(uris: Array<out Uri>) {
        mediaUris.value = uris
    }
}