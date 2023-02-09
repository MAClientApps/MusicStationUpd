package com.lakshitasuman.musicstation.ringtone.utils;

import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lakshitasuman.musicstation.R;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MyUtils {
    public  static File NameLocation=new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_MUSIC + File.separator +"All in One Audio"+File.separator+"Name Ringtone");
    public  static File CutAudioLocation=new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_MUSIC + File.separator +"All in One Audio"+File.separator+"Cut Audio Ringtone");
    public  static File ChangeVoiceLocation=new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_MUSIC + File.separator +"All in One Audio"+File.separator+"Voice Recorder");
    static Context cn;
    public static SharedPreferences.Editor editor;
    public static int isFromScreen;
    public static SharedPreferences pref;

    public MyUtils(Context context) {
        cn = context;
        pref = context.getSharedPreferences(context.getPackageName(), 0);
        editor = pref.edit();
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());
    }

    public static Bitmap getBitmapwithAlpha(Bitmap bitmap, int i) {
        if (!bitmap.isMutable()) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        new Canvas(bitmap).drawColor((i & 255) << 24, PorterDuff.Mode.DST_IN);
        return bitmap;
    }

    public static Bitmap gettextbitmap(String str, int i, Typeface typeface, int i2, int i3, int i4) {
        TextPaint textPaint = new TextPaint(65);
        textPaint.setStyle(Paint.Style.FILL);
        if (typeface != null) {
            textPaint.setTypeface(typeface);
        }
        textPaint.setColor(i);
        textPaint.setTextSize(80.0f);
        if (i2 >= 0 && i3 >= 0) {
            textPaint.setShadowLayer((float) i4, (float) i2, (float) i3, Color.parseColor("#000000"));
        }
        int measureText = (int) textPaint.measureText(str);
        StaticLayout staticLayout = new StaticLayout(str, textPaint, measureText, Layout.Alignment.ALIGN_CENTER, 1.0f, 4.0f, true);
        Bitmap createBitmap = Bitmap.createBitmap(measureText, staticLayout.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(65);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0);
        canvas.drawPaint(paint);
        canvas.save();
        canvas.translate(0.0f, 0.0f);
        staticLayout.draw(canvas);
        canvas.restore();
        return createBitmap;
    }

    public static void openVideo(Context context, String str) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(str), "video/*");
            context.startActivity(intent);
        } catch (Exception exceptionxception) {
            Toast(context, "can't play from here");
        }
    }

    public static void openFile(Context context, String str) {
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(str)), singleton.getMimeTypeFromExtension(str.substring(str.lastIndexOf(".") + 1, str.length())));
        intent.setFlags(FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException exceptionxception) {
            Toast(context, "No handler for this type of file.");
        }
    }

    public static int getgridEqualSpace(Context context, int i, int i2) {
        int i3 = context.getResources().getDisplayMetrics().widthPixels;
        return (i3 - (((i * i3) / 1080) * i2)) / (i2 + 1);
    }

    public static Bitmap getBitmapFromView(FrameLayout frameLayout) {
        try {
            frameLayout.setDrawingCacheEnabled(true);
            frameLayout.buildDrawingCache(true);
            Bitmap createBitmap = Bitmap.createBitmap(frameLayout.getDrawingCache());
            frameLayout.setDrawingCacheEnabled(false);
            return createBitmap;
        } catch (Exception exceptionxception) {
            return null;
        }
    }

    public static Bitmap getBitmapFromText(String str, float f, int i, Typeface typeface) {
        Paint paint = new Paint(1);
        paint.setTextSize(f);
        paint.setColor(i);
        if (typeface != null) {
            paint.setTypeface(typeface);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        float f2 = -paint.ascent();
        Bitmap createBitmap = Bitmap.createBitmap((int) (paint.measureText(str) + 0.5f), (int) (paint.descent() + f2 + 0.5f), Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawText(str, 0.0f, f2, paint);
        return createBitmap;
    }

    public static Bitmap getBitmapFromText(String str, float f, int i, Typeface typeface, int i2) {
        Paint paint = new Paint(1);
        paint.setTextSize(f);
        paint.setColor(i);
        if (typeface != null) {
            paint.setTypeface(typeface);
        }
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setShadowLayer(12.0f, 0.0f, 0.0f, i2);
        float f2 = -paint.ascent();
        Bitmap createBitmap = Bitmap.createBitmap((int) (paint.measureText(str) + 0.5f), (int) (paint.descent() + f2 + 0.5f), Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawText(str, 0.0f, f2, paint);
        return createBitmap;
    }

    public static String getmilliSecondsToTimer(long j) {
        String str;
        String str2;
        int i = (int) (j / 3600000);
        long j2 = j % 3600000;
        int i2 = ((int) j2) / TimeUtils.MilliSeconds.ONE_MINUTE;
        int i3 = (int) ((j2 % 60000) / 1000);
        if (i > 0) {
            str = i + ":";
        } else {
            str = "";
        }
        if (i3 < 10) {
            str2 = "0" + i3;
        } else {
            str2 = "" + i3;
        }
        return str + i2 + ":" + str2;
    }

    public static boolean isAccessibilitySettingsOn(Context context) {
        int i;
        String string;
        try {
            i = Settings.Secure.getInt(context.getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException exceptionxception) {
            i = 0;
        }
        if (i != 1 || (string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services")) == null) {
            return false;
        }
        return string.toLowerCase().contains(context.getPackageName().toLowerCase());
    }

    public static void getPermissionAccessibilty(Activity activity) {
        new Intent().setFlags(FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
        activity.startActivityForResult(new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 100);
    }

    public static Bitmap getBitmapfromPath(String str, View view) {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(new File(str).getAbsolutePath(), new BitmapFactory.Options()), view.getWidth(), view.getHeight(), true);
    }

    public static Bitmap getBitmapfromPath(String str, int i, int i2) {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(new File(str).getAbsolutePath(), new BitmapFactory.Options()), i, i2, true);
    }

    public static void pickImagefromGallery(Activity activity, int i) {
        Intent intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, i);
    }

    public static void pickVideofromGallery(Activity activity, int i) {
        Intent intent = new Intent("android.intent.action.PICK", MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        activity.startActivityForResult(intent, i);
    }

    public static void pickFilefromType(Activity activity, int i, String str) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType(str);
        activity.startActivityForResult(Intent.createChooser(intent, "ChooseFile"), i);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        inputMethodManager.showSoftInputFromInputMethod(currentFocus.getWindowToken(), 0);
    }

    public static Boolean checkoverlay(Activity activity, int i) {
        Context applicationContext = activity.getApplicationContext();
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(applicationContext)) {
            return true;
        }
        activity.startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + applicationContext.getPackageName())), i);
        return false;
    }

    public static boolean checkSystemWritePermission(Context context, int i) {
        boolean canWrite = Build.VERSION.SDK_INT >= 23 ? Settings.System.canWrite(context) : true;
        if (!canWrite) {
            permissionSystemSettings((Activity) context, i);
        }
        return canWrite;
    }

    public static void permissionSystemSettings(Activity activity, int i) {
        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, i);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    public static void addContact(Context context, String str, String str2, Uri uri) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue("account_type", (Object) null).withValue("account_name", (Object) null).build());
        if (str != null) {
            arrayList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data1", str).build());
        }
        if (str2 != null) {
            arrayList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/phone_v2").withValue("data1", str2).withValue("data2", 2).build());
        }
        if (uri != null) {
            try {
                arrayList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/photo").withValue("data15", getBytes(context.getContentResolver().openInputStream(uri))).build());
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception2) {
               exception2.printStackTrace();
            }
        }
        try {
            context.getContentResolver().applyBatch("com.android.contacts", arrayList);
        } catch (Exception exception3) {
            exception3.printStackTrace();
            Toast.makeText(context, "Exception: " + exception3.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void makeCall(Context context, String str) {
        Intent intent = new Intent("android.intent.action.CALL");
        intent.setData(Uri.parse("tel:" + str));
        intent.setFlags(FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
        context.startActivity(intent);
    }

    public static void makeSms(Context context, String str) {
        context.startActivity(new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, (String) null)));
    }

    public static String saveBitmap(Context context, Bitmap bitmap, String str) {
        try {
            String file = Environment.getExternalStorageDirectory().toString();
            Integer.valueOf(0);
            File file2 = new File(file, context.getString(R.string.app_name));
            if (!file2.exists()) {
                file2.mkdir();
            }
            String str2 = gettimestring(System.currentTimeMillis(), "dd_MM_yy_ss");
            File file3 = new File(file, context.getString(R.string.app_name) + "/" + str + str2 + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file3);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast(context, "Saved Successfully");
            String absolutePath = file3.getAbsolutePath();
            MediaScannerConnection.scanFile(context, new String[]{file3.getPath()}, new String[]{"image/png"}, (MediaScannerConnection.OnScanCompletedListener) null);
            return absolutePath;
        } catch (Exception exceptionxception) {
            Toast(context, "Failed to Save");
            return null;
        }
    }

    public static String saveBitmappath(Context context, Bitmap bitmap, String str) {
        try {
            Environment.getExternalStorageDirectory().toString();
            Integer.valueOf(0);
            File filesDir = context.getFilesDir();
            if (!filesDir.exists()) {
                filesDir.mkdir();
            }
            File file = new File(filesDir, str + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file.getAbsolutePath();
        } catch (Exception exceptionxception) {
            Toast(context, "Failed to Save");
            return null;
        }
    }

    public static ArrayList<Bitmap> getImageFromAsset(Context context, String str) {
        ArrayList<Bitmap> arrayList = new ArrayList<>();
        String[] strArr = new String[0];
        try {
            strArr = context.getAssets().list(str);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        for (int i = 0; i < Arrays.asList(strArr).size(); i++) {
            arrayList.add(getBitmapFromAsset(context, str + "/" + ((String) Arrays.asList(strArr).get(i))));
        }
        return arrayList;
    }

    public static Bitmap getColorBit(String str) {
        int parseColor = Color.parseColor(str);
        Bitmap createBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawColor(parseColor);
        return createBitmap;
    }

    public static Bitmap getBitmapFromAsset(Context context, String str) {
        try {
            return BitmapFactory.decodeStream(context.getAssets().open(str));
        } catch (IOException exceptionxception) {
            Log.e("", "");
            return null;
        }
    }

    public static Bitmap bitmapOverlay(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(bitmap, new Matrix(), (Paint) null);
        canvas.drawBitmap(bitmap2, (float) (i - (bitmap2.getWidth() / 2)), (float) (i2 - (bitmap2.getHeight() / 2)), (Paint) null);
        return createBitmap;
    }

    public static Bitmap getRounndCroppedBitmap(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        try {
            return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", (String) null));
        } catch (Exception exception) {
            Log.e("AAA", exception.toString());
            return null;
        }
    }

    public static Bitmap getBitmapResize(Context context, Bitmap bitmap, int i, int i2) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width >= height) {
            int i3 = (height * i) / width;
            if (i3 > i2) {
                i = (i * i2) / i3;
            } else {
                i2 = i3;
            }
        } else {
            int i4 = (width * i2) / height;
            if (i4 > i) {
                i2 = (i2 * i) / i4;
            } else {
                i = i4;
            }
        }
        return Bitmap.createScaledBitmap(bitmap, i, i2, true);
    }

    public static boolean checknotificationaccess(Context context) {
        boolean isNotification;
        try {
            isNotification = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners").contains(context.getPackageName());
        } catch (Exception exceptionxception) {
            isNotification = false;
        }
        if (!isNotification) {
            String string = context.getString(R.string.app_name);
            Toast(context, string + " Select and Give Permission", 1);
            context.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
        }
        return isNotification;
    }

    @SuppressLint("Range")
    public static String getContactName(Context context, String str) {
        Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(str)), new String[]{"display_name"}, (String) null, (String[]) null, (String) null);
        if (query == null) {
            return str;
        }
        if (query.moveToFirst()) {
            str = query.getString(query.getColumnIndex("display_name"));
        }
        if (query != null && !query.isClosed()) {
            query.close();
        }
        return str;
    }

    public static boolean generatetxt(Context context, String str, String str2, String str3) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileWriter fileWriter = new FileWriter(new File(file, str2));
            fileWriter.append(str3);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void sharetextonWhatsApp(Context context, String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");
        intent.putExtra("android.intent.extra.TEXT", str);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException exceptionxception) {
            Toast(context, "Whatsapp have not been installed.");
        }
    }

    public static void shareImage(Context context, String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + str));
        context.startActivity(Intent.createChooser(intent, "Share Image"));
    }

    public static void shareMyMp4(Context context, File file) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/mp4");
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
            context.startActivity(Intent.createChooser(intent, "Share Video"));
        } catch (Exception exception) {
            Log.e("Err", exception.toString());
        }
    }

    public static void shareMyMp3(Context context, File file) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("music/mp3");
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
            context.startActivity(Intent.createChooser(intent, "Share Mp3"));
        } catch (Exception exception) {
            Log.e("Err", exception.toString());
        }
    }

    public static void shareAnyFile(Context context, String str) {
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        Intent intent = new Intent("android.intent.action.SEND");
        String substring = str.substring(str.lastIndexOf(".") + 1, str.length());
        String mimeTypeFromExtension = singleton.getMimeTypeFromExtension(substring);
        if (substring.equalsIgnoreCase("pdf")) {
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(str)));
            intent.setType(mimeTypeFromExtension);
        } else if (substring.equalsIgnoreCase("png") || substring.equalsIgnoreCase("jpg")) {
            intent.setType(mimeTypeFromExtension);
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(str)));
            intent.setFlags(FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
            context.startActivity(Intent.createChooser(intent, "Share Image"));
            return;
        } else {
            intent.setDataAndType(Uri.fromFile(new File(str)), mimeTypeFromExtension);
        }
        intent.setFlags(FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException exceptionxception) {
            Toast(context, "No handler for this type of file.");
        }
    }

    public static void share(Context context) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + context.getPackageName());
        context.startActivity(Intent.createChooser(intent, "Share via"));
    }

    public static void rate(Context context) {
        try {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + context.getPackageName())));
        } catch (ActivityNotFoundException exceptionxception) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static RelativeLayout.LayoutParams getParamsR(Context context, int i, int i2) {
        return new RelativeLayout.LayoutParams((context.getResources().getDisplayMetrics().widthPixels * i) / 1080, (context.getResources().getDisplayMetrics().heightPixels * i2) / 1920);
    }

    public static LinearLayout.LayoutParams getParamsL(Context context, int i, int i2) {
        return new LinearLayout.LayoutParams((context.getResources().getDisplayMetrics().widthPixels * i) / 1080, (context.getResources().getDisplayMetrics().heightPixels * i2) / 1920);
    }

    public static LinearLayout.LayoutParams getParamsLSquare(Context context, int i) {
        return new LinearLayout.LayoutParams((context.getResources().getDisplayMetrics().widthPixels * i) / 1080, (context.getResources().getDisplayMetrics().widthPixels * i) / 1080);
    }

    public static LinearLayout.LayoutParams getParamsLSquareH(Context context, int i) {
        return new LinearLayout.LayoutParams((context.getResources().getDisplayMetrics().heightPixels * i) / 1920, (context.getResources().getDisplayMetrics().heightPixels * i) / 1920);
    }

    public static RelativeLayout.LayoutParams getParamsRSquare(Context context, int i) {
        return new RelativeLayout.LayoutParams((context.getResources().getDisplayMetrics().widthPixels * i) / 1080, (context.getResources().getDisplayMetrics().widthPixels * i) / 1080);
    }

    public static void copyClipboard(Context context, String str) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("copy", str));
        Toast(context, "Copy Message...");
    }

    public static String getNumbers(String str) {
        return str.replaceAll("[^0-9]", "");
    }

    public static Bitmap getMask(Context context, Bitmap bitmap, int i, View view) {
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i);
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        Bitmap createScaledBitmap2 = Bitmap.createScaledBitmap(decodeResource, width, height, true);
        Bitmap createBitmap = Bitmap.createBitmap(createScaledBitmap2.getWidth(), createScaledBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(createScaledBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(createScaledBitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode((Xfermode) null);
        return createBitmap;
    }

    public static Bitmap getMask(Context context, Bitmap bitmap, Bitmap bitmap2, View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        Bitmap createScaledBitmap2 = Bitmap.createScaledBitmap(bitmap2, width, height, true);
        Bitmap createBitmap = Bitmap.createBitmap(createScaledBitmap2.getWidth(), createScaledBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(createScaledBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(createScaledBitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode((Xfermode) null);
        return createBitmap;
    }

    public static Bitmap getMask(Context context, Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, i, i2, true);
        Bitmap createScaledBitmap2 = Bitmap.createScaledBitmap(bitmap2, i, i2, true);
        Bitmap createBitmap = Bitmap.createBitmap(createScaledBitmap2.getWidth(), createScaledBitmap2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(createScaledBitmap, 0.0f, 0.0f, (Paint) null);
        canvas.drawBitmap(createScaledBitmap2, 0.0f, 0.0f, paint);
        paint.setXfermode((Xfermode) null);
        return createBitmap;
    }

    public static String downloadFile(String str, String str2) {
        try {
            URL url = new URL(str);
            int contentLength = url.openConnection().getContentLength();
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            byte[] bArr = new byte[contentLength];
            dataInputStream.readFully(bArr);
            dataInputStream.close();
            File file = new File(Environment.getExternalStorageDirectory() + "/" + str2);
            if (!file.exists()) {
                file.mkdir();
            }
            File file2 = new File(file, "vdo_" + System.currentTimeMillis());
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file2));
            dataOutputStream.write(bArr);
            dataOutputStream.flush();
            dataOutputStream.close();
            return file2.getAbsolutePath();
        } catch (IOException exceptionxception) {
            return null;
        }
    }

    public static void copyRAWtoSDCard(Context context, String str, int i) throws IOException {
        String str2 = Environment.getExternalStorageDirectory() + "/" + str;
        File file = new File(str2);
        if (!file.exists()) {
            file.mkdir();
        }
        File file2 = new File(str2 + "/vdo_" + System.currentTimeMillis() + ".mp4");
        InputStream openRawResource = context.getResources().openRawResource(i);
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                int read = openRawResource.read(bArr);
                if (read > 0) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    openRawResource.close();
                    fileOutputStream.close();
                    return;
                }
            } finally {
                openRawResource.close();
                fileOutputStream.close();
            }
        }
    }

    public static String copyRAWtoSDCard(Context context, String str, String str2, String str3) throws IOException {
        String str4 = Environment.getExternalStorageDirectory() + "/" + str;
        File file = new File(str4);
        if (!file.exists()) {
            file.mkdir();
        }
        String str5 = str4 + "/" + str2 + ".mp4";
        File file2 = new File(str5);
        InputStream openRawResource = context.getResources().openRawResource(Integer.parseInt(str3.substring(str3.lastIndexOf("/") + 1, str3.length())));
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                int read = openRawResource.read(bArr);
                if (read <= 0) {
                    openRawResource.close();
                    fileOutputStream.close();
                    return str5;
                }
                fileOutputStream.write(bArr, 0, read);
            } finally {
                openRawResource.close();
                fileOutputStream.close();
            }
        }
    }


    public static void copyFileUsingStream(File r3, String r4, String r5) throws IOException {

          }

    public static ArrayList<File> getListFiles(String str, String str2) {
        ArrayList<File> arrayList = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory() + "/" + str);
        if (!file.exists()) {
            file.mkdir();
        }
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    arrayList.addAll(getListFiles(file2));
                } else {
                    String absolutePath = file2.getAbsolutePath();
                    if (absolutePath.substring(absolutePath.lastIndexOf(".") + 1, absolutePath.length()).equalsIgnoreCase(str2)) {
                        arrayList.add(file2);
                    }
                }
            }
        }
        return arrayList;
    }

    public static ArrayList<File> getListFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    arrayList.addAll(getListFiles(file2));
                } else {
                    arrayList.add(file2);
                }
            }
        }
        return arrayList;
    }

    public static ArrayList<String> getListFiles(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File(str);
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (file2.isDirectory()) {
                    arrayList.addAll(getListFiles(file2.getPath()));
                } else {
                    arrayList.add(file2.getPath());
                }
            }
        }
        return arrayList;
    }

    public static ArrayList<String> getFolderfiles(String str) {
        ArrayList<String> arrayList = new ArrayList<>();
        File file = new File(str);
        if (file.exists()) {
            for (File file2 : file.listFiles()) {
                if (!file2.isDirectory()) {
                    arrayList.add(file2.getPath());
                }
            }
        }
        return arrayList;
    }

    public static String getFileExtension(String str) {
        String name = new File(str).getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception exceptionxception) {
            return "";
        }
    }

    public static String getMimeType(String str) {
        String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(str);
        if (fileExtensionFromUrl != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
        }
        return null;
    }

    public static boolean checkPermission(Context context, String[] strArr) {
        int length = strArr.length;
        boolean isNotification = true;
        for (int i = 0; i < length; i++) {
            isNotification = ActivityCompat.checkSelfPermission(context, strArr[i]) == 0;
            if (!isNotification) {
                break;
            }
        }
        if (!isNotification) {
            ActivityCompat.requestPermissions((Activity) context, strArr, 101);
        }
        return isNotification;
    }

    public static void ToastImage(Context context, Bitmap bitmap) {
        Toast toast = new Toast(context);
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        toast.setView(imageView);
        toast.show();
    }

    public static void Toast(Context context, String str) {
        Toast(context, str, 0);
    }

    public static void Toast(Context context, String str, int i) {
        Toast.makeText(context, str, i).show();
    }

    public static long gettimeskip(int i) {
        Calendar instance = Calendar.getInstance();
        instance.getTimeInMillis();
        instance.add(12, i);
        instance.set(13, 0);
        return instance.getTimeInMillis();
    }

    public static long getskipday(int i) {
        Calendar instance = Calendar.getInstance();
        instance.getTimeInMillis();
        instance.add(5, i);
        instance.set(13, 0);
        return instance.getTimeInMillis();
    }

    public static String gettimestring(long j, String str) {
        return new SimpleDateFormat(str).format(Long.valueOf(j));
    }

    public static long getdatefromstring(String str, String str2) throws ParseException {
        return new SimpleDateFormat(str).parse(str2).getTime();
    }

    public static String getRealPathFromURI(Activity activity, Uri uri) {
        Cursor managedQuery = activity.managedQuery(uri, new String[]{"_data"}, (String) null, (String[]) null, (String) null);
        int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
        managedQuery.moveToFirst();
        return managedQuery.getString(columnIndexOrThrow);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!Boolean.valueOf(connectivityManager.getActiveNetworkInfo() != null).booleanValue()) {
            Toast(context, "Need Internet Connection");
        }
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public static Uri getBitmapUri(Context context, Bitmap bitmap) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", (String) null));
    }

    public static void setBrigtness(Context context, int i) {
        int i2 = (int) ((((float) i) * 255.0f) / 100.0f);
        ContentResolver contentResolver = context.getContentResolver();
        Window window = ((Activity) context).getWindow();
        Settings.System.putInt(contentResolver, "screen_brightness", i2);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = (float) i2;
        Log.e("AAA", "Brigtness : " + i2);
        window.setAttributes(attributes);
    }

    public static void setAutoBrigtness(Context context) {
        Settings.System.putInt(context.getContentResolver(), "screen_brightness_mode", 1);
    }

    public static int getBrigtness(Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Settings.System.putInt(contentResolver, "screen_brightness_mode", 0);
            return Settings.System.getInt(contentResolver, "screen_brightness");
        } catch (Settings.SettingNotFoundException exception) {
            Log.e("Error", "Cannot access system brightness");
            exception.printStackTrace();
            return -1;
        }
    }

    public static boolean hasWriteSettingsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.System.canWrite(context);
        }
        return ContextCompat.checkSelfPermission(context, "android.permission.WRITE_SETTINGS") == 0;
    }

    public static void getWriteSettingsPermission(Context context, int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            ((Activity) context).startActivityForResult(intent, i);
            return;
        }
        ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_SETTINGS"}, i);
    }

    public static void setAutoRotation(Context context, Boolean bool) {
        Settings.System.putInt(context.getContentResolver(), "accelerometer_rotation", bool.booleanValue() ? 1 : 0);
    }

    public static boolean getAutoRotationOnOff(Context context) {
        int i;
        try {
            i = Settings.System.getInt(context.getContentResolver(), "accelerometer_rotation");
        } catch (Settings.SettingNotFoundException exception) {
            exception.printStackTrace();
            i = 0;
        }
        if (i == 1) {
            return true;
        }
        return false;
    }

    public static Boolean hasGpsEnable(Context context) {
        return Boolean.valueOf(((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled("gps"));
    }

    public static void getGpsManager(Context context, int i) {
        ((Activity) context).startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), i);
    }

    public static boolean isAirplaneModeOn(Context context) {
        return Build.VERSION.SDK_INT < 17 ? Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0 : Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static void setFlightnMode(Context context, int i) {
        Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
        intent.setFlags(FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
        ((Activity) context).startActivityForResult(intent, i);
    }

    public static float getBatteryTemperature() {
        Intent batteryStatusIntent = getBatteryStatusIntent(cn);
        if (batteryStatusIntent == null) {
            return 0.0f;
        }
        double intExtra = (double) batteryStatusIntent.getIntExtra("temperature", 0);
        Double.isNaN(intExtra);
        return (float) (intExtra / 10.0d);
    }

    public static int getBatteryVoltage() {
        Intent batteryStatusIntent = getBatteryStatusIntent(cn);
        if (batteryStatusIntent != null) {
            return batteryStatusIntent.getIntExtra("voltage", 0);
        }
        return 0;
    }

    public static String getBatteryChargingSource() {
        int intExtra = getBatteryStatusIntent(cn).getIntExtra("plugged", 0);
        if (intExtra == 4) {
            return "WIRELESS";
        }
        if (intExtra != 1) {
            return intExtra != 2 ? "--" : "USB";
        }
        return "AC";
    }

    public static Intent getBatteryStatusIntent(Context context) {
        return context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public static Boolean getConnectedStats(Context context) {
        return Boolean.valueOf(context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.hardware.usb.action.USB_STATE")).getExtras().getBoolean("connected"));
    }

    public static void setScreenTimeout(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", i * 1000);
    }

    public static void setMobileDataEnabled(Context context, boolean isDataOff) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (isDataOff) {
            if (isMobileDataOnOff(context).booleanValue()) {
                return;
            }
        } else if (!isMobileDataOnOff(context).booleanValue()) {
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Field declaredField = Class.forName(connectivityManager.getClass().getName()).getDeclaredField("mService");
        declaredField.setAccessible(true);
        Object obj = declaredField.get(connectivityManager);
        Method declaredMethod = Class.forName(obj.getClass().getName()).getDeclaredMethod("setMobileDataEnabled", new Class[]{Boolean.TYPE});
        declaredMethod.setAccessible(true);
        declaredMethod.invoke(obj, new Object[]{Boolean.valueOf(isDataOff)});
    }

    public static Boolean isMobileDataOnOff(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bool = false;
        try {
            Method declaredMethod = Class.forName(connectivityManager.getClass().getName()).getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            declaredMethod.setAccessible(true);
            bool = ((Boolean) declaredMethod.invoke(connectivityManager, new Object[0])).booleanValue();
        } catch (Exception exceptionxception) {
        }
        return Boolean.valueOf(bool);
    }

    public static Bitmap getBitmapfromUri(Context context, Uri uri) {
        String[] strArr = {"_data"};
        Cursor query = context.getContentResolver().query(uri, strArr, (String) null, (String[]) null, (String) null);
        query.moveToFirst();
        @SuppressLint("Range") String string = query.getString(query.getColumnIndex(strArr[0]));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decodeFile = BitmapFactory.decodeFile(string, options);
        query.close();
        return decodeFile;
    }

    public static void deleteFile(String str) {
        File file = new File(str);
        if (file.exists()) {
            file.delete();
        }
    }

    public static String formatTimeUnit(long j) throws ParseException {
        return String.format("%02d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(j)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(j)))});
    }

    public static void launchApp(Context context, String str) {
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
        if (launchIntentForPackage != null) {
            context.startActivity(launchIntentForPackage);
        }
    }

    public static String getAppnameFromPckg(Context context, String str) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 0);
        } catch (Exception exceptionxception) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }

    public static int getCountfromString(String str, String str2) {
        int i = 0;
        while (true) {
            if (!Pattern.compile("[^" + str2 + "]*" + str2).matcher(str).find()) {
                return i;
            }
            i++;
        }
    }

    public static Boolean checkAppOverUsagePermission(Activity activity, int i) {
        Boolean valueOf = Boolean.valueOf(isAccessGranted(activity));
        if (valueOf.booleanValue()) {
            return valueOf;
        }
        try {
            Toast(activity, "Need Prmission");
            Intent intent = new Intent();
            intent.setAction("android.settings.USAGE_ACCESS_SETTINGS");
            activity.startActivityForResult(intent, i);
            return valueOf;
        } catch (Exception exceptionxception) {
            return false;
        }
    }

    public static boolean getstatusAppUsagePermission(Context context) {
        AppOpsManager appOpsManager;
        if (Build.VERSION.SDK_INT >= 21 && (appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE)) != null && appOpsManager.checkOpNoThrow("android:get_usage_stats", Process.myUid(), context.getPackageName()) == 0) {
            long currentTimeMillis = System.currentTimeMillis();
            List<UsageStats> queryUsageStats = ((UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE)).queryUsageStats(0, currentTimeMillis - 10000, currentTimeMillis);
            Log.e("AAA", "Count : " + queryUsageStats.size());
            Boolean valueOf = Boolean.valueOf(queryUsageStats != null && queryUsageStats.size() > 0);
            Log.e("AAA", "Permission : " + valueOf);
            if (queryUsageStats == null || queryUsageStats.size() <= 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isAccessGranted(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            if ((Build.VERSION.SDK_INT > 19 ? ((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE)).checkOpNoThrow("android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName) : 0) == 0) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException exceptionxception) {
        }
        return false;
    }

    public static void sortArray(ArrayList<File> arrayList) {
        Collections.sort(arrayList, new Comparator<File>() {
            public int compare(File file, File file2) {
                int i = ((file.lastModified() - file2.lastModified()) > 0 ? 1 : ((file.lastModified() - file2.lastModified()) == 0 ? 0 : -1));
                if (i > 0) {
                    return -1;
                }
                return i == 0 ? 0 : 1;
            }
        });
    }

    public static File makeBaseFolder(Context context) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory.getAbsolutePath() + "/" + context.getResources().getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static void setRingtone(Context context, String str) {
        String substring = str.substring(str.lastIndexOf("/") + 1);
        String substring2 = substring.substring(0, substring.lastIndexOf("."));
        ContentValues contentValues = new ContentValues();
        contentValues.put("_data", str);
        contentValues.put("title", substring2);
        contentValues.put("_size", Integer.valueOf(str.length()));
        contentValues.put("mime_type", "audio/mp3");
        contentValues.put("artist", context.getString(R.string.app_name));
        contentValues.put("is_ringtone", true);
        contentValues.put("is_notification", false);
        contentValues.put("is_alarm", false);
        contentValues.put("is_music", false);
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(str);
            contentResolver.delete(contentUriForPath, "_data=\"" + str + "\"", (String[]) null);
            Uri insert = contentResolver.insert(contentUriForPath, contentValues);
            Log.e("", "=====Enter ====" + insert);
            RingtoneManager.setActualDefaultRingtoneUri(context, 1, insert);
        } catch (Exception exception) {
            Log.e("", "Error" + exception.getMessage());
        }
    }

    public static String copyfromassets(Context context, String str, String str2) {
        try {
            InputStream open = context.getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(str2);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read != -1) {
                    fileOutputStream.write(bArr, 0, read);
                } else {
                    open.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    return str2;
                }
            }
        } catch (Exception exception) {
            Log.e("AAA", exception.getMessage());
            return "";
        }
    }

    public static float formatFloat(    float f) {
        return BigDecimal.valueOf((double) f).setScale(1, 4).floatValue();
    }
}
