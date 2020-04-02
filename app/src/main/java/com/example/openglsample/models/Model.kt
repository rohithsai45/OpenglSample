package com.example.openglsample.models

import android.opengl.GLES20
import android.renderscript.Float3
import android.renderscript.Matrix4f
import com.example.openglsample.OpenglApplication
import com.example.openglsample.R
import com.example.openglsample.shaderutil.BufferUtils
import com.example.openglsample.shaderutil.ShaderProgram
import com.example.openglsample.shaderutil.TextureUtils
import com.example.openglsample.shaderutil.getTextBitmap
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10


open class Model(
    private val name: String,
    private val shader: ShaderProgram,
    vertices: FloatArray,
    indices: ShortArray
) {
    companion object {
        private const val COORDS_PER_VERTEX = 3
        private const val TEXCOORDS_PER_VERTEX = 2
        private const val COLORS_PER_VERTEX = 4
        private const val SIZE_OF_FLOAT = 4
        private const val SIZE_OF_SHORT = 2
    }

    private val vertices: FloatArray = vertices.copyOfRange(0, vertices.size)
    private val indices: ShortArray = indices.copyOfRange(0, indices.size)
    private var vertexBuffer: FloatBuffer? = null
    private var vertexBufferId = 0
    private var vertexStride = 0
    private var indexBuffer: ShortBuffer? = null
    private var indexBufferId = 0

    private var titleWidth = -1
    private var titleHeight = -1

    private var targetWidth = -1
    private var targetHeight = -1

    // ModelView Transformation
    var position = Float3(0f, 0f, 0f)
    var rotationX = 0.0f
    var rotationY = 0.0f
    var rotationZ = 0.0f
    var scale = 1.0f
    var camera = Matrix4f()
    var projection = Matrix4f()
    var textureName = 0

    init {
        setUpTexture()
    }

    fun draw(gl: GL10?, currentTime: Long) {
        shader.begin()
        //gl?.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName)
        shader.setUniformi("u_Texture", 1)

        camera.multiply(modelMatrix())
        shader.setUniformMatrix("u_ProjectionMatrix", projection)
        shader.setUniformMatrix("u_ModelViewMatrix", camera)
        shader.setUniformf("u_time", currentTime.toFloat())
        //shader.setUniformf("u_resolution", targetWidth.toFloat(), targetHeight.toFloat())

        shader.enableVertexAttribute("a_Position")
        shader.setVertexAttribute(
            "a_Position",
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            0
        )
        shader.enableVertexAttribute("a_TexCoord")
        shader.setVertexAttribute(
            "a_TexCoord",
            TEXCOORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            (COORDS_PER_VERTEX + COLORS_PER_VERTEX) * SIZE_OF_FLOAT
        )

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,  // mode
            indices.size,  // count
            GLES20.GL_UNSIGNED_SHORT,  // type
            0
        ) // offset
        shader.disableVertexAttribute("a_Position")
        shader.disableVertexAttribute("a_TexCoord")

        shader.end()
    }

    fun setTargetDimensions(targetWidth: Int, targetHeight: Int) {
        this.targetWidth = targetWidth
        this.targetHeight = targetHeight
        setupVertexBuffer()
        setupIndexBuffer()
    }

    private fun modelMatrix(): Matrix4f {
        val mat = Matrix4f() // make a new identitiy 4x4 matrix
        mat.translate(position.x, position.y, position.z)
        mat.rotate(rotationX, 1.0f, 0.0f, 0.0f)
        mat.rotate(rotationY, 0.0f, 1.0f, 0.0f)
        mat.rotate(rotationZ, 0.0f, 0.0f, 1.0f)
        mat.scale(scale, scale, scale)
        return mat
    }

    private fun setupVertexBuffer() {

        updatePositionCoordinates()

        vertexBuffer = BufferUtils.newFloatBuffer(vertices.size)
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
        val buffer = IntBuffer.allocate(1)
        GLES20.glGenBuffers(1, buffer)
        vertexBufferId = buffer[0]
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            vertices.size * SIZE_OF_FLOAT,
            vertexBuffer,
            GLES20.GL_STATIC_DRAW
        )
        vertexStride =
            (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX) * SIZE_OF_FLOAT // 4 bytes per vertex
    }

    private fun updatePositionCoordinates() {
        val xPoint = titleWidth * 1f/ targetHeight // yes its right
        val yPoint = titleHeight * 1f/ targetHeight
        vertices[0] = -xPoint
        vertices[1] = yPoint

        vertices[1 * (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX)] = -xPoint
        vertices[1 * (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX) + 1] = -yPoint

        vertices[2 * (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX)] = xPoint
        vertices[2 * (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX) + 1] = -yPoint

        vertices[3 * (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX)] = xPoint
        vertices[3 * (COORDS_PER_VERTEX + COLORS_PER_VERTEX + TEXCOORDS_PER_VERTEX) + 1] = yPoint
    }

    private fun setupIndexBuffer() {
        // initialize index short buffer for index
        indexBuffer = BufferUtils.newShortBuffer(indices.size)
        indexBuffer?.put(indices)
        indexBuffer?.position(0)
        val buffer = IntBuffer.allocate(1)
        GLES20.glGenBuffers(1, buffer)
        indexBufferId = buffer[0]
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId)
        GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER,
            indices.size * SIZE_OF_SHORT,
            indexBuffer,
            GLES20.GL_STATIC_DRAW
        )
    }

    private fun setUpTexture() {
        val bitmap = getTextBitmap(
            R.layout.layout_channel_title_1,
            R.id.channel_title,
            OpenglApplication.instance
        )
        textureName = TextureUtils.loadTexture(bitmap).apply {
            titleWidth = bitmap!!.width
            titleHeight = bitmap.height
            bitmap.recycle()
        }
        GLES20.glActiveTexture(textureName)
        val texConstant = GLES20.GL_TEXTURE_2D
        GLES20.glBindTexture(texConstant, textureName)
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
    }

}