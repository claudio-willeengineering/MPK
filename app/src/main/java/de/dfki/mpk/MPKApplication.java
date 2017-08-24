package de.dfki.mpk;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Olakunmi on 06/08/2017.
 */

public class MPKApplication extends android.app.Application {
     private static final String FIRST_RUN  = "first_run";
    private static final String EMAIL  = "email";

    SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }



    public boolean isFirstRun()
    {
        return preferences.getBoolean(FIRST_RUN,true);
    }

    public void setHasRunOnce()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_RUN,false);
        editor.apply();
        editor.commit();
    }

    public  void setEmail(String email)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMAIL,email);
        editor.apply();
        editor.commit();
    }
}
