package d.spidchenko.sampledaydream

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import d.spidchenko.sampledaydream.objects.ParticleShooter
import d.spidchenko.sampledaydream.objects.ParticleSystem
import d.spidchenko.sampledaydream.programs.ParticleShaderProgram
import d.spidchenko.sampledaydream.util.Point
import d.spidchenko.sampledaydream.util.TextureHelper
import d.spidchenko.sampledaydream.util.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val NANOS_IN_SECOND = 10e9F

class ParticlesRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
//    private lateinit var redParticleShooter: ParticleShooter
//    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var particleShooter: ParticleShooter

    private var globalStartTime: Long = 0L
    private var texture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0F, 0F, 0F, 0F)
//        GLManager.buildProgram()

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(1000)
        globalStartTime = SystemClock.elapsedRealtimeNanos()

        val particleDirection = Vector(-0.5F, 0F, 0F)
//        val particleDirection = Vector(0F, 0F, 0F)
        val angleVarianceInDegrees = 0F
        val speedVariance = 10F

        particleShooter = ParticleShooter(
//            Point(0F, 0F, 0F),
            particleDirection,
//            Color.rgb(5, 50, 255),
            angleVarianceInDegrees,
            speedVariance
        )

        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glEnable(GL_BLEND)
        glBlendFunc(GL_ONE, GL_ONE)
        glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 60F, ratio, 1F, 10F)
        Matrix.setIdentityM(viewMatrix, 0)
        // Push things down and into the distance
        Matrix.translateM(viewMatrix, 0, 0F, 0F, -1F)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        val currentTime = (SystemClock.elapsedRealtimeNanos() - globalStartTime) / NANOS_IN_SECOND

//        redParticleShooter.addParticles(particleSystem, currentTime, 5)
//        greenParticleShooter.addParticles(particleSystem, currentTime, 5)
        particleShooter.addParticles(particleSystem, currentTime, 5)

        particleProgram.useProgram()
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()
    }
}