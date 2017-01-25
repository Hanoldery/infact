package com.hanoldery.pierre.scaneat.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hanoldery.pierre.scaneat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Pierre on 24/01/2017.
 */

public class inFactHelper {

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 600;

    public static String formatDate(Date date) {

        final long MINUTE_IN_SECONDS = 60;
        final long HOUR_IN_SECONDS = MINUTE_IN_SECONDS * 60;
        final long DAY_IN_SECONDS = HOUR_IN_SECONDS * 24;
        final long MONTH_IN_SECONDS = DAY_IN_SECONDS * 30;

        String result = "";

        long timeStamp = (System.currentTimeMillis() - date.getTime()) / 1000;
        timeStamp -= HOUR_IN_SECONDS;

        if (timeStamp < MONTH_IN_SECONDS) {
            if (timeStamp < DAY_IN_SECONDS) {

                if (timeStamp < HOUR_IN_SECONDS) {

                    if (timeStamp < MINUTE_IN_SECONDS) {
                        result += "Now";
                    } else {
                        result += String.valueOf(timeStamp / MINUTE_IN_SECONDS) + " minute(s)";
                    }

                } else {
                    result += String.valueOf(timeStamp / HOUR_IN_SECONDS) + " hour(s)";
                }
            } else {
                result += String.valueOf(timeStamp / DAY_IN_SECONDS) + " day(s)";
            }
        } else {
            result += String.valueOf(timeStamp / MONTH_IN_SECONDS) + " month(s)";
        }


        return result;

    }

    public static int getScreenWidth(Activity context) {

        Point p = new Point();
        Display display = context.getWindowManager().getDefaultDisplay();

        display.getSize(p);
        return p.x == 0 ? DEFAULT_WIDTH : p.x;
    }

    public static int getScreenHeight(Activity context) {

        Point p = new Point();
        Display display = context.getWindowManager().getDefaultDisplay();

        display.getSize(p);
        return p.y == 0 ? DEFAULT_HEIGHT : p.y;
    }

    public static float getValueInPX(float valueDP, Context context) {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueDP, context.getResources().getDisplayMetrics());
    }

    public static String formatDistance(double distance) {

        if (distance > 0 && distance < Float.MAX_VALUE) {

            if (distance < 1000.f) {
                return String.format("%.01f m", (distance));
            } else {
                return String.format("%.0f km", (distance * 0.001));
            }

        } else {
            return null;
        }
    }

    public static void handleErrorJSON(Context context, String resp) {

        if(resp == null) return;

        Log.d("handleErrorJSON", "resp : " + resp);
        try {
            JSONObject result = new JSONObject(resp);

            String title = "Erreur";

            String message = "";
            if (result.has("errors")) {
                JSONArray errorsJSON = result.getJSONArray("errors");
                for (int i = 0; i < errorsJSON.length(); i++) {
                    message += errorsJSON.getJSONObject(i).getString("message");
                    if (i < errorsJSON.length() - 1) message += "\n";
                }
            }

            if (message.length() == 0) {
                if (result.has("error")) title = result.getString("error");
            }

            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject getRespWithControls(Context context, String resp) {

        Log.d("getRespWithControls", resp);
        JSONObject result = null;
        try {
            result = new JSONObject(resp);

            if (inFactHelper.isJsonValid(result)) {
                return result;
            } else {
                inFactHelper.handleJsonError(context, result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static boolean isJsonValid(JSONObject resp) {
        return resp.optJSONObject("error")==null;
    }

    public static void handleJsonError(Context context, JSONObject resp) {

        try {
            new AlertDialog.Builder(context)
                    .setMessage(resp.getString("error"))
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getDeviceId(Context context) {
        final String PREF_DEVICE_ID = "pref_device_id";
        final String DEVICE_ID_STRING = "device_id";
        final String ANDROID_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        /*
         * Bug connu : L'ID 9774d56d682e549c est renvoyé pour beaucoup
		 * d'appareils
		 */
        if (ANDROID_ID != null && (!"".equals(ANDROID_ID))
                && (!"9774d56d682e549c".equals(ANDROID_ID))) {
            return ANDROID_ID;
        }
        // Log.d("noo", "Bad ANDROID_ID");
        SharedPreferences preferences = context.getSharedPreferences(PREF_DEVICE_ID,
                Context.MODE_PRIVATE);
        String device_id = preferences.getString(DEVICE_ID_STRING, null);
        if (device_id == null) {
            device_id = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(DEVICE_ID_STRING, device_id);
            editor.commit();
        }
        return device_id;
    }

    public static String getDeviceModel() {
        String deviceModel = android.os.Build.MANUFACTURER + android.os.Build.PRODUCT;
        return deviceModel;
    }

    public static String getDeviceName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        if (myDevice != null) {
            String deviceName = myDevice.getName();
            return deviceName;
        } else {
            return "defaultname";
        }
    }

    public static String getAppVersion(Context c) {
        PackageInfo pInfo = null;
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        String appVersion = pInfo.versionName;

        return appVersion;
    }

    public static String getOSVersion() {
        String osVersion = android.os.Build.VERSION.RELEASE;
        return osVersion;
    }

    public static String getDeviceLanguage() {
        return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] bitmapdata=out.toByteArray();

        return bitmapdata;
    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
}
