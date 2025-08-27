package d.spidchenko.stars2d.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.R
import d.spidchenko.stars2d.daydream.DreamSurfaceView
import d.spidchenko.stars2d.util.Billing
import d.spidchenko.stars2d.util.Logger
import d.spidchenko.stars2d.util.SoundEngine
import d.spidchenko.stars2d.util.VibrateUtil


class SettingsActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var gLView: DreamSurfaceView
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.preview -> {
                startActivity(Intent(this, PreviewActivity::class.java))
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            settingsFragment = SettingsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings, settingsFragment)
                .commit()
        } else {
            settingsFragment = supportFragmentManager.findFragmentById(R.id.settings) as SettingsFragment
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<LinearLayout>(R.id.dream_preview).addView(gLView)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        OnSharedPreferenceChangeListener {
        private val billing by lazy { Billing(requireContext()) }
        private lateinit var soundEngine: SoundEngine
        private var premiumOption: Preference? = null
        private var gLView: DreamSurfaceView? = null

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            if (activity is SettingsActivity) {
                this.gLView = (activity as SettingsActivity).gLView
            } else {
                throw RuntimeException("Fragment not attached to SettingsActivity")
            }

            // Now you can use gLViewFromActivity
            if (gLView == null) {
                Logger.log("SettingsFragment: gLView is null in onViewCreated!")
            }
        }

        override fun onDetach() {
            super.onDetach()
            gLView = null // Clear the reference to avoid memory leaks
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            Logger.log("onCreatePreferences")
            billing.init()
            soundEngine = SoundEngine(requireContext())
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            premiumOption = findPreference("premium")
            premiumOption?.setOnPreferenceClickListener {
                Logger.log("onCreatePreferences: CLICKED PREM LINK!")
                billing.launchBuyPremiumBillingFlow(requireActivity())
                true
            }
            if (Billing.checkPremium(requireContext())) {
                premiumOption?.isVisible = false
            }
            if (gLView == null) {
                Logger.log("SettingsFragment: gLView is null in onCreatePreferences!")
            }
        }

        override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {
            gLView?.reloadPreferences()
            Logger.log("onSharedChanged: isPrem: ${Billing.checkPremium(requireContext())}")
            if (Billing.checkPremium(requireContext())) {
                premiumOption?.isVisible = false
            }

            // TODO Move "magic strings" to PreferenceKeys object

            if ((key == "play_sound") && (sharedPref?.getBoolean("play_sound", false) == true)) {
                Logger.log("onSharedChanged: pop!")
                soundEngine.playPop()
            }

            if ((key == "vibrate") && (sharedPref?.getBoolean("vibrate", false) == true)) {
                Logger.log("onSharedChanged: vibrate!")
                VibrateUtil.vibrate(requireContext())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(settingsFragment)
    }

    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(settingsFragment)
    }
}