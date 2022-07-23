package d.spidchenko.sampledaydream

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val MILLIS_IN_SECOND = 1000
private const val TAG = "DreamRenderer.LOG_TAG"

class DreamRenderer : GLSurfaceView.Renderer {

    private val isDebugging = true

    var frameCounter = 0L
    var averageFPS = 0L
    private var fps = 60L

    private val stars = List(100) { Star() }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
//    private val rotationMatrix = FloatArray(16)

    // For converting each game world coordinate
    // into a GL space coordinate (-1,-1 to 1,1)
    // for drawing on the screen
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        GLManager.buildProgram()
    }

    override fun onDrawFrame(unused: GL10) {

        val startFrameTime = System.currentTimeMillis()

        update()
        draw()

        val timeThisFrame = System.currentTimeMillis() - startFrameTime
        if (timeThisFrame > 0) {
            fps = MILLIS_IN_SECOND / timeThisFrame
        }

        if (isDebugging) {
            logAverageFPS()
        }
    }


    private fun update() {
        stars.forEach { it.update(fps) }
    }

    private fun draw() {
//        val scratch = FloatArray(16)

        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)


        // Create a rotation transformation for the triangle
//        val time = SystemClock.uptimeMillis() % 4000L
//        val angle = 0.090f * time.toInt()
//        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
//        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)


        stars.forEach { it.draw(vPMatrix) }
    }

    private fun logAverageFPS() {
        frameCounter++
        averageFPS += fps
        if (frameCounter > 100) {
            averageFPS /= frameCounter
            frameCounter = 0
            Log.d(TAG, "Average FPS: $averageFPS")
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}