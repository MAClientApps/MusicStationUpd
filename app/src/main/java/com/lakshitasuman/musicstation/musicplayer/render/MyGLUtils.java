package com.lakshitasuman.musicstation.musicplayer.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyGLUtils {
    private static final String TAG = "MyGLUtils";

    public static int genTexture() {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        GLES20.glBindTexture(3553, iArr[0]);
        GLES20.glTexParameterf(3553, 10241, 9729.0f);
        GLES20.glTexParameterf(3553, 10240, 9729.0f);
        GLES20.glTexParameteri(3553, 10242, 10497);
        GLES20.glTexParameteri(3553, 10243, 10497);
        return iArr[0];
    }

    public static int loadTexture(Context context, int i, int[] iArr) {
        int genTexture = genTexture();
        if (genTexture != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), i, options);
            iArr[0] = options.outWidth;
            iArr[1] = options.outHeight;
            options.inJustDecodeBounds = false;
            Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i, options);
            GLUtils.texImage2D(3553, 0, decodeResource, 0);
            decodeResource.recycle();
        }
        return genTexture;
    }

    public static int buildProgram(Context context, @RawRes int i, @RawRes int i2) {
        return buildProgram(getStringFromRaw(context, i), getStringFromRaw(context, i2));
    }



    public static int buildProgram(String str, String str2) {
        int buildShader;
        int glCreateProgram;
        int buildShader2 = buildShader(35633, str);
        if (buildShader2 == 0 || (buildShader = buildShader(35632, str2)) == 0 || (glCreateProgram = GLES20.glCreateProgram()) == 0) {
            return 0;
        }
        GLES20.glAttachShader(glCreateProgram, buildShader2);
        GLES20.glAttachShader(glCreateProgram, buildShader);
        GLES20.glLinkProgram(glCreateProgram);
        return glCreateProgram;
    }

    public static int buildShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader == 0) {
            return 0;
        }
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 0) {
            return glCreateShader;
        }
        Log.e(TAG, GLES20.glGetShaderInfoLog(glCreateShader));
        GLES20.glDeleteShader(glCreateShader);
        return 0;
    }

    private static String getStringFromRaw(Context context, @RawRes int i) {
        try {
            InputStream openRawResource = context.getResources().openRawResource(i);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            for (int read = openRawResource.read(); read != -1; read = openRawResource.read()) {
                byteArrayOutputStream.write(read);
            }
            String byteArrayOutputStream2 = byteArrayOutputStream.toString();
            openRawResource.close();
            return byteArrayOutputStream2;
        } catch (IOException exceptionxception) {
            return "";
        }
    }
}
