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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SettingsActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private lateinit var preferences: SharedPreferences
    private lateinit var gLView: DreamSurfaceView

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.settings_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
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
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        gLView = DreamSurfaceView(this, preferences)
        findViewById<LinearLayout>(R.id.dream_preview).addView(gLView)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val billing by lazy { Billing(requireContext()) }
        override fun onResume() {
            super.onResume()
            MainScope().launch { billing.querySuccessfulPurchases() }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            Logger.Log("onCreatePreferences")
            billing.init()
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val premiumOption = findPreference<Preference>("premium")
            if (Billing.checkPremium(requireContext())) {
                premiumOption?.isVisible = false
            } else {
                premiumOption?.setOnPreferenceClickListener {
                    Logger.Log("onCreatePreferences: CLICKED PREM LINK!")
                    billing.launchBuyPremiumBillingFlow(requireActivity())
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        gLView.reloadPreferences()
    }
}