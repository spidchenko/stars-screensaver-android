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
private const val MOVEMENT_DIRECTION_KEY = "movement_direction"
private const val PARTICLE_MIN_SIZE = 10
private const val PARTICLE_MAX_SIZE = 30
private const val COLOR_COMPONENT_MAX_VALUE = 255
private const val COLOR_MIN = COLOR_COMPONENT_MAX_VALUE / 2

// As in default preferences:
private const val DEFAULT_PARTICLE_COUNT_MODIFIER = 53  // 6-100
private const val DEFAULT_PARTICLE_SIZE_MODIFIER = 80   // 30-130
private val DEFAULT_MOVEMENT_DIRECTION = MovementDirection.RIGHT_TO_LEFT

class ParticleShooter(
    private val preferences: SharedPreferences,
    var aspectRatio: Float,
    private val speedVariance: Float
) {

    private val isLandscape get() = (aspectRatio > 1f)
    private var particleSizeModifier: Float = preferences.getInt(PARTICLE_SIZE_MODIFIER_KEY, DEFAULT_PARTICLE_SIZE_MODIFIER) / 100F
    private var particleCountModifier: Float = preferences.getInt(PARTICLE_COUNT_MODIFIER_KEY, DEFAULT_PARTICLE_COUNT_MODIFIER) / 100F
    private var movementDirection: MovementDirection = DEFAULT_MOVEMENT_DIRECTION

    // Reusable vector object used to store the direction
    private var thisDirection = Vector(0f,0f,0f)
    // Reusable point object used to store start position
    private var startPosition = Point(0f,0f,0f)

    init { loadPreferences() }

    fun addParticles(particleSystem: ParticleSystem, currentTime: Float) {
        if (Random.nextFloat() >= particleCountModifier) return

        val speedAdjustment = 1f + Random.nextFloat() * speedVariance
        val boundsX = if (isLandscape) aspectRatio else 1f
        val boundsY = if (isLandscape) 1f else 1f / aspectRatio

        val hSpeed = 0.4f * speedAdjustment
        val vSpeed = 0.4f * speedAdjustment

        val startX: Float
        val startY: Float
        val dirX: Float
        val dirY: Float

        when (movementDirection) {
            MovementDirection.LEFT_TO_RIGHT -> {
                startX = -boundsX
                startY = Random.nextFloat(-boundsY, boundsY)
                dirX = hSpeed
                dirY = 0f
            }
            MovementDirection.RIGHT_TO_LEFT -> {
                startX = boundsX
                startY = Random.nextFloat(-boundsY, boundsY)
                dirX = -hSpeed
                dirY = 0f
            }
            MovementDirection.TOP_TO_BOTTOM -> {
                startX = Random.nextFloat(-boundsX, boundsX)
                startY = boundsY
                dirX = 0f
                dirY = -vSpeed
            }
            MovementDirection.BOTTOM_TO_UP -> {
                startX = Random.nextFloat(-boundsX, boundsX)
                startY = -boundsY
                dirX = 0f
                dirY = vSpeed
            }
        }

        thisDirection = Vector(dirX, dirY, 0f)
        startPosition = Point(startX, startY, 0f)

        val size = Random.nextInt(PARTICLE_MIN_SIZE, PARTICLE_MAX_SIZE + 1).toFloat() * particleSizeModifier
        particleSystem.addParticle(startPosition, getRandomColor(), thisDirection, currentTime, size)
    }

    fun reloadPreferences() = loadPreferences()

    private fun loadPreferences() {
        particleSizeModifier = preferences.getInt(PARTICLE_SIZE_MODIFIER_KEY, DEFAULT_PARTICLE_SIZE_MODIFIER) / 100F
        particleCountModifier = preferences.getInt(PARTICLE_COUNT_MODIFIER_KEY, DEFAULT_PARTICLE_COUNT_MODIFIER) / 100F
        val directionString = preferences.getString(MOVEMENT_DIRECTION_KEY, DEFAULT_MOVEMENT_DIRECTION.name)
        movementDirection = try {
            MovementDirection.valueOf(directionString!!)
        } catch (e: Exception) {
            DEFAULT_MOVEMENT_DIRECTION
        }
    }

    private fun getRandomColor() = Color.rgb(
        Random.nextInt(COLOR_MIN, COLOR_COMPONENT_MAX_VALUE),
        Random.nextInt(COLOR_MIN, COLOR_COMPONENT_MAX_VALUE),
        Random.nextInt(COLOR_MIN, COLOR_COMPONENT_MAX_VALUE)
    )

    private fun Random.nextFloat(from: Float, until: Float): Float {
        return from + nextFloat() * (until - from)
    }
}