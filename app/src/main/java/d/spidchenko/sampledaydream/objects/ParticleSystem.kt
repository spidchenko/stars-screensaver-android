package d.spidchenko.sampledaydream.objects

import android.graphics.Color
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import d.spidchenko.sampledaydream.data.VertexArray
import d.spidchenko.sampledaydream.programs.BYTES_PER_FLOAT
import d.spidchenko.sampledaydream.programs.ParticleShaderProgram
import d.spidchenko.sampledaydream.util.Point
import d.spidchenko.sampledaydream.util.Vector

private const val POSITION_COMPONENT_COUNT = 3
private const val COLOR_COMPONENT_COUNT = 3
private const val VECTOR_COMPONENT_COUNT = 3
private const val PARTICLE_START_TIME_COMPONENT_COUNT = 1
private const val SIZE_COMPONENT_COUNT = 1
private const val TOTAL_COMPONENT_COUNT = SIZE_COMPONENT_COUNT +
        POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT
private const val STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT

class ParticleSystem(
    private val maxParticleCount: Int
) {

    private val particles = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
    private val vertexArray = VertexArray(particles)
    private var currentParticleCount: Int = 0
    private var nextParticle: Int = 0

    fun addParticle(position: Point, color: Int, direction: Vector, particleStartTime: Float, size: Float) {
        val particleOffset = nextParticle * TOTAL_COMPONENT_COUNT
        var currentOffset = particleOffset
        nextParticle++

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++
        }

        if (nextParticle == maxParticleCount) {
            nextParticle = 0
        }
        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255F
        particles[currentOffset++] = Color.green(color) / 255F
        particles[currentOffset++] = Color.blue(color) / 255F

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset++] = particleStartTime

        particles[currentOffset] = size

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)
    }

    fun bindData(particleProgram: ParticleShaderProgram) {
        var dataOffset = 0
        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.aPositionLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += POSITION_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.aColorLocation,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += COLOR_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.aDirectionVectorLocation,
            VECTOR_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += VECTOR_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.aParticleStartTimeLocation,
            PARTICLE_START_TIME_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += PARTICLE_START_TIME_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            particleProgram.aParticleSizeLocation,
            SIZE_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() = glDrawArrays(GL_POINTS, 0, currentParticleCount)
}