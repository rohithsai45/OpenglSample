package com.example.openglsample

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class SceneView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    init {
        // use opengl es 2.0
        setEGLContextClientVersion(2)
        setRenderer(SceneRenderer(context))
//         renderMode = RENDERMODE_WHEN_DIRTY
    }
}