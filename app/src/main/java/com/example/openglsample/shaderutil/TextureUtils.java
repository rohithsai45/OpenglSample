package com.example.openglsample.shaderutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by burt on 2016. 6. 24..
 */
public class TextureUtils {

    public static int loadTexture(Context context, int drawableID) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableID);

        int textureName = loadTexture(bitmap);

        bitmap.recycle();

        return textureName;
    }

    public static int loadTexture(Bitmap bitmap) {

        int[] textureName = new int[1];
        GLES20.glGenTextures(1, textureName, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return textureName[0];
    }

}
