package d.spidchenko.sampledaydream

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent

private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: DreamRenderer
    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = DreamRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        // Render the view only when there is a change in the drawing data.
        // To allow the triangle to rotate automatically, this line is commented out:
//        renderMode = RENDERMODE_WHEN_DIRTY
//        Log.d("LOG_TAG", "hardware accelerated? :$isHardwareAccelerated:")
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(e: MotionEvent): Boolean {
//        // MotionEvent reports input details from the touch screen
//        // and other input controls. In this case, you are only
//        // interested in events where the touch position changed.
//
//        val x: Float = e.x
//        val y: Float = e.y
//
//        when (e.action) {
//            MotionEvent.ACTION_MOVE -> {
////                Log.d("LOG_TAG", "hardware accelerated? :$isHardwareAccelerated:")
//
//                var dx: Float = x - previousX
//                var dy: Float = y - previousY
//
//                // reverse direction of rotation above the mid-line
//                if (y > height / 2) {
//                    dx *= -1
//                }
//
//                // reverse direction of rotation to left of the mid-line
//                if (x < width / 2) {
//                    dy *= -1
//                }
//
//                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
//                requestRender()
//            }
//        }
//
//        previousX = x
//        previousY = y
//        return true
//    }


}