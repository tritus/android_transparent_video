package com.tritus.transparentvideo.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.annotation.RawRes

fun extractVideoRatio(context: Context, @RawRes videoSource: Int): Float? {
    val assetDescriptor = context.resources.openRawResourceFd(videoSource)
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(
        assetDescriptor.fileDescriptor,
        assetDescriptor.startOffset,
        assetDescriptor.length
    )
    val videoWidth = retriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        ?.toInt()
    val videoHeight = retriever
        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        ?.toInt()?.let { it / 2 }
    return if (videoWidth != null && videoHeight != null && videoWidth > 0 && videoHeight > 0) {
        videoWidth.toFloat() / videoHeight
    } else {
        Log.e(
            "extractVideoRatio",
            "Could not read video size (w : $videoWidth, h : $videoHeight)"
        )
        null
    }
}
