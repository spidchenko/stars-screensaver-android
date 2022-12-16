package d.spidchenko.stars2d.programs

import android.content.Context
import android.opengl.GLES20.*
import d.spidchenko.stars2d.R

const val BYTES_PER_FLOAT = 4

class ParticleShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.particle_vertex_shader, R.raw.particle_fragment_shader) {

    // Uniform locations
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
    val aParticleSizeLocation by lazy { glGetAttribLocation(program, A_PARTICLE_SIZE) }

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