package d.spidchenko.sampledaydream.util

import android.content.Context
import android.opengl.GLES20
import android.util.Log

private const val TAG = "TextureHelper.LOG_TAG"

object TextureHelper {
    fun loadTexture(context: Context, resourceId: Int): Int{
        val textureObjectsIds = IntArray(1)
        GLES20.glGenTextures(1,textureObjectsIds, 0)

        if (textureObjectsIds[0] == 0){
            if (LoggerConfig.ON){
                Log.d(TAG, "Could not generate a new OpenGl texture object.")
            }
            return 0
        }
    }
}