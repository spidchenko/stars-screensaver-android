package d.spidchenko.sampledaydream

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
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
    private val rect = RectF(x, y, x + diameter, y + diameter)

    init {
        val minVelocity: Double = screenX / MIN_VELOCITY_DIVIDER
        val maxVelocity: Double = screenX / MAX_VELOCITY_DIVIDER
        xVelocity = -Random.nextDouble(minVelocity, maxVelocity).toFloat()
    }

    fun update(fps: Long) =
        if (rect.left < 0) respawn() else rect.offset(xVelocity / fps, yVelocity / fps)

    fun draw(canvas: Canvas?, paint: Paint) =
        canvas?.let { canvas.drawRect(rect, paint) }

    private fun respawn() =
        rect.offsetTo(screenX.toFloat(), Random.nextInt(screenY).toFloat())

    companion object {
        const val STAR_MIN_SIZE = 0.9
        const val STAR_MAX_SIZE = 4.0
        const val MIN_VELOCITY_DIVIDER = 8.0
        const val MAX_VELOCITY_DIVIDER = 3.0
    }
}