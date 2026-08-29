package d.spidchenko.stars2d.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import d.spidchenko.stars2d.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val intent = Intent(Settings.ACTION_DREAM_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)

            Toast.makeText(
                this,
                getString(R.string.welcome_message),
                Toast.LENGTH_LONG
            ).show()
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.error_open_settings),
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            finish()
        }
    }
}
