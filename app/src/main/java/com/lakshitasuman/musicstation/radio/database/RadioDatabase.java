package com.lakshitasuman.musicstation.radio.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lakshitasuman.musicstation.radio.history.TrackHistoryDao;
import com.lakshitasuman.musicstation.radio.history.TrackHistoryEntry;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.lakshitasuman.musicstation.radio.history.TrackHistoryEntry.MAX_UNKNOWN_TRACK_DURATION;

@Database(entities = {TrackHistoryEntry.class}, version = 1 ,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class RadioDatabase extends RoomDatabase {
    public abstract TrackHistoryDao songHistoryDao();

    private static volatile RadioDatabase INSTANCE;

    private Executor queryExecutor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "RadioDroidDatabase Executor"));

    public static RadioDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RadioDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RadioDatabase.class, "radio_droid_database")
                            .addCallback(CALLBACK)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public Executor getQueryExecutor() {
        return queryExecutor;
    }

    private static RoomDatabase.Callback CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            INSTANCE.queryExecutor.execute(() -> {
                // App may have been terminated without notice so we should set last track history entry's
                // end time to something reasonable.
                INSTANCE.songHistoryDao().setLastHistoryItemEndTimeRelative(MAX_UNKNOWN_TRACK_DURATION);
            });
        }
    };

}
