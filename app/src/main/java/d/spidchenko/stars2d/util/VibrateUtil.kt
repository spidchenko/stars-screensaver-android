package d.spidchenko.stars2d.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrateUtil {
    fun vibrate(context: Context) {
        val vibrator: Vibrator
        val vibratorManager: VibratorManager
        val vibratePattern = longArrayOf(500, 500)

        if (Build.VERSION.SDK_INT >= 31) {
            vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator
        } else {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, -1))
        } else {
            vibrator.vibrate(vibratePattern, -1)
        }
    }
}