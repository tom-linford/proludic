package icn.proludic.misc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icn.proludic.DashboardActivity;
import icn.proludic.R;

import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static icn.proludic.misc.Constants.ACHIEVEMENTS_CLASS;
import static icn.proludic.misc.Constants.AFRICAN_ELEPHANT_COL;
import static icn.proludic.misc.Constants.ASIAN_ELEPHANT_COL;
import static icn.proludic.misc.Constants.ASIAN_GUAR_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_BRONZE_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_COPPER_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_DIAMOND_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_GOLD_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_PLATINUM_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_SILVER_COL;
import static icn.proludic.misc.Constants.BURJ_KHALIFA_COL;
import static icn.proludic.misc.Constants.CHALLENGE_BEGIN_COL;
import static icn.proludic.misc.Constants.CROCODILE_COL;
import static icn.proludic.misc.Constants.EIFFEL_TOWER_COL;
import static icn.proludic.misc.Constants.EMPIRE_STATE_BUILDING_COL;
import static icn.proludic.misc.Constants.FACEBOOK;
import static icn.proludic.misc.Constants.FAVOURITE_EXERCISES;
import static icn.proludic.misc.Constants.FAVOURITE_WORKOUTS;
import static icn.proludic.misc.Constants.GETTING_STARTED_COL;
import static icn.proludic.misc.Constants.GIRAFFE_COL;
import static icn.proludic.misc.Constants.GOLDEN_GATE_BRIDGE_COL;
import static icn.proludic.misc.Constants.HIPPOPOTAMUS_COL;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.KEEPING_THE_PEACE_COL;
import static icn.proludic.misc.Constants.KODIAK_BEAR_COL;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.LOCATION_TOTAL_HEARTS;
import static icn.proludic.misc.Constants.LONDON_EYE_COL;
import static icn.proludic.misc.Constants.NOT_SELECTED;
import static icn.proludic.misc.Constants.PROFILE_PERFECT_COL;
import static icn.proludic.misc.Constants.PROLUDIC_BRONZE_COL;
import static icn.proludic.misc.Constants.PROLUDIC_COPPER_COL;
import static icn.proludic.misc.Constants.PROLUDIC_DIAMOND_COL;
import static icn.proludic.misc.Constants.PROLUDIC_GOLD_COL;
import static icn.proludic.misc.Constants.PROLUDIC_PLATINUM_COL;
import static icn.proludic.misc.Constants.PROLUDIC_SILVER_COL;
import static icn.proludic.misc.Constants.REGISTRATION_COL;
import static icn.proludic.misc.Constants.SOCIALIZE_COL;
import static icn.proludic.misc.Constants.SOCIAL_BUZZ_COL;
import static icn.proludic.misc.Constants.TOTAL_NON_WEIGHT_EXERCISES;
import static icn.proludic.misc.Constants.TOTAL_WEIGHT_EXERCISES;
import static icn.proludic.misc.Constants.USER;
import static icn.proludic.misc.Constants.USER_ACHIEVEMENTS;
import static icn.proludic.misc.Constants.USER_ACHIEVEMENTS_CLASS;
import static icn.proludic.misc.Constants.USER_ACHIEVEMENTS_CLASS_NAME;
import static icn.proludic.misc.Constants.USER_BODY_WEIGHT;
import static icn.proludic.misc.Constants.USER_DESCRIPTION;
import static icn.proludic.misc.Constants.USER_DRAW;
import static icn.proludic.misc.Constants.USER_EXERCISES;
import static icn.proludic.misc.Constants.USER_FRIENDS;
import static icn.proludic.misc.Constants.USER_HEARTS;
import static icn.proludic.misc.Constants.USER_HEIGHT;
import static icn.proludic.misc.Constants.USER_IS_MALE;
import static icn.proludic.misc.Constants.USER_IS_OVER_18;
import static icn.proludic.misc.Constants.USER_LOSSES;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;
import static icn.proludic.misc.Constants.USER_WEIGHT;
import static icn.proludic.misc.Constants.USER_WINS;
import static icn.proludic.misc.Constants.VICTORIOUS_COL;
import static icn.proludic.misc.Constants.WEEKLY_WORKER_COL;
import static icn.proludic.misc.Constants.WHALE_SHARK_COL;
import static icn.proludic.misc.Constants.WHITE_RHINOCEROS_COL;
import static icn.proludic.misc.Constants.WORKING_OUT_COL;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class SashidoHelper {

    private static boolean earned;

    public static boolean isLogged() {
        return ParseUser.getCurrentUser() != null;
    }

    public static void logIn(final Context mContext, final Activity activity, String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.e(mContext.getString(R.string.login_activity_event), mContext.getString(R.string.loggingIn));
                    goToDashboard(activity);
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                    showErrorDialog(mContext, e);
                }
            }
        });
    }

    static ParseUser user;
    public static void register(final Context mContext, final Activity activity, String rName, String rUsername, String rConfirmEmail, String rConfirmPassword, final String loginType, boolean isOver18, String height, String weight, boolean isMale, String profilePicture, final String facebookID) {
        JSONArray newJSONArray = new JSONArray();
        if (loginType.equals(FACEBOOK)) {
            try {
                user = ParseUser.getCurrentUser().fetch();
            } catch (ParseException e) {
                // throw an error
                Log.e("SashidoHelper", "failed to get current Facebook user");
                return;
            }
        } else {
            user = new ParseUser();
        }

        user.put(mContext.getString(R.string.SASHIDO_USER_NAME_KEY), rName);
        user.put(mContext.getString(R.string.SASHIDO_USER_LOGIN_KEY), loginType);
        user.put(USER_FRIENDS, newJSONArray);
        user.put(USER_HEARTS, 200);    // Registration achievement earned
        user.put(USER_DESCRIPTION, mContext.getResources().getString(R.string.defaultdescription));
        user.put(HOME_PARK_KEY, NOT_SELECTED);
        user.put(USER_WEIGHT, 0);
        user.put(USER_EXERCISES, 0);
        user.put(TOTAL_NON_WEIGHT_EXERCISES, 0);
        user.put(TOTAL_WEIGHT_EXERCISES, 0);
        user.put(USER_PROFILE_PICTURE, profilePicture);
        user.put(USER_ACHIEVEMENTS, 1);
        user.put(USER_WINS, 0);
        user.put(USER_DRAW, 0);
        user.put(USER_LOSSES, 0);
        user.put(USER_IS_OVER_18, isOver18);
        user.put(USER_IS_MALE, isMale);
        user.put(USER_BODY_WEIGHT, weight);
        user.put(USER_HEIGHT, height);
        user.put(FAVOURITE_EXERCISES, new JSONArray());
        user.put(FAVOURITE_WORKOUTS, new JSONArray());
        user.setUsername(rUsername);
        user.setEmail(rConfirmEmail);

        Log.e("debug", "loginType is " + loginType);

        if (loginType.equals(FACEBOOK)) {
            user.put("facebookID", facebookID);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        createAchievementRecords();
                        importFacebookFriends(facebookID);
                        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);
                        logger.logEvent("Facebook Sign Up");
                        Map<String, String> articleParams = new HashMap<String, String>();
                        //param keys and values have to be of String type
                        articleParams.put("Facebook Sign Up", user.getEmail() + " signed up through Facebook");
                        //up to 10 params can be logged with each event
                        FlurryAgent.logEvent("Facebook Sign Up", articleParams);
                        goToDashboard(activity);
                    } else {
                        Log.e("debug", e.getLocalizedMessage());
                        showErrorDialog(mContext, e);
                    }
                }
            });
        } else {
            user.put("password", rConfirmPassword);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        createAchievementRecords();
                        AppEventsLogger logger = AppEventsLogger.newLogger(mContext);
                        logger.logEvent("Email Sign Up");
                        Map<String, String> articleParams = new HashMap<String, String>();
                        //param keys and values have to be of String type
                        articleParams.put("Email Sign Up", user.getEmail() + " signed up by email");
                        //up to 10 params can be logged with each event
                        FlurryAgent.logEvent("Email Sign Up", articleParams);
                        goToDashboard(activity);
                    } else {
                        Log.e("debug", e.getLocalizedMessage());
                        showErrorDialog(mContext, e);
                    }
                }
            });
        }
    }

    public static void earnAchievement(final String ACHIEVEMENT_COL, final String ACHIEVEMENT_NAME, final Context context, final View view) {
        Log.e("achievement", ACHIEVEMENT_NAME);
        final ParseUser user = ParseUser.getCurrentUser();
        // find current user's row in UserAchievements table
        ParseQuery<ParseObject> userAchievements = ParseQuery.getQuery(USER_ACHIEVEMENTS_CLASS);
        userAchievements.whereEqualTo(USER, user);
        userAchievements.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    // if user does not already have the achievement
                    final ParseObject userRow = objects.get(0);
                    if (!userRow.getBoolean(ACHIEVEMENT_COL)) {
                        // get hearts for achievement
                        ParseQuery<ParseObject> achievements = ParseQuery.getQuery(ACHIEVEMENTS_CLASS);
                        achievements.whereEqualTo("AchievementName", ACHIEVEMENT_NAME);
                        // added this
                        // achievements.whereEqualTo("isFrench", SharedPreferencesManager.getString(context, "locale").equals("fr"));
                        achievements.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e1) {
                                if (e1 == null) {
                                    Log.e("debug", "gets into e1");
                                    userRow.put(ACHIEVEMENT_COL, true);
                                    ParseObject achievement = objects.get(0);
                                    final int hearts = achievement.getInt("HeartsReceived");
                                    int totalAchievements = user.getInt(USER_ACHIEVEMENTS);
                                    int totalHearts = user.getInt(USER_HEARTS);
                                    user.put(USER_ACHIEVEMENTS, totalAchievements + 1);
                                    user.put(USER_HEARTS, totalHearts + hearts);
                                    // save everything
                                    userRow.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e2) {
                                            if (e2 == null) {
                                                Log.e("debug", "gets into e2");
                                                user.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e3) {
                                                        if (e3 == null) {
                                                            updateParkStats(hearts, 1);
                                                            Utils.showAchievementSnackbar(context, view, context.getResources().getQuantityString(R.plurals.achievementsPlural, 1));
                                                            AppEventsLogger logger = AppEventsLogger.newLogger(context);
                                                            logger.logEvent("Achievement Earned");
                                                            Map<String, String> articleParams = new HashMap<String, String>();
                                                            //param keys and values have to be of String type
                                                            articleParams.put("Achievement Earned", ParseUser.getCurrentUser().getEmail() + " has earned the '" + ACHIEVEMENT_NAME + "' achievement.");
                                                            //up to 10 params can be logged with each event
                                                            FlurryAgent.logEvent("Achievement Earned", articleParams);
                                                            Log.e("debug", "gets into e3");
                                                        } else {
                                                            Log.e("debug", e3.getLocalizedMessage());
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.e("debug", e2.getLocalizedMessage());
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("debug", e1.getLocalizedMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("debug", e.getLocalizedMessage());
                }
            }
        });
    }

    // update the park's total achievements and hearts
    private static void updateParkStats(final int parkEarnedHearts, final int totalAchievements) {
        ParseQuery<ParseObject> getPark = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
        getPark.whereEqualTo("objectId", ParseUser.getCurrentUser().getString(HOME_PARK_KEY));
        getPark.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject park = objects.get(0);
                    int parkAchievements = park.getInt(USER_ACHIEVEMENTS) + totalAchievements;
                    int parkHearts = park.getInt(LOCATION_TOTAL_HEARTS) + parkEarnedHearts;
                    park.put(USER_ACHIEVEMENTS, parkAchievements);
                    park.put(LOCATION_TOTAL_HEARTS, parkHearts);
                    park.saveInBackground();
                } else {
                    Log.e("debug", e.getLocalizedMessage());
                }
            }
        });
    }

    private static void importFacebookFriends(String facebookID) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + facebookID + "/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray fbFriends = (JSONArray) response.getJSONObject().get("data");
                            for (int i = 0; i < fbFriends.length(); i++) {
                                addFriendUsingObjIDFromFbID((String) ((JSONObject) fbFriends.get(i)).get("id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private static void addFriendUsingObjIDFromFbID(String facebookID) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("facebookID", facebookID);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    try {
                        autoAddFriendOfFbUser(objects.get(0).getObjectId());
                    } catch (IndexOutOfBoundsException e1) {
                        Log.e("debug", e1.getLocalizedMessage());
                    }
                }
            }
        });
    }

    private static void autoAddFriendOfFbUser(String fbFriendObjID) {
        try {
            final ArrayList<String> appFriends = (ArrayList) ParseUser.getCurrentUser().fetch().get("Friends");
            // if user has no in-app friends or they do not already have this Facebook friend added
            if (appFriends == null || !appFriends.contains(fbFriendObjID)) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("senderUserId", ParseUser.getCurrentUser().getObjectId());
                params.put("recipientUserId", fbFriendObjID);
                ParseCloud.callFunctionInBackground("AddNewFriend", params, new FunctionCallback<String>() {
                    public void done(String success, ParseException e) {
                        if (e == null) {
                            //
                        }
                    }
                });
                ArrayList<String> tmp;
                if (appFriends != null) {
                    tmp = new ArrayList<String>(appFriends);
                } else {
                    tmp = new ArrayList<String>();
                }
                tmp.add(fbFriendObjID);
                ParseUser.getCurrentUser().put("Friends", tmp);
                ParseUser.getCurrentUser().saveInBackground();
            } else {
                //
            }
        } catch (ParseException pe) {
            //
        }
    }

    private static DialogInterface.OnClickListener customDialogInterfaceListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int buttonID) {
            switch (buttonID) {
                case BUTTON_NEUTRAL:
                    dialogInterface.dismiss();
                    break;
            }
        }
    };

    public static void showErrorDialog(Context mContext, ParseException e) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(mContext.getString(R.string.something_went_wrong));
        alertDialog.setMessage(determineError(mContext, e));
        alertDialog.setButton(BUTTON_NEUTRAL, mContext.getString(R.string.ok), customDialogInterfaceListener);
        alertDialog.show();
    }

    private static String determineError(Context mContext, ParseException e) {
        switch (e.getCode()) {
            case ParseException.USERNAME_TAKEN:
                return mContext.getString(R.string.username_taken_err);
            case ParseException.OBJECT_NOT_FOUND:
                return mContext.getString(R.string.invalid_creds_err);
            case ParseException.CONNECTION_FAILED:
                return mContext.getString(R.string.connection_needed_err);
            case ParseException.EMAIL_NOT_FOUND:
                return mContext.getString(R.string.empty_err_msg_email);
            case ParseException.EMAIL_TAKEN:
                return mContext.getString(R.string.email_taken_err);
            default:
                return mContext.getString(R.string.technical_err_msg);
        }
    }

    public static void logOut() {
        ParseUser.logOutInBackground();
        LoginManager.getInstance().logOut();
    }

    public static void goToDashboard(Activity activity) {
        Intent i = new Intent(activity, DashboardActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static String getCurrentName() {
        return ParseUser.getCurrentUser().getString("name");
    }

    public static void setProfilePicture(Context mContext, ImageView profilePicture) {
        Picasso.with(mContext).load(getCurrentProfilePicture()).placeholder(R.mipmap.ic_launcher).transform(new CircleTransform()).into(profilePicture);
    }

    private static String getCurrentProfilePicture() {
        return ParseUser.getCurrentUser().getString("profilePicture");
    }

    public static String getCurrentLoginMethod() {
        return ParseUser.getCurrentUser().getString("login");
    }

    public static String getCurrentFacebookID() {
        return ParseUser.getCurrentUser().getString("facebookID");
    }

    public static JSONArray getCurrentFriends() {
        return ParseUser.getCurrentUser().getJSONArray("friends");
    }

    static void addUsername(String username) {
        ParseUser.getCurrentUser().setUsername(username);
        ParseUser.getCurrentUser().saveInBackground();
    }

    public static void createAchievementRecords() {
        // initialise all achievement earnings to false
        ParseObject userAchievements = new ParseObject(USER_ACHIEVEMENTS_CLASS_NAME);
        String[] achievements = {KODIAK_BEAR_COL, CROCODILE_COL, ASIAN_GUAR_COL, GIRAFFE_COL, HIPPOPOTAMUS_COL, WHITE_RHINOCEROS_COL, ASIAN_ELEPHANT_COL,
                AFRICAN_ELEPHANT_COL, WHALE_SHARK_COL, LONDON_EYE_COL, EIFFEL_TOWER_COL, EMPIRE_STATE_BUILDING_COL, BURJ_KHALIFA_COL,
                GOLDEN_GATE_BRIDGE_COL, GETTING_STARTED_COL, WORKING_OUT_COL, PROFILE_PERFECT_COL, SOCIALIZE_COL,
                SOCIAL_BUZZ_COL, CHALLENGE_BEGIN_COL, VICTORIOUS_COL, WEEKLY_WORKER_COL, KEEPING_THE_PEACE_COL, PROLUDIC_COPPER_COL,
                PROLUDIC_BRONZE_COL, PROLUDIC_SILVER_COL, PROLUDIC_GOLD_COL, PROLUDIC_PLATINUM_COL, PROLUDIC_DIAMOND_COL, BODYWEIGHT_COPPER_COL,
                BODYWEIGHT_BRONZE_COL, BODYWEIGHT_SILVER_COL, BODYWEIGHT_GOLD_COL, BODYWEIGHT_PLATINUM_COL, BODYWEIGHT_DIAMOND_COL};

        for (String achievement : achievements) {
            userAchievements.put(achievement, false);
        }
        userAchievements.put(REGISTRATION_COL, true);
        userAchievements.put(USER, ParseUser.getCurrentUser());
        userAchievements.saveEventually();
    }

    public static void subscribeUserID(String objectID) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userId", objectID);
        installation.put("user", ParseUser.getCurrentUser());
        installation.put("GCMSenderId", Constants.GCM_SENDER_ID);
        installation.saveInBackground();
        Log.e("", "Subscribed with objectID: " + objectID);
    }
}