package d.spidchenko.sampledaydream

import android.service.dreams.DreamService
import android.widget.LinearLayout

class DreamActivity : DreamService() {

    private lateinit var dream: Dream

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        setContentView(R.layout.activity_main)
        dream = Dream(this)
        val dreamLayout = findViewById<LinearLayout>(R.id.DayDreamLayout)
        dreamLayout.addView(dream)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        dream.start()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        dream.stop()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}