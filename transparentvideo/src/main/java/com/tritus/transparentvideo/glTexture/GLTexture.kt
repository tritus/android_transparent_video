package com.tritus.transparentvideo.glTexture

import android.view.TextureView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.flow.Flow

/**
 * This is an implementation of a composable that uses a TextureView to display an OpenGL surface.
 */
@Composable
internal fun GLTexture(
    modifier: Modifier = Modifier,
    renderer: Renderer,
    requestRender: Flow<Unit>
) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        GLTextureViewModel(renderer = renderer, requestRender = requestRender, scope = scope)
    }

    DisposableEffect(key1 = viewModel) {
        onDispose { viewModel.onDispose() }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                TextureView(it).apply {
                    surfaceTextureListener = viewModel
                    isOpaque = false
                }
            }
        )
    }
}
