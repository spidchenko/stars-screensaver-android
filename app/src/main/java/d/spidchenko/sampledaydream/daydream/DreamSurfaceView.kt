package d.spidchenko.sampledaydream.daydream

import android.content.Context
import android.opengl.GLSurfaceView

class DreamSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: DreamRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        renderer = DreamRenderer(context)
        setRenderer(renderer)
    }
}