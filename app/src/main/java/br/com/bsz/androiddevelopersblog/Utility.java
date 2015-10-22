package br.com.bsz.androiddevelopersblog;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by diegobezerrasouza on 21/04/15.
 */
public class Utility {

    private final static String FIRST_TIME_PREF_KEY = "first_time_pref_key";
    private final static String FIRST_TIME_PREF_VAL = "first_time_pref_val";

    public static boolean isFirstTimeApp(Context context) {
        boolean firstTime = true;
        SharedPreferences preferences = context.getSharedPreferences(FIRST_TIME_PREF_KEY, Context.MODE_PRIVATE);
        if (preferences.contains(FIRST_TIME_PREF_VAL)) {
            firstTime = preferences.getBoolean(FIRST_TIME_PREF_VAL, false);
        } else {
            preferences.edit().putBoolean(FIRST_TIME_PREF_VAL, false).apply();
        }
        Log.i("teste", "firstTime = " + String.valueOf(firstTime));
        return firstTime;
    }

    public static String getAmountDisplayArticlesPreference(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getResources().getString(R.string.amount_articles_pref_key), "25");
    }

    public static boolean getNotificationPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getResources().getString(R.string.notification_pref_key), true);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
