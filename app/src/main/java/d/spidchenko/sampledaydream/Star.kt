package d.spidchenko.sampledaydream

import android.graphics.RectF
import android.util.Log
import kotlin.random.Random
import kotlin.random.nextInt


class Star(
    private val screenX: Int,
    private val screenY: Int
) {
    private var mX = Random.nextInt(screenX).toFloat()
    private var mY = Random.nextInt(screenY).toFloat()
    private val diameter = Random.nextDouble(STAR_MAX_SIZE).toFloat()
    private val mXVelocity: Float
    private var mYVelocity: Float = 0F
    val mRect = RectF(mX, mY, mX + diameter, mY + diameter)

    init {
        val minVelocity: Int = screenX / MIN_VELOCITY_DIVIDER
        val maxVelocity: Int = screenX / MAX_VELOCITY_DIVIDER
        mXVelocity = -Random.nextInt(minVelocity..maxVelocity).toFloat()
        Log.d(TAG, ": Velocity range: $minVelocity..$maxVelocity")
        Log.d(TAG, "Star created: x:$mX y:$mY speed:$mXVelocity")
    }

    fun update(fps: Long) {
        if (mRect.left < 0) {
            respawn()
        }
        with(mRect) {
            left += (mXVelocity / fps)
            top += (mYVelocity / fps)
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
        const val STAR_MAX_SIZE = 3.0
        const val MIN_VELOCITY_DIVIDER = 8
        const val MAX_VELOCITY_DIVIDER = 3
    }
}