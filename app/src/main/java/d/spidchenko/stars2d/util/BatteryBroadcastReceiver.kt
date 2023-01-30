package d.spidchenko.stars2d.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.Toast
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.R

class BatteryBroadcastReceiver(
    private val soundEngine: SoundEngine,
    private val vibrateUtil: VibrateUtil,
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val playSound = sharedPreferences.getBoolean("play_sound", false)
        val vibrate = sharedPreferences.getBoolean("vibrate", false)

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val statusFull = BatteryManager.BATTERY_STATUS_FULL
        val statusNotCharging = BatteryManager.BATTERY_STATUS_NOT_CHARGING

        val isCharged = status == statusNotCharging || status == statusFull
        if (isCharged) {
            if (playSound) soundEngine.playPop()
            if (vibrate) vibrateUtil.vibrate(context)
            Toast.makeText(context, context.getString(R.string.device_is_charged), Toast.LENGTH_SHORT).show()
        }
    }
}