package com.tritus.transparentvideo.video.transparent

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tritus.transparentvideo.TransparentVideoViewModel
import com.tritus.transparentvideo.glTexture.GLTexture
import com.tritus.transparentvideo.renderer.TransparentVideoRenderer

/**
 * Displays a video with an alpha channel.
 *
 * The source must be a video compatible with android 4.4+ (https://developer.android.com/guide/topics/media/media-formats)
 * The source must be a composition of two videos vertically superposed :
 * - The upper part of the video must display the rgb channels, without any alpha
 * - The lower part of the video must display the alpha mask in shades of grey
 *   (black -> alpha = 0f, white -> alpha = 1f) to apply to the rgb part.
 *
 *   |-----------------------|
 *   |                       |
 *   |                       |
 *   |       rgb video       |
 *   |                       |
 *   |                       |
 *   |-----------------------|
 *   |                       |
 *   |                       |
 *   |  alpha mask video     |
 *   |                       |
 *   |                       |
 *   |-----------------------|
 *
 *   Warning : This composable cannot display a video that has an alpha channel like transparent
 *   webm. It only blends rgb data with alpha data.
 */
@Composable
fun TransparentVideo(modifier: Modifier = Modifier, @RawRes source: Int, looping: Boolean = true) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { TransparentVideoViewModel(coroutineScope, context, source, looping) }
    DisposableEffect(key1 = viewModel) {
        onDispose { viewModel.onDispose() }
    }
    Box(modifier = modifier) {
        GLTexture(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = viewModel.state.aspectRatio),
            renderer = TransparentVideoRenderer(
                onSurfaceTextureCreated = { surface -> viewModel.onSurfaceTextureCreated(surface) }
            ),
            requestRender = viewModel.state.onFrameAvailable
        )
    }
}
