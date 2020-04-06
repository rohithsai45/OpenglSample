package video

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.opengl.Matrix
import com.example.openglsample.shaderutil.readText
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


open class GenericRenderer(targetWidth: Int, targetHeight: Int) {

    protected var VERTEX_SHADER =  readText("generic_animation_vertx_shader.glsl")
    protected var FRAGMENT_SHADER = readText("generic_animation_fragment_shader.glsl")

    private val FLOAT_SIZE_BYTES = 4
    private val TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES
    private val TRIANGLE_VERTICES_DATA_POS_OFFSET = 0
    private val TRIANGLE_VERTICES_DATA_UV_OFFSET = 3
    private val mTriangleVerticesData = floatArrayOf(
        // X, Y, Z, U, V
        -1.0f, 1.0f, 0f, 0f, 0f,
        -1.0f, -1.0f, 0f, 0f, 1f,
        1.0f, 1.0f, 0f, 1f, 0f,
        1.0f, -1.0f, 0f, 1f, 1f
    )

    private var mTriangleVertices: FloatBuffer? = null

    //matrix and texture handles
    private var muMVPMatrixHandle: Int = 0
    private var muSTMatrixHandle: Int = 0
    private var aPositionHandle: Int = 0
    private var aTextureHandle: Int = 0

    private var resolutionHandle: Int = 0
    private var timeHandle: Int = 0


    private var mExternalTextureId = -12345
    protected var mProgram: Int = 0

    private val mMVPMatrix = FloatArray(16)
    private val mSTMatrix = FloatArray(16)

    init {
        mTriangleVertices = ByteBuffer.allocateDirect(
                mTriangleVerticesData.size * FLOAT_SIZE_BYTES
            )
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTriangleVertices?.put(mTriangleVerticesData)?.position(0)
        Matrix.setIdentityM(mSTMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)
    }

    //open

    open fun drawFrame(
        st: SurfaceTexture,
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
        mediaPlayingTime: Int
    ) {

        st.getTransformMatrix(mSTMatrix)

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)  //black background
        glClear(GL_DEPTH_BUFFER_BIT or GL_COLOR_BUFFER_BIT)
        glUseProgram(mProgram)

        glUniform1f(timeHandle, mediaPlayingTime / 1000f)
        glUniform2f(resolutionHandle, targetWidth.toFloat(), targetHeight.toFloat())

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mExternalTextureId)

        mTriangleVertices?.position(TRIANGLE_VERTICES_DATA_POS_OFFSET)

        glEnableVertexAttribArray(aPositionHandle)
        glBindBuffer(GL_ARRAY_BUFFER, aPositionHandle)
        glVertexAttribPointer(
            aPositionHandle, 3, GL_FLOAT, false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices
        )

        glEnableVertexAttribArray(aTextureHandle)
        mTriangleVertices?.position(TRIANGLE_VERTICES_DATA_UV_OFFSET)
        glVertexAttribPointer(
            aTextureHandle, 2, GL_FLOAT, false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices
        )

        //to be done in each renderer
        glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0)

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)

        glDisableVertexAttribArray(aPositionHandle)
        glDisableVertexAttribArray(aTextureHandle)

        glFinish()
    }

    /**
     * Initializes GL state.  Call this after the EGL surface has been created and made current.
     */
    open fun surfaceCreated() {
        mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        aPositionHandle = glGetAttribLocation(mProgram, "aPosition")
        muMVPMatrixHandle = glGetUniformLocation(mProgram, "uMVPMatrix")
        muSTMatrixHandle = glGetUniformLocation(mProgram, "uSTMatrix")
        aTextureHandle = glGetAttribLocation(mProgram, "aTextureCoord")
        resolutionHandle = glGetUniformLocation(mProgram, "u_resolution")
        timeHandle = glGetUniformLocation(mProgram, "u_time")

        //generate external texture for camera frame
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)
        mExternalTextureId = textures[0]
        bindTexture(mExternalTextureId, GL_TEXTURE0, isExternalTexture = true)
    }

    //public

    fun getTextureId(): Int {
        return mExternalTextureId
    }

    fun checkGlError(op: String) {
        var error: Int
        while (true) {
            error = glGetError()
            if (error == GL_NO_ERROR) break
            val errorString = "$op: glError $error " +
                    "program:$mProgram " +
                    "ExternalTextureId:$mExternalTextureId " +
                    "PositionHandle:$aPositionHandle " +
                    "TextureHandle:$aTextureHandle"
            //logNonFatalError(RuntimeException(errorString))
        }
    }

    fun bindTexture(textureId: Int, texture: Int, isExternalTexture: Boolean = false) {
        glActiveTexture(texture)
        val texConstant =
            if (isExternalTexture) GLES11Ext.GL_TEXTURE_EXTERNAL_OES else GL_TEXTURE_2D
        glBindTexture(texConstant, textureId)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST.toFloat())
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    }

    /*@SuppressLint("InflateParams")
    fun getTextBitmap(resourceId: Int, viewId: Int, data: AnimationData, isTitle: Boolean = true): Bitmap? {
        val inflatedView = LayoutInflater.from(RizzleApplication.instance).inflate(resourceId, null)
        val textView = inflatedView.findViewById<TextView>(viewId)
        textView.text = if (isTitle) data.title else data.subtitle
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isTitle) getTextSizeForChannelTitle(data) else getTextSizeForChannelSubtitle(data))
        return inflatedView.toBitmap()
    }*/

    //private

    private fun loadShader(shaderType: Int, source: String): Int {
        var shader = glCreateShader(shaderType)
        glShaderSource(shader, source)
        glCompileShader(shader)
        val compiled = IntArray(1)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            glDeleteShader(shader)
            shader = 0
        }
        return shader
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource)
        if (pixelShader == 0) {
            return 0
        }
        var program = glCreateProgram()
        if (program == 0) {
        }
        glAttachShader(program, vertexShader)
        glAttachShader(program, pixelShader)
        glLinkProgram(program)
        val linkStatus = IntArray(1)
        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GL_TRUE) {
            glDeleteProgram(program)
            program = 0
        }
        return program
    }


}