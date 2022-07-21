package d.spidchenko.sampledaydream

import android.opengl.GLSurfaceView
import android.service.dreams.DreamService
import android.util.Log

private const val TAG = "DreamActivity.LOG_TAG"

class DreamActivity : DreamService() {

//    private lateinit var dream: Dream
    private lateinit var gLView: GLSurfaceView

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        gLView = MyGLSurfaceView(this)
        setContentView(gLView)
//        dream = Dream(this)
//        val dreamLayout = findViewById<LinearLayout>(R.id.DayDreamLayout)
//        dreamLayout.addView(dream)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        Log.d(TAG, "onDreamingStarted: ")
//        dream.start()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        Log.d(TAG, "onDreamingStopped: ")
//        dream.stop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Log.d(TAG, "onDetachedFromWindow: ")
    }
}