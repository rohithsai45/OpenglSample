package com.example.openglsample.models

import com.example.openglsample.shaderutil.ShaderProgram

/**
 * code from the url at { @link https://developer.android.com/training/graphics/opengl/shapes.html }
 * Created by burt on 2016. 6. 16..
 */
class Square(shader: ShaderProgram) :
    Model("square", shader, squareCoords, indices) {
    companion object {
        val squareCoords = floatArrayOf(
            5.0f, -5.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // top left
            5.0f, 5.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,  // bottom left
            -5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,  // bottom right
            -5.0f, -5.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
        )
        val indices = shortArrayOf(
            0, 1, 2,
            2, 3, 0
        )
    }
}
