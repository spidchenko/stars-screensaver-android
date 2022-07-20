package d.spidchenko.sampledaydream

import android.opengl.GLSurfaceView
import android.service.dreams.DreamService

class DreamActivity : DreamService() {

    private lateinit var dream: Dream
    private lateinit var gLView: GLSurfaceView

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = true
        isFullscreen = true
        gLView = MyGLSurfaceView(this)
        setContentView(gLView)
//        dream = Dream(this)
//        val dreamLayout = findViewById<LinearLayout>(R.id.DayDreamLayout)
//        dreamLayout.addView(dream)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
//        dream.start()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
//        dream.stop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}