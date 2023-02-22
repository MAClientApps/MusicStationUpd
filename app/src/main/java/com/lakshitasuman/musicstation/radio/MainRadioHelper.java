package com.lakshitasuman.musicstation.radio;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import com.sdk.perelander.Mob;
import com.sdk.perelander.MobConfig;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import com.lakshitasuman.musicstation.BuildConfig;
import com.lakshitasuman.musicstation.R;
import com.lakshitasuman.musicstation.radio.alarm.RadioAlarmManager;
import com.lakshitasuman.musicstation.radio.history.TrackHistoryRepository;
import com.lakshitasuman.musicstation.radio.players.mpd.MPDClient;
import com.lakshitasuman.musicstation.radio.station.live.metadata.TrackMetadataSearcher;
import com.lakshitasuman.musicstation.radio.proxy.ProxySettings;
import com.lakshitasuman.musicstation.radio.recording.RecordingsManager;
import com.lakshitasuman.musicstation.radio.utils.TvChannelManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainRadioHelper extends MultiDexApplication {

    private com.lakshitasuman.musicstation.radio.HistoryManager historyManager;
    private com.lakshitasuman.musicstation.radio.FavouriteManager favouriteManager;
    private RecordingsManager recordingsManager;
    private RadioAlarmManager alarmManager;
    private TvChannelManager tvChannelManager;

    private TrackHistoryRepository trackHistoryRepository;

    private MPDClient mpdClient;

    private CastHandler castHandler;

    private TrackMetadataSearcher trackMetadataSearcher;

    private ConnectionPool connectionPool;
    private OkHttpClient httpClient;
    public MobConfig config;
    private Interceptor testsInterceptor;

    public class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        config = new MobConfig(this, "kwdv3iw65slc","3x0h3j");
        Mob.onCreate(config);
        registerActivityLifecycleCallbacks(new MobLifecycleCallbacks());

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        GoogleProviderHelper.use(getBaseContext());
        connectionPool = new ConnectionPool();
        rebuildHttpClient();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(newHttpClientForPicasso()));
        Picasso picassoInstance = builder.build();
        Picasso.setSingletonInstance(picassoInstance);
        com.lakshitasuman.musicstation.radio.CountryCodeDictionary.getInstance().load(this);
        com.lakshitasuman.musicstation.radio.CountryFlagsLoader.getInstance();
        historyManager = new com.lakshitasuman.musicstation.radio.HistoryManager(this);
        favouriteManager = new com.lakshitasuman.musicstation.radio.FavouriteManager(this);
        recordingsManager = new RecordingsManager();
        alarmManager = new RadioAlarmManager(this);
        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            tvChannelManager = new TvChannelManager(this);
            favouriteManager.addObserver(tvChannelManager);
        }

        trackHistoryRepository = new TrackHistoryRepository(this);

        mpdClient = new MPDClient(this);

        castHandler = new CastHandler();

        trackMetadataSearcher = new TrackMetadataSearcher(httpClient);

        recordingsManager.updateRecordingsList();
    }

    public void setTestsInterceptor(Interceptor testsInterceptor) {
        this.testsInterceptor = testsInterceptor;
    }

    private static final class MobLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityResumed(Activity activity) {
            Mob.onResume(activity);
        }
        @Override
        public void onActivityPaused(Activity activity) {
            Mob.onPause();
        }
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }
        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }
        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }
        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }
        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    }

    public void rebuildHttpClient() {
        OkHttpClient.Builder builder = newHttpClient()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new UserAgentInterceptor("RadioDroid2/" + BuildConfig.VERSION_NAME));

        httpClient = builder.build();
    }

    public com.lakshitasuman.musicstation.radio.HistoryManager getHistoryManager() {
        return historyManager;
    }

    public com.lakshitasuman.musicstation.radio.FavouriteManager getFavouriteManager() {
        return favouriteManager;
    }

    public RecordingsManager getRecordingsManager() {
        return recordingsManager;
    }

    public RadioAlarmManager getAlarmManager() {
        return alarmManager;
    }

    public TrackHistoryRepository getTrackHistoryRepository() {
        return trackHistoryRepository;
    }

    public MPDClient getMpdClient() {
        return mpdClient;
    }

    public CastHandler getCastHandler() {
        return castHandler;
    }

    public TrackMetadataSearcher getTrackMetadataSearcher() {
        return trackMetadataSearcher;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    public OkHttpClient.Builder newHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectionPool(connectionPool);

        if (testsInterceptor != null) {
            builder.addInterceptor(testsInterceptor);
        }

        if (!setCurrentOkHttpProxy(builder)) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.ignore_proxy_settings_invalid), Toast.LENGTH_SHORT);
            toast.show();
        }
        return com.lakshitasuman.musicstation.radio.Utils.enableTls12OnPreLollipop(builder);
    }

    public OkHttpClient.Builder newHttpClientWithoutProxy() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectionPool(connectionPool);

        if (testsInterceptor != null) {
            builder.addInterceptor(testsInterceptor);
        }

        return com.lakshitasuman.musicstation.radio.Utils.enableTls12OnPreLollipop(builder);
    }

    public boolean setCurrentOkHttpProxy(@NonNull OkHttpClient.Builder builder) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        ProxySettings proxySettings = ProxySettings.fromPreferences(sharedPref);
        if (proxySettings != null) {
            if (!com.lakshitasuman.musicstation.radio.Utils.setOkHttpProxy(builder, proxySettings)) {
                // proxy settings are not valid
                return false;
            }
        }
        return true;
    }

    private OkHttpClient newHttpClientForPicasso() {
        File cache = new File(getCacheDir(), "picasso-cache");
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new UserAgentInterceptor("RadioDroid2/" + BuildConfig.VERSION_NAME))
                .cache(new Cache(cache, Integer.MAX_VALUE));

        if (testsInterceptor != null) {
            builder.addInterceptor(testsInterceptor);
        }

        setCurrentOkHttpProxy(builder);

        return builder.build();
    }
}
