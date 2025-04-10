package d.spidchenko.stars2d.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

private const val DEFAULT_VOLUME = 1F

class SoundEngine(context: Context) {
    private val soundPool: SoundPool
    private val popId: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        val assetManager = context.assets
        val descriptor = assetManager.openFd("bubble-pop.ogg")
        popId = soundPool.load(descriptor, 0)
    }

    fun playPop() = playSoundById(popId)

    private fun playSoundById(soundId: Int) {
        val priority = 0
        val loopMode = 0
        val rate = 1F
        soundPool.play(soundId, DEFAULT_VOLUME, DEFAULT_VOLUME, priority, loopMode, rate)
    }
}