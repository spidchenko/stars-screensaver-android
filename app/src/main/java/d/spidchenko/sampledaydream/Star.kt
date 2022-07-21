package d.spidchenko.sampledaydream

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.random.Random

private const val screenX = 1.0
private const val screenY = 1.0
private const val TAG = "Star.LOG_TAG"

class Star {

    var pointCoords = floatArrayOf(0.0f, 0.0f, 0.0f)

    val color = floatArrayOf(1f, 1f, 1f, 1.0f)
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount = 1
    private val vertexStride: Int = GLManager.STRIDE // 4 bytes per vertex

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private lateinit var vertexBuffer: FloatBuffer

    //    private val position: Point
    private var x = Random.nextDouble(-screenX, +screenX)
    private var y = Random.nextDouble(-screenY, +screenY)

    //    private val diameter = Random.nextDouble(STAR_MIN_SIZE, STAR_MAX_SIZE).toFloat()
    val minVelocity: Double = 0.8
    val maxVelocity: Double = 0.9
    private val xVelocity = -Random.nextDouble(minVelocity, maxVelocity)
    private val yVelocity: Double = 0.0
//    private val rect = RectF(x, y, x + diameter, y + diameter)


    fun update(fps: Long) =
        if (x < -screenX) {
//            Log.d(TAG, "update: respawn")
            respawn()
        } else {
            x += xVelocity / fps
            y += yVelocity / fps
//            rect.offset(xVelocity / fps, yVelocity / fps)
        }

    fun draw(mvpMatrix: FloatArray) {

        pointCoords[0] = x.toFloat()
        pointCoords[1] = y.toFloat()

        // (number of coordinate values * 4 bytes per float)
        vertexBuffer = ByteBuffer.allocateDirect(pointCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(pointCoords)
                position(0)
            }
        }

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(GLManager.program)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(GLManager.program, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                GLManager.ELEMENTS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle =
                GLES20.glGetUniformLocation(GLManager.program, "vColor").also { colorHandle ->
                    // Set color for drawing the triangle
                    GLES20.glUniform4fv(colorHandle, 1, color, 0)
                }


            // get handle to shape's transformation matrix
            vPMatrixHandle = GLES20.glGetUniformLocation(GLManager.program, "uMVPMatrix")

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)

            // Draw the Point
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount)

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(positionHandle)
        }
    }

    private fun respawn() {
        x = screenX
        y = Random.nextDouble(-screenY, +screenY)
//        rect.offsetTo(screenX.toFloat(), Random.nextInt(screenY).toFloat())
    }


//    companion object {
//        const val STAR_MIN_SIZE = 0.9
//        const val STAR_MAX_SIZE = 4.0
//        const val MIN_VELOCITY_DIVIDER = 8.0
//        const val MAX_VELOCITY_DIVIDER = 3.0
//    }
}