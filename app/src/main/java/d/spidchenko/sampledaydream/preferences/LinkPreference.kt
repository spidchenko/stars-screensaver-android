package d.spidchenko.sampledaydream.preferences

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import androidx.preference.Preference
import d.spidchenko.sampledaydream.R


class LinkPreference(context: Context, attributeSet: AttributeSet) :
    Preference(context, attributeSet) {

    override fun onClick() {
        super.onClick()
        val link: String = context.resources.getString(R.string.author_beer_link)
        context.startActivity(Intent("android.intent.action.VIEW", Uri.parse(link)))
    }
}