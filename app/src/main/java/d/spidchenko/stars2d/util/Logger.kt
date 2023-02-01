package d.spidchenko.stars2d.util

private const val TAG = "Logger.LOG_TAG"

object Logger {
    fun log(message: String){
        if (LoggerConfig.ON){
            android.util.Log.d(TAG, message)
        }
    }
}