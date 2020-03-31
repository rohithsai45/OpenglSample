package com.example.openglsample

import android.content.Context
import android.opengl.GLSurfaceView

class SceneView(context: Context) : GLSurfaceView(context) {

    init {
        // use opengl es 2.0
        setEGLContextClientVersion(2)
        setRenderer(SceneRenderer(context))
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}