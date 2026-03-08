package d.spidchenko.stars2d.daydream

import android.content.Context
import android.content.SharedPreferences
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
import android.os.SystemClock
import d.spidchenko.stars2d.R
import d.spidchenko.stars2d.objects.FullFrameRect
import d.spidchenko.stars2d.objects.ParticleShooter
import d.spidchenko.stars2d.objects.ParticleSystem
import d.spidchenko.stars2d.programs.ParticleShaderProgram
import d.spidchenko.stars2d.programs.TextureShaderProgram
import d.spidchenko.stars2d.util.Logger
import d.spidchenko.stars2d.util.TextureHelper
import d.spidchenko.stars2d.util.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val NANOS_IN_SECOND = 10e9F
private const val MAX_PARTICLE_COUNT = 10000

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

    // Trail/FBO variables
    private var fboId = 0
    private val frameTextures = IntArray(2)
    private var currentTextureIndex = 0
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var fullFrameRect: FullFrameRect

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0F, 0F, 0F, 1F)

        particleProgram = ParticleShaderProgram(context)
        textureProgram = TextureShaderProgram(context)
        fullFrameRect = FullFrameRect()

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
        
        setupFbos(width, height)
        
        glViewport(0, 0, width, height)
        if (aspectRatio > 1.0) {
            orthoM(viewProjectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            orthoM(viewProjectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
    }

    private fun setupFbos(width: Int, height: Int) {
        if (frameTextures[0] != 0) {
            glDeleteTextures(2, frameTextures, 0)
            glDeleteFramebuffers(1, intArrayOf(fboId), 0)
        }

        val fboIds = IntArray(1)
        glGenFramebuffers(1, fboIds, 0)
        fboId = fboIds[0]

        glGenTextures(2, frameTextures, 0)
        for (i in 0..1) {
            glBindTexture(GL_TEXTURE_2D, frameTextures[i])
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        }
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        val currentTime = (SystemClock.elapsedRealtimeNanos() - globalStartTime) / NANOS_IN_SECOND
        particleShooter.addParticles(particleSystem, currentTime)

        val nextTextureIndex = 1 - currentTextureIndex

        // 1. Render to FBO (Current Texture)
        glBindFramebuffer(GL_FRAMEBUFFER, fboId)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, frameTextures[currentTextureIndex], 0)

        // Draw the PREVIOUS frame with a slight fade
        glDisable(GL_BLEND)
        textureProgram.useProgram()
        // 0.96f creates a smooth trail. Lower values make shorter trails.
        textureProgram.setUniforms(frameTextures[nextTextureIndex], 0.7f)
        fullFrameRect.bindData(textureProgram.aPositionLocation, textureProgram.aTextureCoordinatesLocation)
        fullFrameRect.draw()

        // Draw NEW particles on top
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        particleProgram.useProgram()
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, textureId)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()

        // 2. Render FBO result to Screen
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
        glClear(GL_COLOR_BUFFER_BIT)
        glDisable(GL_BLEND)
        textureProgram.useProgram()
        textureProgram.setUniforms(frameTextures[currentTextureIndex], 1.0f)
        fullFrameRect.bindData(textureProgram.aPositionLocation, textureProgram.aTextureCoordinatesLocation)
        fullFrameRect.draw()

        currentTextureIndex = nextTextureIndex
    }

    fun reloadPreferences() {
        particleShooter.reloadPreferences()
    }

    fun releaseResources() {
        glDeleteTextures(1, intArrayOf(textureId), 0)
        glDeleteTextures(2, frameTextures, 0)
        glDeleteFramebuffers(1, intArrayOf(fboId), 0)
        Logger.log("releaseResources: Deleted textures and FBO")
    }
}