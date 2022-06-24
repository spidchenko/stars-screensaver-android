package d.spidchenko.sampledaydream

import android.graphics.RectF
import android.util.Log
import kotlin.random.Random


class Star(
    private val screenX: Int,
    private val screenY: Int
) {
    private var mX = Random.nextFloat() * screenX
    private var mY = Random.nextFloat() * screenY
    private val diameter = Random.nextFloat() * STAR_MAX_SIZE
    private var mXVelocity =
        -(Random.nextFloat() * STAR_MAX_VELOCITY + STAR_MAX_VELOCITY - STAR_MIN_VELOCITY)
    private var mYVelocity = 0
    val mRect = RectF(mX, mY, mX + diameter, mY + diameter)

    init {
        Log.d(TAG, "Star created: x:$mX y:$mY speed:$mXVelocity")
    }

    fun update(fps: Long) {
        if (mRect.left < -100) {
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
        mRect.left = screenX.toFloat() + 100
        mRect.top = Random.nextFloat() * screenY
    }

    companion object {
        private const val TAG = "Star.LOG_TAG"
        const val STAR_MAX_SIZE = 3F
        const val STAR_MIN_VELOCITY = 10F
        const val STAR_MAX_VELOCITY = 150F
    }
}