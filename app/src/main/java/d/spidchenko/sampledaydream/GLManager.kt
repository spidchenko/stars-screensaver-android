package d.spidchenko.sampledaydream

import android.opengl.GLES20
import android.util.Log



object GLManager {
    private const val TAG = "GLManager.LOG_TAG"

    private const val U_MATRIX = "uMVPMatrix"
    private const val U_POINT_SIZE = "diameter"
    private const val A_POSITION = "vPosition"
    private const val A_COLOR = "vColor"

    // number of coordinates per vertex in this array
    private const val COMPONENTS_PER_VERTEX = 3
    private const val FLOAT_SIZE = 4 // In bytes
    const val STRIDE = COMPONENTS_PER_VERTEX * FLOAT_SIZE
    const val ELEMENTS_PER_VERTEX = 3 // x,y,z

    // handle to fragment shader's vColor member
    val colorHandle by lazy { GLES20.glGetUniformLocation(program, A_COLOR) }

    // handle to vertex shader's vPosition member
    val positionHandle by lazy { GLES20.glGetAttribLocation(program, A_POSITION) }

    // handle to vertex shader's diameter member
    val pointDiameter by lazy { GLES20.glGetUniformLocation(program, U_POINT_SIZE) }

    // handle to shape's transformation matrix
    val vPMatrixHandle by lazy { GLES20.glGetUniformLocation(program, U_MATRIX) }

    var program = 0
        private set

    private val vertexShader =
        """
            uniform mat4 uMVPMatrix;
            uniform float diameter;
            attribute vec4 vPosition;
            void main() {
                    gl_Position = uMVPMatrix * vPosition;
                    gl_PointSize = diameter;
                }
        """.trimIndent()

    private val fragmentShader =
        """
            precision mediump float;
                uniform vec4 vColor;
                void main() {
                    gl_FragColor = vColor;
                }
        """.trimIndent()


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