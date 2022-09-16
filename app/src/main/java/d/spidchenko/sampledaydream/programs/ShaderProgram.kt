package d.spidchenko.sampledaydream.programs

import android.content.Context
import android.opengl.GLES20.glUseProgram
import d.spidchenko.sampledaydream.util.ShaderHelper
import d.spidchenko.sampledaydream.util.TextResourceReader


open class ShaderProgram protected constructor(
    context: Context,
    vertexShaderResourceId: Int,
    fragmentShaderResourceId: Int
) {

    companion object {
        //Uniform constants
        @JvmStatic
        protected val U_MATRIX = "u_Matrix"

        @JvmStatic
        protected val U_TEXTURE_UNIT = "u_TextureUnit"

        @JvmStatic
        protected val U_TIME = "u_Time"

        //Attribute constants
        @JvmStatic
        protected val A_POSITION = "a_Position"

        @JvmStatic
        protected val A_COLOR = "a_Color"

        @JvmStatic
        protected val A_TEXTURE_COORDINATE = "a_TextureCoordinate"

        @JvmStatic
        protected val A_DIRECTION_VECTOR = "a_DirectionVector"

        @JvmStatic
        protected val A_PARTICLE_START_TIME = "a_ParticleStartTime"

        @JvmStatic
        protected val A_PARTICLE_SIZE = "a_ParticleSize"
    }


    //Shader program
    protected val program: Int

    init {
        program = ShaderHelper.buildProgram(
            TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
            TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
        )
    }

    fun useProgram() = glUseProgram(program)
}