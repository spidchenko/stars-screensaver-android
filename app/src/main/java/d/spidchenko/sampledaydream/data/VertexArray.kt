package d.spidchenko.sampledaydream.data

import android.opengl.GLES20
import d.spidchenko.sampledaydream.GLManager.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder

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
        GLES20.glVertexAttribPointer(
            attributeHandle,
            componentCount,
            GLES20.GL_FLOAT,
            false,
            stride,
            floatBuffer
        )
        GLES20.glEnableVertexAttribArray(attributeHandle)
        floatBuffer.position(0)
    }

    fun updateBuffer(vertexData: FloatArray, start: Int, count: Int) =
        floatBuffer.apply {
            position(start)
            put(vertexData, start, count)
            position(0)
        }

}