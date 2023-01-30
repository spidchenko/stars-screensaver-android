package d.spidchenko.stars2d.daydream

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.opengl.GLSurfaceView


@SuppressLint("ViewConstructor")
class DreamSurfaceView(
    context: Context,
    preferences: SharedPreferences,
) : GLSurfaceView(context) {
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

    fun releaseResources() {
        renderer.releaseResources()
    }
}