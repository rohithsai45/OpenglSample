package com.example.openglsample.shaderutil

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.view.LayoutInflater
import com.example.openglsample.OpenglApplication
import java.io.IOException

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


fun readText(filePath: String): String {
    return getFileDescriptor(filePath)?.use { it.createInputStream()?.use { inputStream -> inputStream.reader().use { reader -> reader.readText() } } }
        ?: ""
}

fun getFileDescriptor(fileName: String): AssetFileDescriptor? {
    return try {
        OpenglApplication.instance.assets.openFd(fileName)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}