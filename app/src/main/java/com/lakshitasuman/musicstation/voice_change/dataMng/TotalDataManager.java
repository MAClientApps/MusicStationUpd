package com.lakshitasuman.musicstation.voice_change.dataMng;





import com.lakshitasuman.musicstation.voice_change.constants.IVoiceChangerConstants;
import com.lakshitasuman.musicstation.voice_change.models.EffectModel;
import com.lakshitasuman.musicstation.voice_change.soundMng.SoundManager;

import java.util.ArrayList;
import java.util.Iterator;

public class TotalDataManager implements IVoiceChangerConstants {
    public static final String TAG = "TotalDataManager";
    private static TotalDataManager totalDataManager;
    private ArrayList<EffectModel> listEffectObjects;

    public static TotalDataManager getInstance() {
        if (totalDataManager == null) {
            totalDataManager = new TotalDataManager();
        }
        return totalDataManager;
    }

    private TotalDataManager() {
    }



    public void onDestroy() {

        if (listEffectObjects != null) {
            listEffectObjects.clear();
            this.listEffectObjects = null;
        }
        try {
            SoundManager.getInstance().releaseSound();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        totalDataManager = null;
    }

    public ArrayList<EffectModel> getListEffectObjects() {
        return listEffectObjects;
    }

    public void setListEffectObjects(ArrayList<EffectModel> arrayList) {
        listEffectObjects = arrayList;
    }



    public void onResetState() {
        if (listEffectObjects != null && listEffectObjects.size() > 0) {
            Iterator<EffectModel> it = listEffectObjects.iterator();
            while (it.hasNext()) {
                it.next().setPlaying(false);
            }
        }
    }
}
