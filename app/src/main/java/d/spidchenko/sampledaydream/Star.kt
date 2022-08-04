package d.spidchenko.sampledaydream

import android.opengl.GLES20
import android.opengl.Matrix.*
import kotlin.random.Random

private const val TAG = "Star.LOG_TAG"
private const val MAX_COLOR_COMPONENT_VALUE = 1.0
private const val HALF_COLOR_COMPONENT_VALUE = 0.5
private const val STAR_MIN_SIZE = 1.0
private const val STAR_MAX_SIZE = 4.0
private const val STAR_MIN_VELOCITY: Double = 0.2
private const val STAR_MAX_VELOCITY: Double = 0.8
private const val VERTEX_COUNT = 1

class Star {
    private val modelMatrix = FloatArray(16)
    private val viewportModelMatrix = FloatArray(16)
    private val diameter = Random.nextDouble(STAR_MIN_SIZE, STAR_MAX_SIZE).toFloat()
    private val color = getRandomStarColor()

    private var x = Random.nextDouble(-SCREEN_X, SCREEN_X)
    private var y = Random.nextDouble(-SCREEN_Y, SCREEN_Y)
    private val xVelocity = -Random.nextDouble(STAR_MIN_VELOCITY, STAR_MAX_VELOCITY)
//    Not used now
//    private val yVelocity: Double = 0.0


    fun update(fps: Double) = if (x < -SCREEN_X) respawn() else x += xVelocity / fps


    fun draw(mvpMatrix: FloatArray) {

        // Move vertex to (x,y) position on the screen
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, x.toFloat(), y.toFloat(), 0F)
        multiplyMM(viewportModelMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(GLManager.program)

        // Set color for drawing the point
//        GLES20.glUniform4fv(GLManager.colorHandle, 1, color, 0)

        // Set size for this point
//        GLES20.glUniform1f(GLManager.pointDiameter, diameter)

        // Pass the projection and view transformation to the shader
//        GLES20.glUniformMatrix4fv(GLManager.uMatrixLocation, 1, false, viewportModelMatrix, 0)

        // Draw the Point
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, VERTEX_COUNT)

        // Disable vertex array
//        GLES20.glDisableVertexAttribArray(GLManager.positionHandle)
    }

    private fun respawn() {
        x = SCREEN_X
        y = Random.nextDouble(-SCREEN_Y, SCREEN_Y)
    }

    private fun getRandomStarColor() = floatArrayOf(
        Random.nextDouble(HALF_COLOR_COMPONENT_VALUE, MAX_COLOR_COMPONENT_VALUE).toFloat(),
        Random.nextDouble(HALF_COLOR_COMPONENT_VALUE, MAX_COLOR_COMPONENT_VALUE).toFloat(),
        Random.nextDouble(HALF_COLOR_COMPONENT_VALUE, MAX_COLOR_COMPONENT_VALUE).toFloat(),
        1.0f
    )
}