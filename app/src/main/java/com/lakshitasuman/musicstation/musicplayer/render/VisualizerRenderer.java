package com.lakshitasuman.musicstation.musicplayer.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.view.TextureView;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import com.lakshitasuman.musicstation.musicplayer.utils.FFTFrame;
import com.lakshitasuman.musicstation.musicplayer.utils.WaveFormFrame;

public class VisualizerRenderer implements Runnable, TextureView.SurfaceTextureListener {
    private static final float DBM_INTERP = 0.75f;
    private static final int DRAW_INTERVAL = 33;
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private Context mContext;
    private EGL10 mEgl10;
    private EGLContext mEglContext;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private FFTFrame mFFTFrame;
    private float[] mLastDbmArray;
    private Thread mRenderThread;
    private SceneController mSceneController;
    private int mSurfaceHeight;
    private SurfaceTexture mSurfaceTexture;
    private int mSurfaceWidth;
    private int mTextureWidth;
    private WaveFormFrame mWaveFormFrame;

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public VisualizerRenderer(Context context, int mTextureWidth) {
        mContext = context;
        this.mTextureWidth = mTextureWidth;
    }

    public void setSceneController(SceneController sceneController) {
        mSceneController = sceneController;
    }

    public void updateWaveFormFrame(WaveFormFrame waveFormFrame) {
        mWaveFormFrame = waveFormFrame;
    }

    public void updateFFTFrame(FFTFrame fFTFrame) {
        mFFTFrame = fFTFrame;
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int mSurfaceWidth, int mSurfaceHeight) {
        this.mSurfaceWidth = -mSurfaceWidth;
        this.mSurfaceHeight = -mSurfaceHeight;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mRenderThread == null || !mRenderThread.isAlive()) {
            return true;
        }
        mRenderThread.interrupt();
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        if (mRenderThread != null && mRenderThread.isAlive()) {
            mRenderThread.interrupt();
        }
        mRenderThread = new Thread(this);
        mSurfaceTexture = surfaceTexture;
        mSurfaceWidth = -i;
        mSurfaceHeight = -i2;
        mRenderThread.start();
    }


    @Override
    public void run() {
        GLScene activedScene;
        if (initGL(mSurfaceTexture)) {
            byte[] bArr = new byte[(mTextureWidth * 2)];
            int genAudioTexture = genAudioTexture(bArr, mTextureWidth);
            if (mSceneController != null) {
                mSceneController.onSetup(mContext, genAudioTexture, mTextureWidth);
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (mSurfaceWidth < 0 && mSurfaceHeight < 0) {
                        int i = -mSurfaceWidth;
                        mSurfaceWidth = i;
                        int i2 = -mSurfaceHeight;
                        mSurfaceHeight = i2;
                        GLES20.glViewport(0, 0, i, i2);
                    }
                    GLES20.glClear(16384);
                    GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
                    fillFFT(bArr, 0, mTextureWidth);
                    if (mWaveFormFrame != null) {
                        System.arraycopy(mWaveFormFrame.getRawWaveForm(), 0, bArr, mTextureWidth, mTextureWidth);
                    }
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(3553, genAudioTexture);
                    GLES20.glTexSubImage2D(3553, 0, 0, 0, mTextureWidth, 2, 6409, 5121, ByteBuffer.wrap(bArr));
                    if (!(mSceneController == null || (activedScene = mSceneController.getActivedScene()) == null)) {
                        activedScene.draw(mSurfaceWidth, mSurfaceHeight);
                    }
                    GLES20.glFlush();
                    mEgl10.eglSwapBuffers(mEglDisplay, mEglSurface);
                    Thread.sleep(33);
                } catch (InterruptedException exceptionxception) {
                    Thread.currentThread().interrupt();
                }
            }
            GLES20.glDeleteTextures(1, new int[]{genAudioTexture}, 0);
            return;
        }
        throw new RuntimeException("Initializing OpenGL failed");
    }





    private void fillFFT(byte[] bArr, int i, int i2) {
        if (mFFTFrame != null && i + i2 <= bArr.length) {
            mLastDbmArray = new float[i2];
            mFFTFrame.calculate(i2, mLastDbmArray);
            float f = Float.MIN_VALUE;
            float f2 = Float.MAX_VALUE;
            for (int i3 = 0; i3 < i2; i3++) {
                float mLastDb = mLastDbmArray[i3];
                if (mLastDb < 0.0f) {
                    mLastDb = 0.0f;
                }
                if (mLastDb > 1.0f) {
                    mLastDb = 1.0f;
                }
                if (this.mLastDbmArray != null) {
                    float f4 = this.mLastDbmArray[i3];
                    if (f4 - mLastDb > 0.025f) {
                        mLastDb = f4 - 0.025f;
                    } else {
                        mLastDb = (mLastDb * 0.25f) + (this.mLastDbmArray[i3] * DBM_INTERP);
                    }
                }
                if (mLastDb > f) {
                    f = mLastDb;
                }
                if (mLastDb < f2) {
                    f2 = mLastDb;
                }
                mLastDbmArray[i3] = mLastDb;
                bArr[i + i3] = (byte) ((int) (mLastDb * 255.0f));
            }

        }
    }



    private int genAudioTexture(byte[] bArr, int i) {
        GLES20.glActiveTexture(33984);
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        int i2 = iArr[0];
        GLES20.glBindTexture(3553, i2);
        GLES20.glTexParameterf(3553, 10241, 9728.0f);
        GLES20.glTexParameterf(3553, 10240, 9728.0f);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glBindTexture(3553, i2);
        GLES20.glTexImage2D(3553, 0, 6409, i, 2, 0, 6409, 5121, ByteBuffer.wrap(bArr));
        return i2;
    }


    private boolean initGL(SurfaceTexture surfaceTexture) {
        mEgl10 = (EGL10) EGLContext.getEGL();
        mEglDisplay = mEgl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            return false;
        }
        if (!mEgl10.eglInitialize(mEglDisplay, new int[2])) {
            return false;
        }
        int[] iArr = new int[1];
        EGLConfig[] eGLConfigArr = new EGLConfig[1];
        if (!mEgl10.eglChooseConfig(mEglDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 0, 12326, 0, 12344}, eGLConfigArr, 1, iArr)) {
            return false;
        }
        EGLConfig eGLConfig = iArr[0] > 0 ? eGLConfigArr[0] : null;
        if (eGLConfig == null) {
            return false;
        }
        mEglContext = mEgl10.eglCreateContext(mEglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, 12344});
        mEglSurface = mEgl10.eglCreateWindowSurface(mEglDisplay, eGLConfig, surfaceTexture, (int[]) null);
        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE || !mEgl10.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            return false;
        }
        return true;
    }
}
