package com.example.openglsample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.renderscript.Float3
import com.example.openglsample.models.Square
import com.example.openglsample.shaderutil.ShaderProgram
import com.example.openglsample.shaderutil.ShaderUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SceneRenderer(private val context: Context) : GLSurfaceView.Renderer{

    private var square: Square? = null

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val shader = ShaderProgram(
            ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_vertex_shader),
            ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_fragment_shader)
        )
        square = Square(shader)
        val centerPosition = Float3(0.0f, 0.0f, 0.0f)
        square?.setPosition(centerPosition)
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