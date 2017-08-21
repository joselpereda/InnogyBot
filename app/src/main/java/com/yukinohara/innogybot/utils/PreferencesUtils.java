package com.yukinohara.innogybot.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by YukiNoHara on 6/14/2017.
 */

public class PreferencesUtils {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static void setUserId(Context context, String userId){
        sharedPreferences = context.getSharedPreferences(PreferencesKey.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PreferencesKey.PREF_USER_ID, userId);
        editor.apply();
    }

    public static String getUserId(Context context){
        sharedPreferences = context.getSharedPreferences(PreferencesKey.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PreferencesKey.PREF_USER_ID, null);
    }

    public static void setUsername(Context context, String name){
        sharedPreferences = context.getSharedPreferences(PreferencesKey.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PreferencesKey.PREF_USERNAME, name);
        editor.apply();
    }

    public static String getUsername(Context context){
        sharedPreferences = context.getSharedPreferences(PreferencesKey.PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PreferencesKey.PREF_USERNAME, null);
    }
}
