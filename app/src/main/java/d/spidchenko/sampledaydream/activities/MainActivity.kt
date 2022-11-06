package d.spidchenko.sampledaydream.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import d.spidchenko.sampledaydream.R


class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent("android.settings.DREAM_SETTINGS"))
        Toast.makeText(
            applicationContext,
            getString(R.string.greeting),
            Toast.LENGTH_LONG
        ).show()
        finish()

    }
}