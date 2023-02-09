package com.lakshitasuman.musicstation.musicplayer.render;

import android.content.Context;

public abstract class SceneController {
    private GLScene mActivedScene;

    public abstract void onSetup(Context context, int i, int i2);

    public void changeScene(GLScene gLScene) {
        gLScene.reset();
        this.mActivedScene = gLScene;
    }

    
    public GLScene getActivedScene() {
        return this.mActivedScene;
    }
}
