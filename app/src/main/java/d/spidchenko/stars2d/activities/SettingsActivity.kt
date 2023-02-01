package d.spidchenko.stars2d.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import d.spidchenko.stars2d.R
import d.spidchenko.stars2d.daydream.DreamSurfaceView
import d.spidchenko.stars2d.util.Billing
import d.spidchenko.stars2d.util.Logger


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
        setContentView(R.layout.settings_activity)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences)
        settingsFragment = SettingsFragment(gLView)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, settingsFragment)
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        findViewById<LinearLayout>(R.id.dream_preview).addView(gLView)
    }

    class SettingsFragment(private val gLView: DreamSurfaceView) : PreferenceFragmentCompat(),
        OnSharedPreferenceChangeListener {
        private val billing by lazy { Billing(requireContext()) }
        private var premiumOption: Preference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            Logger.log("onCreatePreferences")
            billing.init()
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
        }

        override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {
            gLView.reloadPreferences()
            Logger.log("onSharedChanged: isPrem: ${Billing.checkPremium(requireContext())}")
            if (Billing.checkPremium(requireContext())) {
                premiumOption?.isVisible = false
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