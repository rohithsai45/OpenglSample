package com.example.openglsample.models

import android.opengl.GLES20
import android.renderscript.Float3
import android.renderscript.Matrix4f
import com.example.openglsample.shaderutil.BufferUtils
import com.example.openglsample.shaderutil.ShaderProgram
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import java.util.*


open class Model(
    private val name: String,
    private val shader: ShaderProgram,
    vertices: FloatArray,
    indices: ShortArray
) {
    private val vertices: FloatArray = Arrays.copyOfRange(vertices, 0, vertices.size)
    private val indices: ShortArray = Arrays.copyOfRange(indices, 0, indices.size)
    private var vertexBuffer: FloatBuffer? = null
    private var vertexBufferId = 0
    private var vertexStride = 0
    private var indexBuffer: ShortBuffer? = null
    private var indexBufferId = 0

    // ModelView Transformation
    var position = Float3(0f, 0f, 0f)
    var rotationX = 0.0f
    var rotationY = 0.0f
    var rotationZ = 0.0f
    var scale = 1.0f
    var camera = Matrix4f()
    var projection = Matrix4f()
    var textureName = 0

    private fun setupVertexBuffer() {
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

    fun modelMatrix(): Matrix4f {
        val mat = Matrix4f() // make a new identitiy 4x4 matrix
        mat.translate(position.x, position.y, position.z)
        mat.rotate(rotationX, 1.0f, 0.0f, 0.0f)
        mat.rotate(rotationY, 0.0f, 1.0f, 0.0f)
        mat.rotate(rotationZ, 0.0f, 0.0f, 1.0f)
        mat.scale(scale, scale, scale)
        return mat
    }

    fun draw(dt: Long) {
        shader.begin()

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName)
        shader.setUniformi("u_Texture", 1)

        camera.multiply(modelMatrix())
        shader.setUniformMatrix("u_ProjectionMatrix", projection)
        shader.setUniformMatrix("u_ModelViewMatrix", camera)
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

    companion object {
        private const val COORDS_PER_VERTEX = 3
        private const val TEXCOORDS_PER_VERTEX = 2
        private const val COLORS_PER_VERTEX = 4
        private const val SIZE_OF_FLOAT = 4
        private const val SIZE_OF_SHORT = 2
    }

    init {
        setupVertexBuffer()
        setupIndexBuffer()
    }
}