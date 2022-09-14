package d.spidchenko.sampledaydream.objects

import android.graphics.Color
import android.opengl.GLES20.*
import d.spidchenko.sampledaydream.GLManager.BYTES_PER_FLOAT
import d.spidchenko.sampledaydream.GLManager.aColorLocation
import d.spidchenko.sampledaydream.GLManager.aDirectionVectorLocation
import d.spidchenko.sampledaydream.GLManager.aParticleStartTimeLocation
import d.spidchenko.sampledaydream.GLManager.aPositionLocation
import d.spidchenko.sampledaydream.data.VertexArray
import d.spidchenko.sampledaydream.util.Point
import d.spidchenko.sampledaydream.util.Vector

private const val POSITION_COMPONENT_COUNT = 3
private const val COLOR_COMPONENT_COUNT = 3
private const val VECTOR_COMPONENT_COUNT = 3
private const val PARTICLE_START_TIME_COMPONENT_COUNT = 3
private const val TOTAL_COMPONENT_COUNT =
    POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT + PARTICLE_START_TIME_COMPONENT_COUNT
private const val STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT

class ParticleSystem(
    private val maxParticleCount: Int
) {

    private val particles = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
    private val vertexArray = VertexArray(particles)
    private var currentParticleCount: Int = 0
    private var nextParticle: Int = 0

    fun addParticle(position: Point, color: Int, direction: Vector, particleStartTime: Float) {
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

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)
    }

    fun bindData() {
        var dataOffset = 0
        vertexArray.setVertexAttribPointer(
            dataOffset,
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += POSITION_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += COLOR_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            aDirectionVectorLocation,
            VECTOR_COMPONENT_COUNT,
            STRIDE
        )
        dataOffset += VECTOR_COMPONENT_COUNT

        vertexArray.setVertexAttribPointer(
            dataOffset,
            aParticleStartTimeLocation,
            PARTICLE_START_TIME_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() = glDrawArrays(GL_POINTS, 0, currentParticleCount)

}