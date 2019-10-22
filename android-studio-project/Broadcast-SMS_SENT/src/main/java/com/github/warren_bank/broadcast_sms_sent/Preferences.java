package com.github.warren_bank.broadcast_sms_sent;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences {
    private static final String PREFS_FILENAME   = "PREFS";
    private static final String PREF_LAST_SMS_ID = "LAST_SMS_ID";

    public static int getLastSmsId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PREF_LAST_SMS_ID, -1);
    }

    public static void setLastSmsId(Context context, int lastSmsId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor = sharedPreferences.edit();
        prefs_editor.putInt(PREF_LAST_SMS_ID, lastSmsId);
        prefs_editor.apply();
    }
}
