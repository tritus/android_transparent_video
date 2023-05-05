package com.tritus.transparentvideo.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import androidx.annotation.RawRes
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener

private const val ALLOWED_JOINING_TIME_MS = 0L
private const val MAX_DROPPED_FRAMES_TO_NOTIFY = 0

internal fun createMediaPlayer(
    context: Context,
    @RawRes source: Int,
    looping: Boolean
): ExoPlayer {
    val videoOnlyRenderersFactory = RenderersFactory { handler: Handler?,
            videoListener: VideoRendererEventListener?,
            _: AudioRendererEventListener?,
            _: TextOutput?,
            _: MetadataOutput? ->
        arrayOf(
            MediaCodecVideoRenderer(
                context,
                MediaCodecSelector.DEFAULT,
                ALLOWED_JOINING_TIME_MS,
                handler,
                videoListener,
                MAX_DROPPED_FRAMES_TO_NOTIFY
            )
        )
    }

    val matroskaExtractorFactory = ExtractorsFactory { arrayOf(MatroskaExtractor()) }
    return ExoPlayer.Builder(
        context,
        videoOnlyRenderersFactory,
        DefaultMediaSourceFactory(context, matroskaExtractorFactory)
    ).build()
        .apply {
            val resUri: Uri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + context.resources.getResourcePackageName(source) +
                    '/' + context.resources.getResourceTypeName(source) +
                    '/' + context.resources.getResourceEntryName(source)
            )
            setMediaItem(MediaItem.fromUri(resUri))
            if (looping) {
                repeatMode = REPEAT_MODE_ONE
            }
        }
}
