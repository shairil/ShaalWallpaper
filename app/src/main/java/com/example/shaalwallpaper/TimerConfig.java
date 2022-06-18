package com.example.shaalwallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TimerConfig {

    private static final String MY_PREFERENCE_NAME = "Timer";
    public static final String PREF_TOTAL_KEY = "time";

    public static void saveTotalInPref(Context context, String total) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOTAL_KEY, total);
        editor.apply();
        Log.d("CollectionTAG", "saveTotalInPref: " + total);
    }

    public static String loadTotalFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getString(PREF_TOTAL_KEY, "15 min");
    }

    public static void removeDataFromPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_TOTAL_KEY);
        editor.apply();
    }

    public static void registerPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPref(Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        pref.unregisterOnSharedPreferenceChangeListener(listener);
    }

}