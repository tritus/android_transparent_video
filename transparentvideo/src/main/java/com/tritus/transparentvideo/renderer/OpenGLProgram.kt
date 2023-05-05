package com.tritus.transparentvideo.renderer

import android.graphics.SurfaceTexture

internal class OpenGLProgram(
    val nativeProgram: Int,
    val positionInSurfaceHandle: Int,
    val positionInTextureHandle: Int,
    val surfaceTransformMatrixHandle: Int,
    val surfaceTexture: SurfaceTexture
)
