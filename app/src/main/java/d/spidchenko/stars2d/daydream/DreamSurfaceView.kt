package d.spidchenko.stars2d.daydream

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.GLSurfaceView
import android.os.BatteryManager
import d.spidchenko.stars2d.util.SoundEngine

private const val CHARGED_ACTION_KEY = "action_when_charged"


@SuppressLint("ViewConstructor")
class DreamSurfaceView(
    context: Context,
    preferences: SharedPreferences,
    soundEngine: SoundEngine? = null
) : GLSurfaceView(context) {
    private val renderer: DreamRenderer
    val batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if ((preferences.getString(CHARGED_ACTION_KEY, "none")).equals("play_sound")) {

                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val statusFull = BatteryManager.BATTERY_STATUS_FULL
                val statusNotCharging = BatteryManager.BATTERY_STATUS_NOT_CHARGING

                val isCharged = status == statusNotCharging || status == statusFull
                if (isCharged) {
                    soundEngine?.playPop()
                }
            }
        }
    }


    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        renderer = DreamRenderer(context, preferences)
        setRenderer(renderer)
    }

    fun reloadPreferences() {
        renderer.reloadPreferences()
    }

    fun releaseResources() {
        renderer.releaseResources()
    }
}