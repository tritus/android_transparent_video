package com.tritus.transparentvideo

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Surface
import androidx.annotation.RawRes
import com.tritus.transparentvideo.media.createMediaPlayer
import com.tritus.transparentvideo.media.extractVideoRatio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal class TransparentVideoViewModel(
    private val scope: CoroutineScope,
    context: Context,
    @RawRes source: Int,
    looping: Boolean
) {
    private val onFrameAvailable = MutableSharedFlow<Unit>(extraBufferCapacity = UNLIMITED)

    val state = State(
        aspectRatio = extractVideoRatio(context, source) ?: 1f,
        onFrameAvailable = onFrameAvailable
    )

    private val mediaPlayer = createMediaPlayer(context, source, looping)
    private var mediaPlayerSurface: Surface? = null

    fun onDispose() {
        mediaPlayer.release()
        mediaPlayerSurface?.release()
    }

    fun onSurfaceTextureCreated(surfaceTexture: SurfaceTexture) {
        surfaceTexture.setOnFrameAvailableListener { onFrameAvailable.tryEmit(Unit) }
        val surface = Surface(surfaceTexture).also { mediaPlayerSurface = it }
        scope.launch(Dispatchers.Main) {
            mediaPlayer.setVideoSurface(surface)
            mediaPlayer.prepare()
            mediaPlayer.play()
        }
    }

    class State(
        val aspectRatio: Float,
        val onFrameAvailable: Flow<Unit>
    )
}
