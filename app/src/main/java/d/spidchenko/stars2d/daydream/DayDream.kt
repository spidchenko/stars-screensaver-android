package d.spidchenko.stars2d.daydream

import android.content.Intent
import android.content.IntentFilter
import android.service.dreams.DreamService
import android.util.Log
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.util.LoggerConfig
import d.spidchenko.stars2d.util.SoundEngine

private const val TAG = "DayDream.LOG_TAG"

class DayDream : DreamService() {

    private lateinit var gLView: DreamSurfaceView


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = false
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences, SoundEngine(this))
        setContentView(gLView)
        registerReceiver(gLView.batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (LoggerConfig.ON) {
            Log.d(TAG, "onAttachedToWindow: Registered batteryInfoReceiver")
        }
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
    }
}