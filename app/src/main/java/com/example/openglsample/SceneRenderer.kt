package com.example.openglsample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.openglsample.models.Square
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SceneRenderer(private val context: Context) : GLSurfaceView.Renderer{

    private var square: Square? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        square = Square(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height) // tried changing width and height values no effect of viewport here :(
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        square?.draw()
    }

}