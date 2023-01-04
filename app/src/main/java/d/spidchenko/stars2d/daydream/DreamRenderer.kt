package d.spidchenko.stars2d.daydream

import android.content.Context
import android.content.SharedPreferences
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
import android.os.SystemClock
import android.util.Log
import d.spidchenko.stars2d.R
import d.spidchenko.stars2d.objects.ParticleShooter
import d.spidchenko.stars2d.objects.ParticleSystem
import d.spidchenko.stars2d.programs.ParticleShaderProgram
import d.spidchenko.stars2d.util.LoggerConfig
import d.spidchenko.stars2d.util.TextureHelper
import d.spidchenko.stars2d.util.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val NANOS_IN_SECOND = 10e9F
private const val MAX_PARTICLE_COUNT = 10000
private const val TAG = "StarsRenderer.LOG_TAG"

class DreamRenderer(
    private val context: Context,
    private val preferences: SharedPreferences
) :
    GLSurfaceView.Renderer {

    private val viewProjectionMatrix = FloatArray(16)

    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var particleShooter: ParticleShooter

    private var globalStartTime: Long = 0L
    private var textureId: Int = 0

    private var frameCounter = 0L
    private var averageFPS = 0.0
    private var fps = 60.0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0F, 0F, 0F, 0F)

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(MAX_PARTICLE_COUNT)
        globalStartTime = SystemClock.elapsedRealtimeNanos()

        val particleDirection = Vector(-0.5F, 0F, 0F)
        val speedVariance = 10F

        particleShooter = ParticleShooter(
            preferences,
            particleDirection,
            1F,
            speedVariance
        )

        textureId = TextureHelper.loadTexture(context, R.drawable.particle_texture)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val aspectRatio: Float = width.toFloat() / height.toFloat()
        particleShooter.aspectRatio = aspectRatio
        if (LoggerConfig.ON) Log.d(TAG, "onSurfaceChanged: ${width}x$height Ratio= $aspectRatio")
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        // Set the OpenGL viewport to fill the entire surface
        glViewport(0, 0, width, height)
        if (aspectRatio > 1.0) {
            // Landscape
            orthoM(viewProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            // Portrait or square
            orthoM(viewProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        val currentTime = (SystemClock.elapsedRealtimeNanos() - globalStartTime) / NANOS_IN_SECOND

        particleShooter.addParticles(particleSystem, currentTime)

        particleProgram.useProgram()
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, textureId)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()

        if (LoggerConfig.ON) {
            logAverageFPS()
        }
    }

    fun reloadPreferences() {
        particleShooter.reloadPreferences()
    }

    fun releaseResources() {
        glDeleteTextures(1, intArrayOf(textureId), 0)
        if (LoggerConfig.ON) Log.d(TAG, "releaseResources: Deleted textures")
    }

    private fun logAverageFPS() {
        frameCounter++
        averageFPS += fps
        if (frameCounter > 100) {
            averageFPS /= frameCounter
            frameCounter = 0
            Log.d(TAG, "Average FPS: $averageFPS")
        }
    }
}