package d.spidchenko.stars2d.daydream

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.service.dreams.DreamService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TIME_30_MINUTES = 30 * 60 * 1000L
private const val KEEP_SCREEN_BRIGHT_KEY = "is_screen_bright"
private const val DEFAULT_KEEP_SCREEN_BRIGHT = true

class DayDream : DreamService(), LifecycleOwner {

    private var gLView: DreamSurfaceView? = null
    private val lifecycleRegistry = LifecycleRegistry(this)
    private var soundEngine: SoundEngine? = null
    private var batteryInfoReceiver: BatteryBroadcastReceiver? = null
    private var timerJob: Job? = null

    // Track registration state to prevent double registration/unregistration
    private var isReceiverRegistered = false
    private val receiverLock = Any()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Logger.log("onAttachedToWindow")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize resources that don't depend on the dream being started
        soundEngine = SoundEngine(this)
        batteryInfoReceiver = BatteryBroadcastReceiver(soundEngine!!, VibrateUtil)
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        Logger.log("onDreamingStarted")

        try {
            // Configure dream properties
            isInteractive = false
            isFullscreen = true

            // Load preferences
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            isScreenBright = preferences.getBoolean(KEEP_SCREEN_BRIGHT_KEY, DEFAULT_KEEP_SCREEN_BRIGHT)

            // Initialize GL view
            gLView = DreamSurfaceView(this, preferences)
            setContentView(gLView)

            // Safely register battery receiver
            registerBatteryReceiver()

            // Start timer for non-premium users
            val isPremium = Billing.checkPremium(this)
            if (!isPremium) {
                startTimer()
                Logger.log("Screensaver will turn off after 30 minutes")
            } else {
                Logger.log("Screensaver is in premium mode. There is no timer")
            }
        } catch (e: Exception) {
            Logger.log("Error in onDreamingStarted: ${e.message}")
            // Finish the dream if initialization fails
            finish()
        }
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        Logger.log("onDreamingStopped")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

        // Clean up resources when dreaming stops
        stopTimer()
        unregisterBatteryReceiver()

        // Release GL resources if view exists
        gLView?.releaseResources()
        gLView = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Logger.log("onDetachedFromWindow")
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        // Final cleanup - ensure everything is properly released
        cleanup()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.log("onDestroy")

        // Final safety cleanup
        cleanup()
        soundEngine = null
        batteryInfoReceiver = null
    }

    private fun registerBatteryReceiver() {
        synchronized(receiverLock) {
            if (!isReceiverRegistered && batteryInfoReceiver != null) {
                try {
                    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        registerReceiver(batteryInfoReceiver, filter, RECEIVER_NOT_EXPORTED)
                    } else {
                        registerReceiver(batteryInfoReceiver, filter)
                    }
                    isReceiverRegistered = true
                    Logger.log("Successfully registered batteryInfoReceiver")
                } catch (e: Exception) {
                    Logger.log("Failed to register battery receiver: ${e.message}")
                }
            }
        }
    }

    private fun unregisterBatteryReceiver() {
        synchronized(receiverLock) {
            if (isReceiverRegistered && batteryInfoReceiver != null) {
                try {
                    unregisterReceiver(batteryInfoReceiver)
                    isReceiverRegistered = false
                    Logger.log("Successfully unregistered batteryInfoReceiver")
                } catch (e: IllegalArgumentException) {
                    // Receiver was not registered or already unregistered
                    Logger.log("Receiver was not registered or already unregistered: ${e.message}")
                } catch (e: Exception) {
                    Logger.log("Error unregistering receiver: ${e.message}")
                } finally {
                    isReceiverRegistered = false
                }
            }
        }
    }

    private fun startTimer() {
        // Cancel any existing timer first
        stopTimer()

        timerJob = lifecycleScope.launch {
            try {
                Logger.log("Coroutine: finish timer started.")
                delay(TIME_30_MINUTES)
                Logger.log("Coroutine: dream is about to finish NOW.")
                finish()
            } catch (e: Exception) {
                Logger.log("Timer coroutine error: ${e.message}")
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        Logger.log("Timer stopped")
    }

    private fun cleanup() {
        // Stop any running timer
        stopTimer()

        // Unregister receiver if needed
        unregisterBatteryReceiver()

        // Release GL resources
        gLView?.releaseResources()
        gLView = null
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}