package id.ac.polinema.deteksipadiapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Application extends android.app.Application {
    private static SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }
}

