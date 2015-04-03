package ru.egor_d.vircitiesbot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/**
 * Created by Egor Danilin on 25.03.2015.
 */
public class Utils {
    private Context context;
    public static int BROADCAST_REQUEST_CODE = 0x01;

    public static int SECOND = 1000;
    public static int MINUTE = 60 * SECOND;
    public static int HOUR = 60 * MINUTE;
    public static int DAY = 24 * HOUR;
    public static int WEEK = 7 * DAY;

    public static String API = "http://api.vircities.com";

    public Utils(Context context) {
        this.context = context;
    }

    public String loadLogin() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("login", "");
    }

    public void saveLogin(String s) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("login", s).commit();
    }

    public String loadPassword() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("password", "");
    }

    public void savePassword(String s) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("password", s).commit();
    }

    public boolean loadStarted() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("started", false);
    }

    public void saveStarted(boolean b) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("started", b).commit();
    }

    public void registerAlarm(Context context, int interval) {
        Intent i = new Intent(context, MoneyBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, Utils.BROADCAST_REQUEST_CODE, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), interval, sender);
    }

    public void unregisterAlarm(Context context) {
        Intent i = new Intent(context, MoneyBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, Utils.BROADCAST_REQUEST_CODE, i, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }
}
