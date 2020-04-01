package com.example.openglsample

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.renderscript.Float3
import android.renderscript.Matrix4f
import com.example.openglsample.models.Model
import com.example.openglsample.models.Texture
import com.example.openglsample.shaderutil.ShaderProgram
import com.example.openglsample.shaderutil.ShaderUtils
import com.example.openglsample.shaderutil.TextureUtils
import com.example.openglsample.shaderutil.getTextBitmap
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin

class SceneRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var texture: Model? = null
    private var lastTimeMillis = 0L


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val shader = ShaderProgram(
            ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_vertex_shader),
            ShaderUtils.readShaderFileFromRawResource(context, R.raw.simple_fragment_shader)
        )
        texture = Texture(shader)
        texture?.position = Float3(0.0f, 0.0f, 0.0f)
        texture?.textureName = TextureUtils.loadTexture(
            getTextBitmap(
                R.layout.layout_channel_title_1,
                R.id.channel_title,
                context
            )
        )
        lastTimeMillis = System.currentTimeMillis()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(
            0,
            0,
            width,
            height
        ) // tried changing width and height values no effect of viewport here :(
        texture?.apply {
            //val perspective = Matrix4f()
            //perspective.loadPerspective(85.0f, width.toFloat() / height.toFloat(), 1.0f, -150.0f)
            val ratio = width / height.toFloat()
            val matrix = FloatArray(16)
            Matrix.frustumM(matrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
            texture?.projection = Matrix4f(matrix)
        }
    }


    override fun onDrawFrame(gl10: GL10?) {
        GLES20.glClearColor(0.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val currentTimeMillis = System.currentTimeMillis()
        updateWithDelta(currentTimeMillis - lastTimeMillis)
        lastTimeMillis = currentTimeMillis
    }

    private fun updateWithDelta(dt: Long) {
        val xMovement =
            0.1f * sin(System.currentTimeMillis() * 2 * Math.PI / (3.0f * 1000)).toFloat()
        val yMovement =
            -0.2f * sin(System.currentTimeMillis() * 2 * Math.PI / (2.0f * 1000)).toFloat()

        val viewMatrix = FloatArray(16)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        val camera = Matrix4f(viewMatrix)

        camera.translate(xMovement, yMovement, 0f)
        camera.rotate(360.0f * xMovement, 0.0f, 0.0f, 1.0f)
        //camera.scale(movement, movement, movement)
        texture?.camera = camera
        texture?.draw(dt)
    }

}