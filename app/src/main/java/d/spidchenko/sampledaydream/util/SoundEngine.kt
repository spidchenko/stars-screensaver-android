package d.spidchenko.sampledaydream.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundEngine(context: Context) {
    private val soundPool: SoundPool
    private val popId: Int
    private val volume = 0.3F

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        val assetManager = context.assets
        val descriptor = assetManager.openFd("bubble-pop.ogg")
        popId = soundPool.load(descriptor, 0)
    }

    fun playPop() = playSoundById(popId)

    private fun playSoundById(soundId: Int) {
        val leftVolume = volume
        val rightVolume = volume
        val priority = 0
        val loopMode = 0
        val rate = 1F
        soundPool.play(soundId, leftVolume, rightVolume, priority, loopMode, rate)
    }
}