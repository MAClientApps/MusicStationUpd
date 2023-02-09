package com.lakshitasuman.musicstation.voice_change.dataMng;
import com.lakshitasuman.musicstation.voice_change.constants.IVoiceChangerConstants;
import com.lakshitasuman.musicstation.voice_change.models.EffectModel;
import com.lakshitasuman.musicstation.voice_change.utils.DBLog;
import com.lakshitasuman.musicstation.voice_change.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParsingUtils implements IVoiceChangerConstants {
    public static final String TAG = "JsonParsingUtils";

    public static ArrayList<EffectModel> parsingListEffectObject(String str) {
        if (StringUtils.isEmptyString(str)) {
            return null;
        }
        try {
            JSONArray jSONArray = new JSONArray(str);
            int length = jSONArray.length();
            if (length <= 0) {
                return null;
            }
            ArrayList<EffectModel> arrayList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                String id = jSONObject.getString("id");
                String name = jSONObject.getString("name");
                String imagePath = jSONObject.getString("image");
                int pitch = jSONObject.getInt("pitch");
                int rate = jSONObject.getInt("rate");
                boolean flanger = jSONObject.getBoolean("flanger");
                boolean reverse = jSONObject.opt("reverse") != null ? jSONObject.getBoolean("reverse") : false;
                boolean echo = jSONObject.opt("echo") != null ? jSONObject.getBoolean("echo") : false;
                EffectModel effectObject = new EffectModel(id, name, imagePath,pitch, (float) rate);
                effectObject.setFlanger(flanger);
                effectObject.setReverse(reverse);
                effectObject.setEcho(echo);
                arrayList.add(effectObject);
                JSONArray jSONArray2 = jSONObject.getJSONArray("reverb");
                if (jSONArray2.length() > 0) {
                    float[] fArr = new float[3];
                    for (int i4 = 0; i4 < 3; i4++) {
                        fArr[i4] = (float) jSONArray2.getDouble(i4);
                    }
                    effectObject.setReverb(fArr);
                }
                if (jSONObject.opt("eq") != null) {
                    JSONArray jSONArray3 = jSONObject.getJSONArray("eq");
                    if (jSONArray3.length() > 0) {
                        float[] fArr2 = new float[3];
                        for (int i5 = 0; i5 < 3; i5++) {
                            fArr2[i5] = (float) jSONArray3.getDouble(i5);
                        }
                        effectObject.setEq(fArr2);
                    }
                }
            }
            DBLog.d(TAG, "===================>size effect =" + arrayList.size());
            return arrayList;
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
            return null;
        }
    }
}
