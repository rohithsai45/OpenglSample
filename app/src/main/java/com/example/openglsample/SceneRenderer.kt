package com.example.openglsample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.renderscript.Float3
import android.renderscript.Matrix4f
import com.example.openglsample.models.Square
import com.example.openglsample.shaderutil.ShaderProgram
import com.example.openglsample.shaderutil.ShaderUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin

class SceneRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var square: Square? = null
    private var lastTimeMillis = 0L


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val shader = ShaderProgram(
            ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_vertex_shader),
            ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_fragment_shader)
        )
        square = Square(shader)
        square?.position = Float3(0.0f, 0.0f, 0.0f)
        lastTimeMillis = System.currentTimeMillis()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(
            0,
            0,
            width,
            height
        ) // tried changing width and height values no effect of viewport here :(
        if (square != null) {
            val perspective = Matrix4f()
            perspective.loadPerspective(85.0f, width.toFloat() / height.toFloat(), 1.0f, -150.0f)
            //Matrix.frustumM(perspective.array, 0, -width.toFloat() / height.toFloat(), width.toFloat() / height.toFloat(), -1f, 1f, 3f, 7f)
            square?.projection = perspective
        }
    }


    override fun onDrawFrame(gl10: GL10?) {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        /*square?.setPosition(
            Float3(
                sin(System.currentTimeMillis() * Math.PI / 1000f).toFloat(),
                sin(System.currentTimeMillis() * Math.PI / 1500f).toFloat(),
                0f
            )
        )*/

        val currentTimeMillis = System.currentTimeMillis()
        updateWithDelta(currentTimeMillis - lastTimeMillis)
        lastTimeMillis = currentTimeMillis
    }

    private fun updateWithDelta(dt: Long) {
        val secsPerMove: Float = 3.0f * 1000
        val movement = sin(System.currentTimeMillis() * 2 * Math.PI / secsPerMove).toFloat()

        val camera = Matrix4f()
        camera.translate(0.0f, -1.0f * movement, -15.0f)
        //camera.rotate(360.0f * movement, 0.0f, 0.0f, 1.0f)
        //camera.scale(movement, movement, movement)
        square?.camera = camera
        square?.draw(dt)
    }

}