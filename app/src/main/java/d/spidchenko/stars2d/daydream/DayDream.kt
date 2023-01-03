package d.spidchenko.stars2d.daydream

import android.content.Intent
import android.content.IntentFilter
import android.service.dreams.DreamService
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.util.LoggerConfig
import d.spidchenko.stars2d.util.SoundEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "DayDream.LOG_TAG"
private const val TIME_30_MINUTES = 30 * 60 * 1000L

class DayDream : DreamService(), LifecycleOwner {

    private lateinit var gLView: DreamSurfaceView
    private val lifecycleRegistry = LifecycleRegistry(this)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = false
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences, SoundEngine(this))
        setContentView(gLView)
        registerReceiver(gLView.batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (LoggerConfig.ON) Log.d(TAG, "onAttachedToWindow: Registered batteryInfoReceiver")
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        exitAfter30Minutes()
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
        unregisterReceiver(gLView.batteryInfoReceiver)
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override fun getLifecycle() = lifecycleRegistry

    private fun exitAfter30Minutes() = lifecycleScope.launch {
        if (LoggerConfig.ON) Log.d(TAG, "Coroutine: finish timer started.")
        delay(TIME_30_MINUTES)
        if (LoggerConfig.ON) Log.d(TAG, "Coroutine: dream is about to finish NOW.")
        finish()
    }
}