package com.oculus.sample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.oculus.sample.player.SphericalVideoPlayer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class SphericalPlayerActivity : AppCompatActivity() {

    private lateinit var videoPlayer: SphericalVideoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val s = intent.extras?.getString("url")
        val sampleVideoPath = s ?: "android.resource://com.oculus.sample/raw/" + R.raw.closer
        setContentView(R.layout.activity_main)
        videoPlayer = findViewById(R.id.spherical_video_player)
        videoPlayer.setVideoURIPath(sampleVideoPath)
        videoPlayer.playWhenReady()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestExternalStoragePermission()
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            init()
        }
    }

    private fun init() {
        videoPlayer.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                videoPlayer.initRenderThread(surface, width, height)
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                videoPlayer.releaseResources()
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
        videoPlayer.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            return
        }
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            toast(this, "Access not granted for reading video file :(")
            return
        }
        init()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x1
        fun toast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun readRawTextFile(context: Context, resId: Int): String? {
            val `is` = context.resources.openRawResource(resId)
            val reader = InputStreamReader(`is`)
            val buf = BufferedReader(reader)
            val text = StringBuilder()
            try {
                var line: String?
                while (buf.readLine().also { line = it } != null) {
                    text.append(line).append('\n')
                }
            } catch (e: IOException) {
                return null
            }
            return text.toString()
        }
    }
}
