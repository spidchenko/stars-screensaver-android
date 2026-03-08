package d.spidchenko.stars2d.objects

import android.content.SharedPreferences
import android.graphics.Color
import d.spidchenko.stars2d.util.Point
import d.spidchenko.stars2d.util.Vector
import kotlin.random.Random
import kotlin.random.nextInt

enum class MovementDirection {
    LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_UP
}

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
    private val movementDirection: MovementDirection,
    var aspectRatio: Float,
    private val speedVariance: Float
) {

    private val isLandscape = aspectRatio > 1f
    private var particleSizeModifier: Float = preferences.getInt(PARTICLE_SIZE_MODIFIER_KEY, DEFAULT_PARTICLE_SIZE_MODIFIER) / 100F
    private var particleCountModifier: Float = preferences.getInt(PARTICLE_COUNT_MODIFIER_KEY, DEFAULT_PARTICLE_COUNT_MODIFIER) / 100F

    // Reusable vector object used to store the direction
    private var thisDirection = Vector(0f,0f,0f)
    // Reusable point object used to store start position
    private var startPosition = Point(0f,0f,0f)


    fun addParticles(particleSystem: ParticleSystem, currentTime: Float) {

        val speedAdjustment = 1F + Random.nextFloat() * speedVariance

        // Calculate a scaling factor to normalize speed across different aspect ratios
        val horizontalSpeed = if (isLandscape) 0.5f * aspectRatio else 0.5f
        val verticalSpeed = if (isLandscape) 0.5f else 0.5f * aspectRatio / 2f // DONE

        thisDirection = when (movementDirection) {
            MovementDirection.LEFT_TO_RIGHT -> Vector(horizontalSpeed * speedAdjustment, 0f, 0f)
            MovementDirection.RIGHT_TO_LEFT -> Vector(-horizontalSpeed * speedAdjustment, 0f, 0f)
            MovementDirection.TOP_TO_BOTTOM -> Vector(0f, -verticalSpeed * speedAdjustment, 0f)
            MovementDirection.BOTTOM_TO_UP -> Vector(0f, verticalSpeed * speedAdjustment, 0f)
        }

        when (movementDirection) {
            MovementDirection.LEFT_TO_RIGHT -> {
                val boundsY = if (isLandscape) 1f else aspectRatio
                val randomY = Random.nextDouble(-boundsY.toDouble(), boundsY.toDouble()).toFloat()
                val startX = if (isLandscape) -aspectRatio else -1f
                startPosition = Point(startX, randomY, 0f)
            }
            MovementDirection.RIGHT_TO_LEFT -> {
                val boundsY = if (isLandscape) 1f else aspectRatio
                val randomY = Random.nextDouble(-boundsY.toDouble(), boundsY.toDouble()).toFloat()
                val startX = if (isLandscape) aspectRatio else 1f
                startPosition = Point(startX, randomY, 0f)
            }
            MovementDirection.TOP_TO_BOTTOM -> {
                val boundsX = if (isLandscape) aspectRatio else 1f
                val randomX = Random.nextDouble(-boundsX.toDouble(), boundsX.toDouble()).toFloat()
                val startY = if (isLandscape) 1f else aspectRatio
                startPosition = Point(randomX, startY, 0f)
            }
            MovementDirection.BOTTOM_TO_UP -> {
                val boundsX = if (isLandscape) aspectRatio else 1f
                val randomX = Random.nextDouble(-boundsX.toDouble(), boundsX.toDouble()).toFloat()
                val startY = if (isLandscape) -1f else -aspectRatio
                startPosition = Point(randomX, startY, 0f)
            }
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