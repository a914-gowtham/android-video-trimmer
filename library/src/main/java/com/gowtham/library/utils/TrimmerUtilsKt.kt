package com.gowtham.library.utils

import android.app.Activity
import android.net.Uri

object TrimmerUtilsKt {

    fun estimateVideoSizeMb(
        context: Activity,
        fileUri: Uri,
        durationSec: Int,
        width: Int? = null,
        height: Int? = null,
        bitsPerPixelPerFrame: Double = 0.07 // default compression factor (typical for 720p–1080p)
    ): Double {

        val bitRateBps= TrimmerUtils.getBitRate(context, fileUri)
        val frameRate= TrimmerUtils.getFrameRate(context, fileUri)

        val bitrate: Long = bitRateBps.toLong() ?: run {
            if (width != null && height != null && frameRate != null) {
                (width * height * frameRate * bitsPerPixelPerFrame).toLong()
            } else {
                0L
            }
        } ?: 0L

        val sizeBytes = (bitrate * durationSec) / 8.0
        return sizeBytes / (1024 * 1024) // → MB
    }

}