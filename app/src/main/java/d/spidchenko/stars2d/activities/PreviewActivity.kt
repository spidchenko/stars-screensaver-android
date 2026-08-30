package d.spidchenko.stars2d.activities

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.daydream.DreamSurfaceView

class PreviewActivity : AppCompatActivity() {

    private lateinit var gLView: DreamSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideSystemUI()

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences = preferences)
        setContentView(gLView)
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Allow rendering in the notch/cutout area (Android P+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onResume() {
        super.onResume()
        gLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        gLView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        gLView.releaseResources()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        finish()
        return true
    }
}
