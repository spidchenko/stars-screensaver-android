package d.spidchenko.sampledaydream.util

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object TextResourceReader {
    fun readTextFileFromResource(context: Context, resourceId: Int): String {

        val inputString : String

        try {
            val inputStream = context.resources.openRawResource(resourceId)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            inputString = bufferedReader.use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: $resourceId. $e")
        } catch (nfe: Resources.NotFoundException) {
            throw RuntimeException("Resource not found: $resourceId. $nfe")
        }

        return inputString
    }
}