package d.spidchenko.stars2d.daydream

import android.content.Context
import android.content.SharedPreferences
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.util.Logger

class DreamSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
) : GLSurfaceView(context, attrs) {

    private val renderer: DreamRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // Don't destroy the EGL context when paused.
        preserveEGLContextOnPause = true

        renderer = DreamRenderer(context, preferences)
        setRenderer(renderer)

        // Ensure we render continuously for the animation
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    /**
     * Updates renderer parameters from SharedPreferences on the GL thread.
     */
    fun reloadPreferences() {
        queueEvent {
            renderer.reloadPreferences()
        }
    }

    /**
     * Safely releases OpenGL resources (textures, buffers) on the GL thread.
     */
    fun releaseResources() {
        queueEvent {
            renderer.releaseResources()
            Logger.log("DreamSurfaceView: GL resources released on GL thread")
        }
    }

    override fun onPause() {
        super.onPause()
        Logger.log("DreamSurfaceView: Paused")
    }

    override fun onResume() {
        super.onResume()
        Logger.log("DreamSurfaceView: Resumed")
    }
}
