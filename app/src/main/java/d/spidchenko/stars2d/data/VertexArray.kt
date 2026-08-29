package d.spidchenko.stars2d.data

import android.opengl.GLES20.*
import d.spidchenko.stars2d.programs.BYTES_PER_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VertexArray(
    vertexData: FloatArray
) {
    private val byteBuffer: ByteBuffer = ByteBuffer
        .allocateDirect(vertexData.size * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())

    private val floatBuffer: FloatBuffer = byteBuffer.asFloatBuffer()

    private var vboId: Int = 0

    init {
        // Upload initial data to the buffers
        floatBuffer.put(vertexData)
        floatBuffer.position(0)
        
        // Ensure the ByteBuffer is also reset
        byteBuffer.position(0)
        byteBuffer.limit(byteBuffer.capacity())

        val buffers = IntArray(1)
        glGenBuffers(1, buffers, 0)
        vboId = buffers[0]

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(
            GL_ARRAY_BUFFER,
            byteBuffer.capacity(),
            byteBuffer,
            GL_DYNAMIC_DRAW
        )
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    /**
     * Binds a shader attribute to this vertex array.
     * @param offsetInFloats The starting index in the buffer for this attribute.
     * @param attributeLocation The shader attribute location.
     * @param componentCount Number of components (e.g. 3 for XYZ).
     * @param strideInBytes Byte offset between consecutive attributes.
     */
    fun setVertexAttribPointer(
        offsetInFloats: Int,
        attributeLocation: Int,
        componentCount: Int,
        strideInBytes: Int
    ) {
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glVertexAttribPointer(
            attributeLocation,
            componentCount,
            GL_FLOAT,
            false,
            strideInBytes,
            offsetInFloats * BYTES_PER_FLOAT
        )
        glEnableVertexAttribArray(attributeLocation)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    /**
     * Updates a portion of the GPU buffer with new data.
     */
    fun updateBuffer(vertexData: FloatArray, startOffsetInFloats: Int, count: Int) {
        floatBuffer.position(startOffsetInFloats)
        floatBuffer.put(vertexData, startOffsetInFloats, count)
        floatBuffer.position(0)

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        
        // Calculate byte positions
        val startByte = startOffsetInFloats * BYTES_PER_FLOAT
        val countBytes = count * BYTES_PER_FLOAT
        
        // Set ByteBuffer state exactly for the update range
        byteBuffer.position(startByte)
        byteBuffer.limit(startByte + countBytes)
        
        glBufferSubData(
            GL_ARRAY_BUFFER,
            startByte,
            countBytes,
            byteBuffer
        )
        
        // Reset ByteBuffer state for safety
        byteBuffer.position(0)
        byteBuffer.limit(byteBuffer.capacity())
        
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun release() {
        if (vboId != 0) {
            glDeleteBuffers(1, intArrayOf(vboId), 0)
            vboId = 0
        }
    }
}
