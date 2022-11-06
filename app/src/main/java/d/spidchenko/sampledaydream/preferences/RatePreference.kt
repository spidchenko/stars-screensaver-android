package d.spidchenko.sampledaydream.preferences

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import androidx.preference.Preference


class RatePreference(context: Context, attributeSet: AttributeSet) :
    Preference(context, attributeSet) {
    override fun onClick() {
        super.onClick()
        try {
            context.startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("market://details?id=d.spidchenko.sampledaydream")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("http://play.google.com/store/apps/details?id=d.spidchenko.sampledaydream")
                )
            )
        }
    }
}