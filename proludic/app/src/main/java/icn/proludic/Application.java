package icn.proludic;

/*
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import icn.proludic.misc.Constants;
import icn.proludic.misc.SharedPreferencesManager;

import static icn.proludic.misc.Constants.FIRST_RUN;
import static icn.proludic.misc.Constants.NORMAL_RUN;
import static icn.proludic.misc.Constants.UPGRADED_RUN;

public class Application extends android.app.Application {

    private Context mContext = this;

    @Override
    public void onCreate() {
        super.onCreate();
        initSDKs();
        generateFBKeyHash(mContext);
        String FLURRY_KEY = "CRSGCS4XG4YN9KJV8H56";
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withListener(new FlurryAgentListener() {
                    @Override
                    public void onSessionStarted() {
                        // Capture author info & user status
                    }
                })
                .build(this, FLURRY_KEY);
    }

    private void initSDKs() {
        if (Locale.getDefault().getLanguage().equals("fr")) {
            SharedPreferencesManager.setString(mContext, "locale", "fr");
        } else {
            SharedPreferencesManager.setString(mContext, "locale", "en");
        }

        Parse.initialize(new Parse.Configuration.Builder(mContext)
                .applicationId(Constants.SASHIDO_APP_ID)
                .clientKey(Constants.SASHIDO_CLIENT_ID)
                .server(Constants.SASHIDO_SERVER_ID)
                .build()
        );

        ParsePush.subscribeInBackground("Proludic", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("debug", "subscribed to parse!");
            }
        });
        ParseFacebookUtils.initialize(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "icn.proludic",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /**
     * Author: StackOverflow User: Suragch
     * Url: http://stackoverflow.com/questions/7217578/check-if-application-is-on-its-first-run
     *
     * @param mContext passes the Context from the Activity.
     * @return returns the type of run based on the phones sharedPrefs
     */
    public static String checkFirstRun(Context mContext) {

        String type = null;
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // This is just a normal run
            type = NORMAL_RUN;
        } else if (savedVersionCode == DOESNT_EXIST) {
            // This is a new install (or the user cleared the shared preferences)
            type = FIRST_RUN;
        } else if (currentVersionCode > savedVersionCode) {
            // This is an upgrade
            type = UPGRADED_RUN;
        }
        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return type;
    }

    public static int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static void generateFBKeyHash(Context mContext) {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(
                    "icn.proludic",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("fb key hash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.e("failed", e.getMessage());
        }
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics realMetrics = new DisplayMetrics();
        return realMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics realMetrics = new DisplayMetrics();
        return realMetrics.heightPixels;
    }
}