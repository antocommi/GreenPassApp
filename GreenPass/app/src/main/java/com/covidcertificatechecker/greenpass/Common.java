package com.covidcertificatechecker.greenpass;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

public class Common {

    private static final String MY_PREFS_NAME = "myPrefs";
    public static ProgressDialog pd;


    public static void saveValues(Context context, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getValues(Context context, String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(key, "-");
        return name;
    }
}
