package com.example.openglsample

import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import video.AnimationRenderer

class MainActivity : AppCompatActivity() {

    private var renderer: AnimationRenderer? = null
    private var rendererListener: AnimationRenderer.Listener? = null
    val handler = Handler()


    private val requestRenderRunnable = Runnable {
        val view = video_view_wrapper?.getChildAt(0)
        (view as? GLSurfaceView)?.requestRender()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onResume() {
        super.onResume()
        setupAnimationRenderer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayers()
    }

    private fun releasePlayers() {
        val view = video_view_wrapper?.getChildAt(0)
        (view as? GLSurfaceView)?.onPause()
        renderer?.release()
        rendererListener = null
        handler.removeCallbacks(requestRenderRunnable)
    }

    private fun setupAnimationRenderer() {
        renderer = AnimationRenderer()
        rendererListener = object : AnimationRenderer.Listener() {
            override fun onFrameDrawn() {
                handler.post(requestRenderRunnable)
            }

            override fun onPlayerReady(videoPlayer: MediaPlayer?, audioPlayer: MediaPlayer?) {
                initMediaPlayers(videoPlayer, audioPlayer)
            }

            override fun onSurfaceUpdated(width: Int, height: Int) {
            }
        }
        video_view_wrapper.removeAllViews()
        val surfaceView = GLSurfaceView(this)
        surfaceView.setEGLContextClientVersion(2)
        //surfaceView.id = R.id.video_view
        surfaceView.setRenderer(renderer)
        surfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        video_view_wrapper.addView(surfaceView)

        renderer?.setUp(rendererListener)
    }

    private fun initMediaPlayers(videoPlayer: MediaPlayer?, audioPlayer: MediaPlayer?) {
        prepareVideoPlayer(videoPlayer)
    }

    private fun prepareVideoPlayer(videoPlayer: MediaPlayer?) {
        videoPlayer?.reset()
        val assetFileDescriptor = OpenglApplication.instance.assets.openFd("channel_introduction_1.mp4")
        videoPlayer?.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        assetFileDescriptor.close()
        videoPlayer?.prepare()
        videoPlayer?.start()
        //videoPlayer?.setVolume(0.2f, 0.2f)
        videoPlayer?.setOnCompletionListener {
            videoPlayer.start()
        }
    }




}
