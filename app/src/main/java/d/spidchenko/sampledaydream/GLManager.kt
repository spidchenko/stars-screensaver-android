package d.spidchenko.sampledaydream

import android.opengl.GLES20
import android.util.Log


object GLManager {
    private const val TAG = "GLManager.LOG_TAG"

    private const val U_MATRIX = "u_Matrix"
    private const val U_POINT_SIZE = "diameter"
    private const val U_TIME = "u_Time"
    private const val U_TEXTURE_UNIT = "u_TextureUnit"
    private const val A_POSITION = "a_Position"
    private const val A_COLOR = "a_Color"
    private const val A_DIRECTION_VECTOR = "a_DirectionVector"
    private const val A_PARTICLE_START_TIME = "a_ParticleStartTime"


    // number of coordinates per vertex in this array
    private const val COMPONENTS_PER_VERTEX = 3
    const val BYTES_PER_FLOAT = 4

    //    const val STRIDE = COMPONENTS_PER_VERTEX * FLOAT_SIZE
    const val ELEMENTS_PER_VERTEX = 3 // x,y,z

    //    val pointDiameter by lazy { GLES20.glGetUniformLocation(program, U_POINT_SIZE) }
    private val uMatrixLocation by lazy { GLES20.glGetUniformLocation(program, U_MATRIX) }
    private val uTimeLocation by lazy { GLES20.glGetUniformLocation(program, U_TIME) }
    private val uTextureUnitLocation by lazy {
        GLES20.glGetUniformLocation(
            program,
            U_TEXTURE_UNIT
        )
    }

    val aPositionLocation by lazy { GLES20.glGetAttribLocation(program, A_POSITION) }
    val aColorLocation by lazy { GLES20.glGetAttribLocation(program, A_COLOR) }
    val aDirectionVectorLocation by lazy { GLES20.glGetAttribLocation(program, A_DIRECTION_VECTOR) }
    val aParticleStartTimeLocation by lazy {
        GLES20.glGetAttribLocation(
            program,
            A_PARTICLE_START_TIME
        )
    }

    var program = 0
        private set

    private val vertexShader =
        """
            uniform mat4 u_Matrix;
            uniform float u_Time;
            attribute vec3 a_Position;
            attribute vec3 a_Color;
            attribute vec3 a_DirectionVector;
            attribute float a_ParticleStartTime;
            varying vec3 v_Color;
            varying float v_ElapsedTime;
            
            void main()
            {
                v_Color = a_Color;
                v_ElapsedTime = u_Time - a_ParticleStartTime;
                float gravityFactor = v_ElapsedTime * v_ElapsedTime / 8.0;
                vec3 currentPosition = a_Position + (a_DirectionVector * v_ElapsedTime);
                currentPosition.y -= gravityFactor;
                gl_Position = u_Matrix * vec4(currentPosition, 1.0);
                gl_PointSize = 25.0;
            }
        """.trimIndent()

    private val fragmentShader =
        """
            precision mediump float;
            varying vec3 v_Color;
            varying float v_ElapsedTime;
            void main()
            {
                gl_FragColor = vec4(v_Color / v_ElapsedTime, 1.0) * texture2D(u_TextureUnit, gl_PointCoord);
            }
        """.trimIndent()

    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        // Matrix
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        // Time
        GLES20.glUniform1f(uTimeLocation, elapsedTime)
        // Texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

    fun useProgram() = GLES20.glUseProgram(program)

    fun buildProgram() {
        Log.d(TAG, "GL Manager init")
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)
        }


    }

    private fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}