package d.spidchenko.sampledaydream

import android.service.dreams.DreamService

class MyDream : DreamService() {
    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)

    //    }
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true

        setContentView(R.layout.activity_main)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
}