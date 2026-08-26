package d.spidchenko.stars2d.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

private const val TAG = "SoundEngine"
private const val DEFAULT_VOLUME = 1f

class SoundEngine(context: Context) {
    private val soundPool: SoundPool
    private var popId: Int = -1
    private var isLoaded = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2) // Allow slight overlap if pops happen rapidly
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                isLoaded = true
            } else {
                Log.e(TAG, "Failed to load sound, status: $status")
            }
        }

        try {
            val descriptor = context.assets.openFd("bubble-pop.ogg")
            popId = soundPool.load(descriptor, 1)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading audio asset", e)
        }
    }

    fun playPop(volume: Float = DEFAULT_VOLUME) {
        if (isLoaded && popId != -1) {
            soundPool.play(popId, volume, volume, 1, 0, 1f)
        }
    }

    /**
     * Must be called to free native resources when the engine is no longer needed.
     */
    fun release() {
        soundPool.release()
    }
}