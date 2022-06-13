package d.spidchenko.sampledaydream

import android.graphics.RectF
import java.util.*


class Star(screenX: Int, screenY: Int) {
    // TODO A lot of Random objects will be created, need refactoring
    private var mX = Random().nextFloat() * screenX
    private var mY = Random().nextFloat() * screenY
    private val diameter = Random().nextFloat() * STAR_MAX_SIZE
    private var mXVelocity =
        -Random().nextFloat() * STAR_MAX_VELOCITY + STAR_MAX_VELOCITY - STAR_MIN_VELOCITY
    private var mYVelocity = 0
    val mRect = RectF(mX, mY, mX + diameter , mY + diameter)


    fun update(fps: Long) {
        with(mRect){
            left += (mXVelocity / fps)
            top += (mYVelocity / fps)
            right = mRect.left + diameter
            bottom = mRect.top + diameter
        }
    }

    companion object {
        const val STAR_MAX_SIZE = 10F
        const val STAR_MIN_VELOCITY = 10F
        const val STAR_MAX_VELOCITY = 100F
    }
}