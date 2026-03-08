package d.spidchenko.stars2d.objects

import android.opengl.GLES20.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class FullFrameRect {
    private val vertexBuffer: FloatBuffer

    init {
        val vertices = floatArrayOf(
            -1f, -1f,  0f, 0f,
             1f, -1f,  1f, 0f,
            -1f,  1f,  0f, 1f,
             1f,  1f,  1f, 1f
        )
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)
    }

    fun bindData(positionLocation: Int, texCoordLocation: Int) {
        vertexBuffer.position(0)
        glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 16, vertexBuffer)
        glEnableVertexAttribArray(positionLocation)

        vertexBuffer.position(2)
        glVertexAttribPointer(texCoordLocation, 2, GL_FLOAT, false, 16, vertexBuffer)
        glEnableVertexAttribArray(texCoordLocation)
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }
}