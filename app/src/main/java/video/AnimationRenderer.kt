package video

import android.media.MediaPlayer
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class AnimationRenderer : GLSurfaceView.Renderer {

    private var listener: Listener? = null
    private var videoPlayer: MediaPlayer? = null
    private var audioPlayer: MediaPlayer? = null
    private var outputSurface: OutputRenderSurface? = null

    /*
    This value is used when segments are played sequentially in preview fragment to maintain the time of video.
     */
    var elapsedTime: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        outputSurface?.drawImage(elapsedTime + (videoPlayer?.currentPosition ?: 0))
        listener?.onFrameDrawn()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d("AnimationRenderer","onSurfaceChanged $width $height")
        outputSurface = OutputRenderSurface(width, height)
        videoPlayer?.setSurface(outputSurface?.surface)
        GLES20.glViewport(0, 0, width, height)
        listener?.onSurfaceUpdated(width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        listener?.onPlayerReady(videoPlayer, audioPlayer)
    }

    fun setUp(listener: Listener?) {
        this.videoPlayer = MediaPlayer()
        this.audioPlayer = MediaPlayer()
        this.listener = listener
    }

    fun release() {
        outputSurface?.release()
        outputSurface = null

        listener = null

        videoPlayer?.release()
        videoPlayer = null

        audioPlayer?.release()
        audioPlayer = null
    }

    abstract class Listener {
        abstract fun onPlayerReady(videoPlayer: MediaPlayer?, audioPlayer: MediaPlayer?)
        abstract fun onFrameDrawn()
        abstract fun onSurfaceUpdated(width: Int, height: Int)
    }


}