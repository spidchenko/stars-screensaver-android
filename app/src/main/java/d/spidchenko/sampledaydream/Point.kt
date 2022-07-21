package d.spidchenko.sampledaydream

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Point {

    var pointCoords = floatArrayOf(0.0f, 0.0f, 0.0f)

    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexCount = 1
    private val vertexStride: Int = GLManager.STRIDE // 4 bytes per vertex

    // Use to access and set the view transformation
    private var vPMatrixHandle: Int = 0

    private lateinit var vertexBuffer: FloatBuffer

    fun draw(mvpMatrix: FloatArray) {

//        pointCoords[0] = x
//        pointCoords[1] = y

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
}