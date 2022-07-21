package d.spidchenko.sampledaydream

import android.opengl.GLES20
import android.util.Log

private const val TAG = "GLManager.LOG_TAG"

object GLManager {
    // number of coordinates per vertex in this array
    const val COMPONENTS_PER_VERTEX = 3
    const val FLOAT_SIZE = 4
    const val STRIDE = COMPONENTS_PER_VERTEX * FLOAT_SIZE
    const val ELEMENTS_PER_VERTEX = 3 // x,y,z

    const val U_MATRIX = "uMVPMatrix"
    const val A_POSITION = "vPosition"
    const val U_COLOR = "vColor"
    var uMatrixLocation = 0
    var aPositionLocation = 0
    var uColorLocation = 0
    var program = 0
        private set

    private val vertexShader =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "  gl_PointSize = 5.0;" +
                "}"

    private val fragmentShader =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

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