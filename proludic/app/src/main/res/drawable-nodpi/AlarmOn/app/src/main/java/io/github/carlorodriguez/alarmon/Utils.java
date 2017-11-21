package io.github.carlorodriguez.alarmon;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static io.github.carlorodriguez.alarmon.Constants.FIRST_RUN;
import static io.github.carlorodriguez.alarmon.Constants.NORMAL_RUN;
import static io.github.carlorodriguez.alarmon.Constants.UPGRADED_RUN;

/**
 * Created by Bradley on 06/07/2017.
 */

public class Utils {

    public String checkFirstRun(Context context) {
        String type = "";
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            type = NORMAL_RUN;
        } else if (savedVersionCode == DOESNT_EXIST) {
            type = FIRST_RUN;
        } else if (currentVersionCode > savedVersionCode) {
            type = UPGRADED_RUN;
        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return type;
    }
}
