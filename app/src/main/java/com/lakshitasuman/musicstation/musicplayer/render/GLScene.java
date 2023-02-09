package com.lakshitasuman.musicstation.musicplayer.render;

import android.opengl.GLES20;

import androidx.annotation.CallSuper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class GLScene {
    private static final float[] SQUARE_COORDS = {1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
    private static final float[] TEXTURE_COORDS = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static FloatBuffer TEXTURE_COORD_BUF = ByteBuffer.allocateDirect(TEXTURE_COORDS.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private static FloatBuffer VERTEX_BUF = ByteBuffer.allocateDirect(SQUARE_COORDS.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private int iFrame = 0;
    private long mStartTime = System.currentTimeMillis();
    public abstract void onDraw(int i, int i2);
    static {
        VERTEX_BUF.put(SQUARE_COORDS);
        VERTEX_BUF.position(0);
        TEXTURE_COORD_BUF.put(TEXTURE_COORDS);
        TEXTURE_COORD_BUF.position(0);
    }

    @CallSuper
    public void reset() {
        iFrame = 0;
        mStartTime = System.currentTimeMillis();
    }


    public final void draw(int i, int i2) {
        onDraw(i, i2);
        iFrame++;
    }



    public void runShandertoyProgram(int i, int[] iArr, int[] iArr2, int[][] iArr3) {
        runShandertoyProgram(i, VERTEX_BUF, TEXTURE_COORD_BUF, iArr, iArr2, iArr3);
    }


    public void runShandertoyProgram(int i, FloatBuffer floatBuffer, FloatBuffer floatBuffer2, int[] iArr, int[] iArr2, int[][] iArr3) {
        GLES20.glUseProgram(i);
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(i, "iResolution"), 1, FloatBuffer.wrap(new float[]{(float) iArr[0], (float) iArr[1], 1.0f}));
        GLES20.glUniform1f(GLES20.glGetUniformLocation(i, "iTime"), ((float) (System.currentTimeMillis() - mStartTime)) / 1000.0f);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(i, "iFrame"), iFrame);
        int glGetAttribLocation = GLES20.glGetAttribLocation(i, "vPosition");
        GLES20.glEnableVertexAttribArray(glGetAttribLocation);
        GLES20.glVertexAttribPointer(glGetAttribLocation, 2, 5126, false, 8, floatBuffer);
        int glGetAttribLocation2 = GLES20.glGetAttribLocation(i, "vTexCoord");
        GLES20.glEnableVertexAttribArray(glGetAttribLocation2);
        GLES20.glVertexAttribPointer(glGetAttribLocation2, 2, 5126, false, 8, floatBuffer2);
        for (int i3 = 0; i3 < iArr2.length; i3++) {
            int glGetUniformLocation = GLES20.glGetUniformLocation(i, "iChannel" + i3);
            GLES20.glActiveTexture(33984 + i3);
            GLES20.glBindTexture(3553, iArr2[i3]);
            GLES20.glUniform1i(glGetUniformLocation, i3);
        }
        float[] fArr = new float[(iArr3.length * 3)];
        for (int i4 = 0; i4 < iArr3.length; i4++) {
            int i5 = i4 * 3;
            fArr[i5] = (float) iArr3[i4][0];
            fArr[i5 + 1] = (float) iArr3[i4][1];
            fArr[i5 + 2] = 1.0f;
        }
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(i, "iChannelResolution"), fArr.length, FloatBuffer.wrap(fArr));
        GLES20.glDrawArrays(5, 0, 4);
    }
}
