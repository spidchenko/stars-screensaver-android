package d.spidchenko.stars2d.objects

import android.content.SharedPreferences
import android.graphics.Color
import d.spidchenko.stars2d.util.Point
import d.spidchenko.stars2d.util.Vector
import kotlin.random.Random
import kotlin.random.nextInt

private const val PARTICLE_SIZE_MODIFIER_KEY = "size_of_particles"
private const val PARTICLE_COUNT_MODIFIER_KEY = "number_of_particles"
private const val PARTICLE_MIN_SIZE = 10
private const val PARTICLE_MAX_SIZE = 30
private const val COLOR_COMPONENT_MAX_VALUE = 255
private const val MIN_Y = -1.0
private const val MAX_Y = 1.0
private const val MAX_X = 1.0
// As in default preferences:
private const val DEFAULT_PARTICLE_COUNT_MODIFIER = 53  // 6-100
private const val DEFAULT_PARTICLE_SIZE_MODIFIER = 80   // 30-130

class ParticleShooter(
    private val preferences: SharedPreferences,
    direction: Vector,
    var aspectRatio: Float,
    private val speedVariance: Float
) {

    private var particleSizeModifier: Float = preferences.getInt(PARTICLE_SIZE_MODIFIER_KEY, DEFAULT_PARTICLE_SIZE_MODIFIER) / 100F
    private var particleCountModifier: Float = preferences.getInt(PARTICLE_COUNT_MODIFIER_KEY, DEFAULT_PARTICLE_COUNT_MODIFIER) / 100F
    private val directionVector = floatArrayOf(direction.x, direction.y, direction.z, 0F)


    fun addParticles(particleSystem: ParticleSystem, currentTime: Float) {

        val speedAdjustment = 1F + Random.nextFloat() * speedVariance

        val thisDirection = Vector(
            directionVector[0] * speedAdjustment,
            directionVector[1] * speedAdjustment,
            directionVector[2] * speedAdjustment
        )

        val randomY: Float
        val startPosition: Point
        if (aspectRatio > 1) {
            // Landscape
            randomY = Random.nextDouble(MIN_Y, MAX_Y).toFloat()
            startPosition = Point(aspectRatio, randomY, 0F)
        } else {
            // Portrait or square
            randomY = Random.nextDouble((-aspectRatio).toDouble(), aspectRatio.toDouble()).toFloat()
            startPosition = Point(MAX_X.toFloat(), randomY, 0F)
        }
        val color = getRandomColor()
        val randomSize =
            Random.nextInt(PARTICLE_MIN_SIZE..PARTICLE_MAX_SIZE).toFloat() * particleSizeModifier

        if (Random.nextFloat() < particleCountModifier) {
            particleSystem.addParticle(startPosition, color, thisDirection, currentTime, randomSize)
        }
    }

    fun reloadPreferences() {
        particleSizeModifier = preferences.getInt(PARTICLE_SIZE_MODIFIER_KEY, DEFAULT_PARTICLE_SIZE_MODIFIER) / 100F
        particleCountModifier = preferences.getInt(PARTICLE_COUNT_MODIFIER_KEY, DEFAULT_PARTICLE_COUNT_MODIFIER) / 100F
    }

    private fun getRandomColor() = Color.rgb(
        Random.nextInt(COLOR_COMPONENT_MAX_VALUE / 2..COLOR_COMPONENT_MAX_VALUE),
        Random.nextInt(COLOR_COMPONENT_MAX_VALUE / 2..COLOR_COMPONENT_MAX_VALUE),
        Random.nextInt(COLOR_COMPONENT_MAX_VALUE / 2..COLOR_COMPONENT_MAX_VALUE)
    )
}