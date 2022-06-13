package d.spidchenko.sampledaydream

import android.service.dreams.DreamService
import android.widget.LinearLayout

class DreamActivity : DreamService() {

    private lateinit var mDream: Dream

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        setContentView(R.layout.activity_main)
        mDream = Dream(this)
        val dreamLayout = findViewById<LinearLayout>(R.id.DayDreamLayout)
        dreamLayout.addView(mDream)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        mDream.start()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        mDream.stop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}