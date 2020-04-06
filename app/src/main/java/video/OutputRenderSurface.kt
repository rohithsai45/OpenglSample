package video

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.Matrix
import android.renderscript.Float3
import android.renderscript.Matrix4f
import android.view.Surface
import com.example.openglsample.OpenglApplication
import com.example.openglsample.R
import com.example.openglsample.models.Texture
import com.example.openglsample.shaderutil.ShaderProgram
import com.example.openglsample.shaderutil.ShaderUtils
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin

/**
 * Holds state associated with a Surface used for MediaCodec decoder output.
 *
 *
 * The (width,height) constructor for this class will prepare GL, create a SurfaceTexture,
 * and then create a Surface for that SurfaceTexture.  The Surface can be passed to
 * MediaCodec.configure() to receive decoder output.  When a frame arrives, we latch the
 * texture with updateTexImage, then render the texture with GL to a pbuffer.
 *
 *
 * The no-arg constructor skips the GL preparation step and doesn't allocate a pbuffer.
 * Instead, it just creates the Surface and SurfaceTexture, and when a frame arrives
 * we just draw it on whatever surface is current.
 *
 *
 * By default, the Surface will be using a BufferQueue in asynchronous mode, so we
 * can potentially drop frames.
 */
class OutputRenderSurface(private val targetWidth: Int, private val targetHeight: Int) {

    private var mEGLDisplay = EGL14.EGL_NO_DISPLAY
    private var mEGLContext = EGL14.EGL_NO_CONTEXT
    private var mEGLSurface = EGL14.EGL_NO_SURFACE
    private var mSurfaceTexture: SurfaceTexture? = null

    /**
     * Creates an OutputSurface using the current EGL context (rather than establishing a
     * new one).  Creates a Surface that can be passed to MediaCodec.configure().
     */
    init {
        setup()
    }

    /**
     * Returns the Surface that we draw onto.
     */
    var surface: Surface? = null
        private set

    private var mTextureRender: GenericRenderer? = null
    private var texture: Texture? = null

    /**
     * dimensions for drawing frame
     */
    private val sourceWidth: Int = 360
    private val sourceHeight: Int = 640

    /**
     * Creates instances of TextureRender and SurfaceTexture, and a Surface associated
     * with the SurfaceTexture.
     */
    private fun setup() {

        mTextureRender = GenericRenderer(targetWidth, targetHeight)
        mTextureRender?.surfaceCreated()

        // Even if we don't access the SurfaceTexture after the constructor returns, we
        // still need to keep a reference to it.  The Surface doesn't retain a reference
        // at the Java level, so if we don't either then the object can get GCed, which
        // causes the native finalizer to run.
        mSurfaceTexture = SurfaceTexture(mTextureRender?.getTextureId() ?: 0)
        surface = Surface(mSurfaceTexture)

        // set Up text renderer
        setUpTextRenderer()
    }


    private fun setUpTextRenderer() {
        val shader = ShaderProgram(
            ShaderUtils.readShaderFileFromRawResource(OpenglApplication.instance, R.raw.simple_vertex_shader),
            ShaderUtils.readShaderFileFromRawResource(OpenglApplication.instance, R.raw.simple_fragment_shader)
        )
        texture = Texture(shader)
        texture?.position = Float3(0.0f, 0.0f, 0.0f)
        texture?.setTargetDimensions(targetWidth, targetHeight)
        texture?.apply {
            //val perspective = Matrix4f()
            //perspective.loadPerspective(85.0f, width.toFloat() / height.toFloat(), 1.0f, -150.0f)
            val ratio = targetWidth / targetHeight.toFloat()
            val matrix = FloatArray(16)
            Matrix.frustumM(matrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
            texture?.projection = Matrix4f(matrix)
        }
    }

    /**
     * Discard all resources held by this class, notably the EGL context.
     */
    fun release() {
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface)
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(mEGLDisplay)
        }
        surface?.release()
        // this causes a bunch of warnings that appear harmless but might confuse someone:
        //  W BufferQueue: [unnamed-3997-2] cancelBuffer: BufferQueue has been abandoned!
        mSurfaceTexture?.release()
        mEGLDisplay = EGL14.EGL_NO_DISPLAY
        mEGLContext = EGL14.EGL_NO_CONTEXT
        mEGLSurface = EGL14.EGL_NO_SURFACE
        mTextureRender = null
        surface = null
        mSurfaceTexture = null
    }

    /**
     * Draws the data from SurfaceTexture onto the current EGL surface.
     */
    fun drawImage(mediaPlayingTime: Int) {
        mSurfaceTexture?.updateTexImage()
        mSurfaceTexture?.let {
            mTextureRender?.drawFrame(
                it,
                sourceWidth,
                sourceHeight,
                targetWidth,
                targetHeight,
                mediaPlayingTime
            )
        }
        updateWithDelta(null, mediaPlayingTime.toLong())
    }

    private fun updateWithDelta(gl10: GL10?, currentTime: Long) {
        val xMovement = 0.1f * sin(System.currentTimeMillis() * 2 * Math.PI / (2.0f * 1000)).toFloat()
        val yMovement = 0.2f * sin(System.currentTimeMillis() * 2 * Math.PI / (3.0f * 1000)).toFloat()


        val viewMatrix = FloatArray(16)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        val camera = Matrix4f(viewMatrix)

        camera.translate(xMovement, yMovement, 0f)
        camera.rotate(360.0f * xMovement, 0.0f, 0.0f, 1.0f)
        //camera.scale(xMovement, 1f, 1f)
        texture?.camera = camera
        texture?.draw(null, currentTime)
    }
}