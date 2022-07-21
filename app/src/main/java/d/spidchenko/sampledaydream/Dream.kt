package d.spidchenko.sampledaydream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class Dream(context: Context) : SurfaceView(context), Runnable, SurfaceHolder.Callback {

    private var isWorking = false
    private var isSurfaceCreated = false
    private lateinit var thread: Thread
    private var currentFPS: Long = 0
    private var screenX: Int = 0
    private var screenY: Int = 0
    private var isSceneInitialized = false
    private val surfaceHolder: SurfaceHolder = holder
    private var canvas: Canvas? = null
    private val paint: Paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = false
    }
    private var stars: List<Star>? = null


    override fun run() {
        surfaceHolder.addCallback(this)
        while (isWorking) {
            if (isSurfaceCreated) {
                val frameStartTime = System.currentTimeMillis()

                update()
                draw()

                val timeThisFrame = System.currentTimeMillis() - frameStartTime
                if (timeThisFrame > 0) {
                    currentFPS = MILLIS_IN_SECOND / timeThisFrame
                }
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
            }

            canvas =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    surfaceHolder.lockHardwareCanvas()
                } else {
                    surfaceHolder.lockCanvas()
                }

            canvas?.drawColor(Color.BLACK)

//            stars?.forEach { it.draw(canvas, paint) }

            if (surfaceHolder.surface.isValid) {
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun initialize2D() {
        screenX = surfaceHolder.surfaceFrame.width()
        screenY = surfaceHolder.surfaceFrame.height()
//        stars = MutableList(100) { Star(screenX, screenY) }
        isSceneInitialized = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isSurfaceCreated = true
        Log.d(TAG, "surfaceCreated: ")
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceCreated = false
        Log.d(TAG, "surfaceDestroyed: ")
    }

    companion object {
        private const val TAG = "Dream.LOG_TAG"
        private const val MILLIS_IN_SECOND = 1000
    }
}