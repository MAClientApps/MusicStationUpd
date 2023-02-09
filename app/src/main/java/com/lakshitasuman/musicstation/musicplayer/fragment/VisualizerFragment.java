package com.lakshitasuman.musicstation.musicplayer.fragment;

import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.List;

import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.musicplayer.render.GLScene;
import com.lakshitasuman.musicstation.musicplayer.render.SceneController;
import com.lakshitasuman.musicstation.musicplayer.render.VisualizerRenderer;

public class VisualizerFragment extends Fragment implements Visualizer.OnDataCaptureListener {
    public static int captureSize;

    public static RotateAnimation rotate;

    public static VisualizerRenderer mRender;
    public static SceneController mSceneController;
    public static List<Pair<String, ? extends GLScene>> mSceneList;
    public static Visualizer mVisualizer;
    public  static   CardView rotateImage;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_visualizer, viewGroup, false);
        rotateImage=inflate.findViewById(R.id.rotateImage);
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        rotateImage.startAnimation(rotate);

        initVisualizer();
        startVisualizer();
        return inflate;
    }



    public void startVisualizer() {
        /*mVisualizer = new Visualizer(PlayerActivity.mMediaPlayer.getAudioSessionId());
        mVisualizer.setEnabled(false);
        mVisualizer.setCaptureSize(captureSize);
        mVisualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
        mVisualizer.setEnabled(true);*/
    }


    public void initVisualizer() {
       /* captureSize = Visualizer.getCaptureSizeRange()[1];
        int i = 512;
        if (captureSize <= 512) {
            i = captureSize;
        }
        captureSize = i;
        TextureView textureView = new TextureView(getContext());
        display.addView(textureView);
        mRender = new VisualizerRenderer(getContext(), captureSize / 2);
        textureView.setSurfaceTextureListener(mRender);
        textureView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mRender.onSurfaceTextureSizeChanged((SurfaceTexture) null, view.getWidth(), view.getHeight());
            }
        });
        textureView.requestLayout();
        mSceneController = new SceneController() {
            @Override
            public void onSetup(Context context, int i, int i2) {
                mSceneList = new ArrayList();
                changeScene(new EnhancedSpectrumScene(context, i, i2));
            }
        };
        mRender.setSceneController(mSceneController);*/
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bArr, int i) {
       // mRender.updateWaveFormFrame(new WaveFormFrame(bArr, 0, bArr.length / 2));
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] bArr, int i) {
       // mRender.updateFFTFrame(new FFTFrame(bArr, 0, bArr.length / 2));
    }
}
