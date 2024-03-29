package d.spidchenko.stars2d.activities

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.daydream.DreamSurfaceView

class PreviewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val gLView = DreamSurfaceView(this, preferences)
        setContentView(gLView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        finish()
        return true
    }
}