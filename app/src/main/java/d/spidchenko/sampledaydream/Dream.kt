package d.spidchenko.sampledaydream

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class Dream(context: Context) : SurfaceView(context), Runnable {

    private var mWorking = false
    private lateinit var mThread: Thread
    private var mCurrentFPS: Long = 0
    private var mScreenX: Int = 0
    private var mScreenY: Int = 0
    private var mSceneInitialized = false
    private val mSurfaceHolder: SurfaceHolder = holder
    private var mCanvas: Canvas? = null
    private val mPaint: Paint = Paint()
    private var mStars: Array<Star>? = null


    override fun run() {
        while (mWorking) {
            val frameStartTime = System.currentTimeMillis()

            update()
            draw()

            val timeThisFrame = System.currentTimeMillis() - frameStartTime
            if (timeThisFrame > 0) {
                mCurrentFPS = MILLIS_IN_SECOND / timeThisFrame
            }
        }
    }

    fun start() {
        Log.d(TAG, "start")
        mWorking = true
        mThread = Thread(this)
        mThread.start()
    }

    fun stop() {
        Log.d(TAG, "stop")
        mWorking = false
        mThread.join()
    }

    private fun update() = mStars?.forEach { it.update(mCurrentFPS) }

    private fun draw() {
        if (holder.surface.isValid) {
            if (!mSceneInitialized) {
                initialize2D()
                mSceneInitialized = true
            }
            mCanvas = holder.lockCanvas()
            mCanvas?.drawColor(Color.BLACK)
            mPaint.color = Color.WHITE

            mStars?.forEach { mCanvas?.drawRect(it.mRect, mPaint) }

            mSurfaceHolder.unlockCanvasAndPost(mCanvas)
        }
    }

    private fun initialize2D() {
        mScreenX = mSurfaceHolder.surfaceFrame.width()
        mScreenY = mSurfaceHolder.surfaceFrame.height()
        mStars = Array(100) { Star(mScreenX, mScreenY) }
    }

    companion object {
        private const val TAG = "Dream.LOG_TAG"
        private const val MILLIS_IN_SECOND = 1000
    }
}