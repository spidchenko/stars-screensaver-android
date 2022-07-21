package d.spidchenko.sampledaydream

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.random.Random


class Star(
    private val screenX: Int,
    private val screenY: Int
) {
    private var x = Random.nextInt(screenX).toFloat()
    private var y = Random.nextInt(screenY).toFloat()
    private val diameter = Random.nextDouble(STAR_MIN_SIZE, STAR_MAX_SIZE).toFloat()
    private val xVelocity: Float
    private val yVelocity: Float = 0F

    init {
        val minVelocity: Double = screenX / MIN_VELOCITY_DIVIDER
        val maxVelocity: Double = screenX / MAX_VELOCITY_DIVIDER
        xVelocity = -Random.nextDouble(minVelocity, maxVelocity).toFloat()
    }

    fun update(fps: Long) =
        if (x < 0) {
            respawn()
        } else {
            x += xVelocity / fps
            y += yVelocity / fps
        }

    fun draw(canvas: Canvas?, paint: Paint) =
        canvas?.let { it.drawPoint(x, y, paint.apply { strokeWidth = diameter }) }

    private fun respawn() {
        x = screenX.toFloat()
        y = Random.nextInt(screenY).toFloat()
    }


    companion object {
        const val STAR_MIN_SIZE = 0.9
        const val STAR_MAX_SIZE = 4.0
        const val MIN_VELOCITY_DIVIDER = 8.0
        const val MAX_VELOCITY_DIVIDER = 3.0
    }
}