package d.spidchenko.sampledaydream

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import d.spidchenko.sampledaydream.objects.ParticleShooter
import d.spidchenko.sampledaydream.objects.ParticleSystem
import d.spidchenko.sampledaydream.util.Point
import d.spidchenko.sampledaydream.util.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL
import javax.microedition.khronos.opengles.GL10

private const val NANOS_IN_SECOND = 10e9F

class ParticlesRenderer : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter

//    private val texture = Textu

    private var globalStartTime: Long = 0L

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0F, 0F, 0F, 0F)
        GLManager.buildProgram()

        particleSystem = ParticleSystem(10000)
        globalStartTime = SystemClock.elapsedRealtimeNanos()

        val particleDirection = Vector(0F, 0.5F, 0F)
        val angleVarianceInDegrees = 5F
        val speedVariance = 1F

        redParticleShooter = ParticleShooter(
            Point(-1F, 0F, 0F),
            particleDirection,
            Color.rgb(255, 50, 5),
            angleVarianceInDegrees,
            speedVariance
        )

        greenParticleShooter = ParticleShooter(
            Point(0F, 0F, 0F),
            particleDirection,
            Color.rgb(25, 255, 25),
            angleVarianceInDegrees,
            speedVariance
        )

        blueParticleShooter = ParticleShooter(
            Point(1F, 0F, 0F),
            particleDirection,
            Color.rgb(5, 50, 255),
            angleVarianceInDegrees,
            speedVariance
        )

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 60F, ratio, 1F, 10F)
        Matrix.setIdentityM(viewMatrix, 0)
        // Push things down and into the distance
        Matrix.translateM(viewMatrix, 0, 0F, -1.5F, -5F)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        val currentTime = (SystemClock.elapsedRealtimeNanos() - globalStartTime) / NANOS_IN_SECOND

        redParticleShooter.addParticles(particleSystem, currentTime, 5)
        greenParticleShooter.addParticles(particleSystem, currentTime, 5)
        blueParticleShooter.addParticles(particleSystem, currentTime, 5)

        GLManager.useProgram()
        GLManager.setUniforms(viewProjectionMatrix, currentTime)
        particleSystem.bindData()
        particleSystem.draw()
    }
}