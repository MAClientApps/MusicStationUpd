
package com.un4seen.bass;

import java.nio.ByteBuffer;

public class BASSenc {
    public static final int BASS_CONFIG_ENCODE_CAST_PROXY = 66321;
    public static final int BASS_CONFIG_ENCODE_CAST_TIMEOUT = 66320;
    public static final int BASS_CONFIG_ENCODE_PRIORITY = 66304;
    public static final int BASS_CONFIG_ENCODE_QUEUE = 66305;
    public static final int BASS_ENCODE_AIFF = 16384;
    public static final int BASS_ENCODE_AUTOFREE = 262144;
    public static final int BASS_ENCODE_BIGEND = 16;
    public static final int BASS_ENCODE_CAST_NOLIMIT = 4096;
    public static final int BASS_ENCODE_COUNT_CAST = 2;
    public static final int BASS_ENCODE_COUNT_IN = 0;
    public static final int BASS_ENCODE_COUNT_OUT = 1;
    public static final int BASS_ENCODE_COUNT_QUEUE = 3;
    public static final int BASS_ENCODE_COUNT_QUEUE_FAIL = 5;
    public static final int BASS_ENCODE_COUNT_QUEUE_LIMIT = 4;
    public static final int BASS_ENCODE_FP_16BIT = 4;
    public static final int BASS_ENCODE_FP_24BIT = 6;
    public static final int BASS_ENCODE_FP_32BIT = 8;
    public static final int BASS_ENCODE_FP_8BIT = 2;
    public static final int BASS_ENCODE_LIMIT = 8192;
    public static final int BASS_ENCODE_NOHEAD = 1;
    public static final int BASS_ENCODE_NOTIFY_CAST = 2;
    public static final int BASS_ENCODE_NOTIFY_CAST_TIMEOUT = 65536;
    public static final int BASS_ENCODE_NOTIFY_ENCODER = 1;
    public static final int BASS_ENCODE_NOTIFY_FREE = 65538;
    public static final int BASS_ENCODE_NOTIFY_QUEUE_FULL = 65537;
    public static final int BASS_ENCODE_PAUSE = 32;
    public static final int BASS_ENCODE_PCM = 64;
    public static final int BASS_ENCODE_QUEUE = 512;
    public static final int BASS_ENCODE_RF64 = 128;
    public static final int BASS_ENCODE_SERVER_NOHTTP = 1;
    public static final int BASS_ENCODE_STATS_ICE = 1;
    public static final int BASS_ENCODE_STATS_ICESERV = 2;
    public static final int BASS_ENCODE_STATS_SHOUT = 0;
    public static final String BASS_ENCODE_TYPE_AAC = "audio/aacp";
    public static final String BASS_ENCODE_TYPE_MP3 = "audio/mpeg";
    public static final String BASS_ENCODE_TYPE_OGG = "application/ogg";
    public static final int BASS_ENCODE_WFEXT = 1024;
    public static final int BASS_ERROR_CAST_DENIED = 2100;

    static {
        System.loadLibrary((String)"bassenc");
    }

    public static native boolean BASS_Encode_AddChunk(int var0, String var1, ByteBuffer var2, int var3);

    public static native String BASS_Encode_CastGetStats(int var0, int var1, String var2);

    public static native boolean BASS_Encode_CastInit(int var0, String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, int var9, boolean var10);

    public static native boolean BASS_Encode_CastSendMeta(int var0, int var1, ByteBuffer var2, int var3);

    public static native boolean BASS_Encode_CastSetTitle(int var0, String var1, String var2);

    public static native int BASS_Encode_GetChannel(int var0);

    public static native long BASS_Encode_GetCount(int var0, int var1);

    public static native int BASS_Encode_GetVersion();

    public static native int BASS_Encode_IsActive(int var0);

    public static native int BASS_Encode_ServerInit(int var0, String var1, int var2, int var3, int var4, ENCODECLIENTPROC var5, Object var6);

    public static native boolean BASS_Encode_ServerKick(int var0, String var1);

    public static native boolean BASS_Encode_SetChannel(int var0, int var1);

    public static native boolean BASS_Encode_SetNotify(int var0, ENCODENOTIFYPROC var1, Object var2);

    public static native boolean BASS_Encode_SetPaused(int var0, boolean var1);

    public static native int BASS_Encode_Start(int var0, String var1, int var2, ENCODEPROC var3, Object var4);

    public static native int BASS_Encode_StartLimit(int var0, String var1, int var2, ENCODEPROC var3, Object var4, int var5);

    public static native int BASS_Encode_StartUser(int var0, String var1, int var2, ENCODERPROC var3, Object var4);

    public static native boolean BASS_Encode_Stop(int var0);

    public static native boolean BASS_Encode_StopEx(int var0, boolean var1);

    public static native boolean BASS_Encode_Write(int var0, ByteBuffer var1, int var2);

    public static interface ENCODECLIENTPROC {
        public boolean ENCODECLIENTPROC(int var1, boolean var2, String var3, String var4, Object var5);
    }

    public static interface ENCODENOTIFYPROC {
        public void ENCODENOTIFYPROC(int var1, int var2, Object var3);
    }

    public static interface ENCODEPROC {
        public void ENCODEPROC(int var1, int var2, ByteBuffer var3, int var4, Object var5);
    }

    public static interface ENCODERPROC {
        public int ENCODERPROC(int var1, int var2, ByteBuffer var3, int var4, int var5, Object var6);
    }

}

