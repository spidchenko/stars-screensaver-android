package d.spidchenko.sampledaydream.data

import android.opengl.GLES20.*
import d.spidchenko.sampledaydream.GLManager.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(
    private val vertexData: FloatArray
) {
    private val floatBuffer = ByteBuffer
        .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexData)

    fun setVertexAttribPointer(
        dataOffset: Int,
        attributeHandle: Int,
        componentCount: Int,
        stride: Int
    ) {
        floatBuffer.position(dataOffset)
        glVertexAttribPointer(
            attributeHandle,
            componentCount,
            GL_FLOAT,
            false,
            stride,
            floatBuffer
        )
        glEnableVertexAttribArray(attributeHandle)
        floatBuffer.position(0)
    }

    fun updateBuffer(vertexData: FloatArray, start: Int, count: Int): FloatBuffer =
        floatBuffer.apply {
            position(start)
            put(vertexData, start, count)
            position(0)
        }

}