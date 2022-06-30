package d.spidchenko.sampledaydream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class Dream(context: Context) : SurfaceView(context), Runnable {

    private var isWorking = false
    private lateinit var thread: Thread
    private var currentFPS: Long = 0
    private var screenX: Int = 0
    private var screenY: Int = 0
    private var isSceneInitialized = false
    private val surfaceHolder: SurfaceHolder = holder
    private var canvas: Canvas? = null
    private val paint: Paint = Paint()
    private var stars: Array<Star>? = null


    override fun run() {
        while (isWorking) {
            val frameStartTime = System.currentTimeMillis()

            update()
            draw()

            val timeThisFrame = System.currentTimeMillis() - frameStartTime
            if (timeThisFrame > 0) {
                currentFPS = MILLIS_IN_SECOND / timeThisFrame
            }
        }
    }

    fun start() {
        Log.d(TAG, "start")
        isWorking = true
        thread = Thread(this)
        thread.start()
    }

    fun stop() {
        Log.d(TAG, "stop")
        isWorking = false
        thread.join()
    }

    private fun update() = stars?.forEach { it.update(currentFPS) }

    private fun draw() {
        if (surfaceHolder.surface.isValid) {
            if (!isSceneInitialized) {
                initialize2D()
                isSceneInitialized = true
            }
            canvas = surfaceHolder.lockCanvas()
            canvas?.drawColor(Color.BLACK)
            paint.color = Color.WHITE

            mStars?.forEach { mCanvas?.drawRect(it.mRect, mPaint) }

            if (surfaceHolder.surface.isValid) {
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun initialize2D() {
        screenX = surfaceHolder.surfaceFrame.width()
        screenY = surfaceHolder.surfaceFrame.height()
        stars = Array(100) { Star(screenX, screenY) }
    }

    companion object {
        private const val TAG = "Dream.LOG_TAG"
        private const val MILLIS_IN_SECOND = 1000
    }
}