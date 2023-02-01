package d.spidchenko.stars2d.util

import android.opengl.GLES20.*

object ShaderHelper {

    fun buildProgram(vertexShaderSource: String, fragmentShaderSource: String): Int {

        val vertexShader = compileVertexShader(vertexShaderSource)
        val fragmentShader = compileFragmentShader(fragmentShaderSource)
        val program = linkProgram(vertexShader, fragmentShader)

        if (LoggerConfig.ON) {
            validateProgram(program)
        }

        return program
    }

    private fun compileVertexShader(shaderCode: String) =
        compileShader(GL_VERTEX_SHADER, shaderCode)

    private fun compileFragmentShader(shaderCode: String) =
        compileShader(GL_FRAGMENT_SHADER, shaderCode)

    private fun compileShader(type: Int, shaderCode: String): Int {
        val shaderObjectId = glCreateShader(type)

        if (shaderObjectId == 0) {
            Logger.log("Could not create new shader.")
            return 0
        }

        glShaderSource(shaderObjectId, shaderCode)
        glCompileShader(shaderObjectId)
        val compileStatus = IntArray(1)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)

        Logger.log(
            "Results of compiling source:\n" +
                    "$shaderCode\n" +
                    glGetShaderInfoLog(shaderObjectId)
        )

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId)
            Logger.log("Compilation of shader failed.")
            return 0
        }

        return shaderObjectId
    }

    private fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = glCreateProgram()

        if (programObjectId == 0) {
            Logger.log("Could not create new program.")
            return 0
        }

        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        glLinkProgram(programObjectId)

        val linkStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)

        Logger.log("Results of linking program:\n${glGetProgramInfoLog(programObjectId)}")

        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)
            Logger.log("Linking of program failed.")
            return 0
        }

        return programObjectId
    }

    private fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)
        val validateStatus = IntArray(1)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0)
        Logger.log(
            "Results of validating program: $validateStatus[0]\n" +
                    "Log:${glGetProgramInfoLog(programObjectId)}"
        )

        return validateStatus[0] != 0
    }
}