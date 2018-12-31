package com.example.gs63.feedprototype.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static SharedPreferences.Editor editor;
    // Shared Preferences
    private static SharedPreferences pref;
    private static final String PREF_NAME = "UserData";
    private static int PRIVATE_MODE = 0;

    public static void setContext(Context context){
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SharedPreferences getData() {
        return pref;
    }

    public static void setUserId(String userId){
        editor.putString(KEY_USER_ID,userId).commit();
    }

    public static String getUserId(){
        return getData().getString(KEY_USER_ID,"");
    }
}
