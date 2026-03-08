package d.spidchenko.stars2d.programs

import android.content.Context
import android.opengl.GLES20.*
import d.spidchenko.stars2d.util.ShaderHelper

class TextureShaderProgram(context: Context) {

    private val program: Int

    private val uTextureUnitLocation: Int
    private val uAlphaLocation: Int
    val aPositionLocation: Int
    val aTextureCoordinatesLocation: Int

    init {
        val vertexShaderSource = """
            attribute vec4 a_Position;
            attribute vec2 a_TextureCoordinates;
            varying vec2 v_TextureCoordinates;
            void main() {
                v_TextureCoordinates = a_TextureCoordinates;
                gl_Position = a_Position;
            }
        """.trimIndent()

        val fragmentShaderSource = """
            precision mediump float;
            uniform sampler2D u_TextureUnit;
            uniform float u_Alpha;
            varying vec2 v_TextureCoordinates;
            void main() {
                vec4 color = texture2D(u_TextureUnit, v_TextureCoordinates);
                // Multiply both RGB and Alpha by the fade factor
                gl_FragColor = color * u_Alpha;
            }
        """.trimIndent()

        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource)

        uTextureUnitLocation = glGetUniformLocation(program, "u_TextureUnit")
        uAlphaLocation = glGetUniformLocation(program, "u_Alpha")
        aPositionLocation = glGetAttribLocation(program, "a_Position")
        aTextureCoordinatesLocation = glGetAttribLocation(program, "a_TextureCoordinates")
    }

    fun useProgram() {
        glUseProgram(program)
    }

    fun setUniforms(textureId: Int, alpha: Float) {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        glUniform1i(uTextureUnitLocation, 0)
        glUniform1f(uAlphaLocation, alpha)
    }
}