package d.spidchenko.sampledaydream.activities

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import d.spidchenko.sampledaydream.daydream.DreamSurfaceView

class PreviewActivity : Activity() {
    private lateinit var gLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        gLView = DreamSurfaceView(this)
//        setContentView(gLView)
    }
}