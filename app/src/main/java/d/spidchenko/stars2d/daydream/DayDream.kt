package d.spidchenko.stars2d.daydream

import android.content.Intent
import android.content.IntentFilter
import android.service.dreams.DreamService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TIME_30_MINUTES = 3 * 60 * 1000L

class DayDream : DreamService(), LifecycleOwner {

    private lateinit var gLView: DreamSurfaceView
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val soundEngine by lazy { SoundEngine(this) }
    private val batteryInfoReceiver by lazy { BatteryBroadcastReceiver(soundEngine, VibrateUtil) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Logger.log("onAttachedToWindow")
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        Logger.log("onDreamingStarted")
        isInteractive = false
        isFullscreen = true
        // TODO Add this to settings:
        isScreenBright = false
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences)
        setContentView(gLView)
        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        Logger.log("onDreamingStarted: Registered batteryInfoReceiver")
        val isPremium = Billing.checkPremium(this)
        if (isPremium.not()) {
            lifecycleScope.launch { exitAfter30Minutes() }
            Logger.log("Screensaver will turn off after 30 minutes")
        } else {
            Logger.log("Screensaver is in premium mode")
        }
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        Logger.log("onDreamingStopped: ")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        gLView.releaseResources()
        unregisterReceiver(batteryInfoReceiver)
        Logger.log("onDetachedFromWindow: ")
    }

    private suspend fun exitAfter30Minutes() {
        Logger.log("Coroutine: finish timer started.")
        delay(TIME_30_MINUTES)
        Logger.log("Coroutine: dream is about to finish NOW.")
        finish()
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}