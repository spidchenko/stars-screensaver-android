package d.spidchenko.stars2d.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import d.spidchenko.stars2d.R


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent("android.settings.DREAM_SETTINGS"))
        Toast.makeText(
            applicationContext,
            getString(R.string.welcome_message),
            Toast.LENGTH_LONG
        ).show()
        finish()

    }
}