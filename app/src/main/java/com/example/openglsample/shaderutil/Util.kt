package com.example.openglsample.shaderutil

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.TextView

fun getTextBitmap(resourceId: Int, viewId: Int, context: Context): Bitmap? {
    val inflatedView = LayoutInflater.from(context).inflate(resourceId, null)
    val textView = inflatedView.findViewById<TextView>(viewId)
    return inflatedView.toBitmap()
}
