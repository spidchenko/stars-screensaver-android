package d.spidchenko.sampledaydream.objects

import android.graphics.Color
import android.opengl.Matrix
import d.spidchenko.sampledaydream.util.Point
import d.spidchenko.sampledaydream.util.Vector
import kotlin.random.Random
import kotlin.random.nextInt

class ParticleShooter(
    direction: Vector,
    var aspectRatio: Float,
    private val angleVariance: Float,
    private val speedVariance: Float
) {

    private val rotationMatrix = FloatArray(16)
    private val directionVector = floatArrayOf(direction.x, direction.y, direction.z, 0F)
    private val resultVector = FloatArray(4)


    fun addParticles(particleSystem: ParticleSystem, currentTime: Float) {

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

        val randomY: Float
        val startPosition: Point
        if (aspectRatio > 1){
            // Landscape
             randomY = Random.nextDouble(-1.0, 1.0).toFloat()
             startPosition = Point(aspectRatio, randomY, 0F)
        } else {
            // Portrait or square
            randomY = Random.nextDouble((-aspectRatio).toDouble(), aspectRatio.toDouble()).toFloat()
            startPosition = Point(1F, randomY, 0F)
        }
        val color = getRandomColor()
        val randomSize = Random.nextInt(5..15).toFloat()
        particleSystem.addParticle(startPosition, color, thisDirection, currentTime, randomSize)
    }

    private fun getRandomColor() = Color.rgb(
        Random.nextInt(127..255),
        Random.nextInt(127..255),
        Random.nextInt(127..255)
    )
}