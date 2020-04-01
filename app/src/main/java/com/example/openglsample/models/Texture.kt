package com.example.openglsample.models

import com.example.openglsample.shaderutil.ShaderProgram

/**
 * code from the url at { @link https://developer.android.com/training/graphics/opengl/shapes.html }
 * Created by burt on 2016. 6. 16..
 */
class Texture(
    shader: ShaderProgram
) : Model("square", shader, textureCoords, indices) {
    companion object {
        var textureCoords = floatArrayOf(
            // these position coordinates will be replaced with actual bitmap position codordinates in Model
            0.3f, -0.3f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,// top left
            0.3f, 0.3f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,// bottom left
            -0.3f, 0.3f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, // bottom right
            -0.3f, -0.3f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f
        )
        val indices = shortArrayOf(
            0, 1, 2,
            2, 3, 0
        )
    }
}
