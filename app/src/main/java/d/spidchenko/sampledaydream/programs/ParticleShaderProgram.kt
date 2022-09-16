package d.spidchenko.sampledaydream.programs

import android.content.Context
import android.opengl.GLES20.*
import d.spidchenko.sampledaydream.R

private const val TAG = "ParticleShaderProgram.LOG_TAG"

private const val U_POINT_SIZE = "diameter"

private const val COMPONENTS_PER_VERTEX = 3
const val BYTES_PER_FLOAT = 4
const val ELEMENTS_PER_VERTEX = 3

class ParticleShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader) {

    // Uniform locations
    //    val pointDiameter by lazy { glGetUniformLocation(program, U_POINT_SIZE) }
    private val uMatrixLocation by lazy { glGetUniformLocation(program, U_MATRIX) }
    private val uTimeLocation by lazy { glGetUniformLocation(program, U_TIME) }
    private val uTextureUnitLocation by lazy { glGetUniformLocation(program, U_TEXTURE_UNIT) }

    // AttributeLocations
    val aPositionLocation by lazy { glGetAttribLocation(program, A_POSITION) }
    val aColorLocation by lazy { glGetAttribLocation(program, A_COLOR) }
    val aDirectionVectorLocation by lazy {
        glGetAttribLocation(program, A_DIRECTION_VECTOR)
    }
    val aParticleStartTimeLocation by lazy {
        glGetAttribLocation(program, A_PARTICLE_START_TIME)
    }

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        // Matrix
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        // Time
        glUniform1f(uTimeLocation, elapsedTime)
        // Texture
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
    }

}