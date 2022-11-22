package d.spidchenko.sampledaydream.daydream

import android.content.Context
import android.content.SharedPreferences
import android.opengl.GLSurfaceView
import android.util.Log

class DreamSurfaceView(context: Context, preferences: SharedPreferences) : GLSurfaceView(context) {
    private val renderer: DreamRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        renderer = DreamRenderer(context, preferences)
        setRenderer(renderer)
    }

    fun reloadPreferences() {
        renderer.reloadPreferences()
    }
}