package com.example.openglsample.shaderutil

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import com.example.openglsample.OpenglApplication

fun getTextBitmap(resourceId: Int, viewId: Int, context: Context): Bitmap? {
    val inflatedView = LayoutInflater.from(context).inflate(resourceId, null)
    return inflatedView.toBitmap()
}

fun getDisplayDimensions(): IntArray {
    val displayMetrics = OpenglApplication.instance.resources.displayMetrics
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels
    val density = displayMetrics.densityDpi
    return intArrayOf(width, height, density)
}
