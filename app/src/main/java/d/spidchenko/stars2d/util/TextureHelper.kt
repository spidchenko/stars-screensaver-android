package d.spidchenko.stars2d.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log

private const val TAG = "TextureHelper.LOG_TAG"

object TextureHelper {
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectsIds = IntArray(1)
        glGenTextures(1, textureObjectsIds, 0)

        if (textureObjectsIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.d(TAG, "Could not generate a new OpenGl texture object.")
            }
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.d(TAG, "Resource ID $resourceId could not be decoded.")
            }
            glDeleteTextures(1, textureObjectsIds, 0)
            return 0
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectsIds[0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        glGenerateMipmap(GL_TEXTURE_2D)
        glBindTexture(GL_TEXTURE_2D, 0)
        return textureObjectsIds[0]
    }
}