package d.spidchenko.sampledaydream.daydream

import android.opengl.GLSurfaceView
import android.service.dreams.DreamService
import android.util.Log
import android.view.WindowManager
import d.spidchenko.sampledaydream.util.LoggerConfig

private const val TAG = "DayDream.LOG_TAG"

class DayDream : DreamService() {

    private lateinit var gLView: GLSurfaceView

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = true
//        WindowManager.LayoutParams params = getWindow
        gLView = DreamSurfaceView(this)
        setContentView(gLView)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        if (LoggerConfig.ON) {
            Log.d(TAG, "onDreamingStarted: ")
        }
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        if (LoggerConfig.ON) {
            Log.d(TAG, "onDreamingStopped: ")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (LoggerConfig.ON) {
            Log.d(TAG, "onDetachedFromWindow: ")
        }
    }
}