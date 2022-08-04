package d.spidchenko.sampledaydream.objects

import android.opengl.Matrix
import d.spidchenko.sampledaydream.util.Point
import d.spidchenko.sampledaydream.util.Vector
import kotlin.random.Random

class ParticleShooter(
    private val position: Point,
    private val direction: Vector,
    private val color: Int,
    private val angleVariance: Float,
    private val speedVariance: Float
) {

    private val rotationMatrix = FloatArray(16)
    private val directionVector = floatArrayOf(direction.x, direction.y, direction.z, 0F)
    private val resultVector = FloatArray(4)


    fun addParticles(particleSystem: ParticleSystem, currentTime: Float, count: Int) {

        Matrix.setRotateEulerM(
            rotationMatrix, 0,
            (Random.nextFloat() - 0.5F) * angleVariance,
            (Random.nextFloat() - 0.5F) * angleVariance,
            (Random.nextFloat() - 0.5F) * angleVariance
        )

        Matrix.multiplyMV(
            resultVector, 0,
            rotationMatrix, 0,
            directionVector, 0
        )

        val speedAdjustment = 1F + Random.nextFloat() * speedVariance

        val thisDirection = Vector(
            resultVector[0] * speedAdjustment,
            resultVector[1] * speedAdjustment,
            resultVector[2] * speedAdjustment
        )

        particleSystem.addParticle(position, color, thisDirection, currentTime)
    }
}