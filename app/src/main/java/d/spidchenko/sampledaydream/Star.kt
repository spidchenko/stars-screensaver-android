package d.spidchenko.sampledaydream

import android.graphics.RectF
import android.util.Log
import kotlin.random.Random
import kotlin.random.nextInt


class Star(
    private val screenX: Int,
    private val screenY: Int
) {
    private var x = Random.nextInt(screenX).toFloat()
    private var y = Random.nextInt(screenY).toFloat()
    private val diameter = Random.nextDouble(STAR_MIN_SIZE, STAR_MAX_SIZE).toFloat()
    private val xVelocity: Float
    private val yVelocity: Float = 0F
    private val rect = RectF(x, y, x + diameter, y + diameter)

    init {
        val minVelocity: Int = screenX / MIN_VELOCITY_DIVIDER
        val maxVelocity: Int = screenX / MAX_VELOCITY_DIVIDER
        xVelocity = -Random.nextInt(minVelocity..maxVelocity).toFloat()
    }

    fun update(fps: Long) {
        if (mRect.left < 0) {
            respawn()
        }
        with(mRect) {
            left += (mXVelocity / fps)
//            top += (mYVelocity / fps)
            right = mRect.left + diameter
            bottom = mRect.top + diameter
        }
    }

    private fun respawn() {
        mRect.left = screenX.toFloat()
        mRect.top = Random.nextFloat() * screenY
    }

    companion object {
        private const val TAG = "Star.LOG_TAG"
        const val STAR_MIN_SIZE = 0.9
        const val STAR_MAX_SIZE = 4.0
        const val MIN_VELOCITY_DIVIDER = 8
        const val MAX_VELOCITY_DIVIDER = 3
    }
}