package icn.proludic.misc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import icn.proludic.Application;
import icn.proludic.DashboardActivity;
import icn.proludic.LoginActivity;
import icn.proludic.R;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.View.GONE;
import static icn.proludic.misc.Constants.DATE;
import static icn.proludic.misc.Constants.EMAIL;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_ACCEPTED;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_CLASS_NAME;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_IS_CHALLENGE;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_IS_COMPLETE;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_IS_PENDING;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_USER_REQUESTED;
import static icn.proludic.misc.Constants.FRIEND_REQUESTS_USER_REQUESTING;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.LENGTH_LONG;
import static icn.proludic.misc.Constants.LOCATION_SERVICES;
import static icn.proludic.misc.Constants.LOGOUT_ALERT_ID;
import static icn.proludic.misc.Constants.MARGIN_20;
import static icn.proludic.misc.Constants.MAX_LINES;
import static icn.proludic.misc.Constants.NO_PICTURE;
import static icn.proludic.misc.Constants.RESET_PASSWORD;
import static icn.proludic.misc.Constants.TRACKED_EVENTS_CLASS_NAME;
import static icn.proludic.misc.Constants.TRACKED_EXERCISES_USED;
import static icn.proludic.misc.Constants.TRACKED_HEARTS;
import static icn.proludic.misc.Constants.TRACKED_TOTAL_EXERCISES;
import static icn.proludic.misc.Constants.TRACKED_USER;
import static icn.proludic.misc.Constants.USERNAME;
import static icn.proludic.misc.Constants.USERNAME_INPUT;
import static icn.proludic.misc.Constants.USER_DESCRIPTION;
import static icn.proludic.misc.Constants.USER_FULL_NAME;
import static icn.proludic.misc.Constants.USER_HEARTS;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;
import static icn.proludic.misc.Constants.WARNING_DISABLED;
import static icn.proludic.misc.Validate.isValid;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class Utils {
    private Activity activity;
    private Context context;
    private Utils utils;
    private int actionID;
    private AlertDialog dialog;
    private List<String> exericseNames = new ArrayList<>();
    private List<String> workoutNames = new ArrayList<>();
    private double longitude, latitude;
    private DatabaseHandler SQLDatabase;
    private AlertDialog alertDialog;
    private String homeObjectId, location = "";
    private Date df;
    private Date lastWeeksDate;

    public Utils(Object activity) {
        if (activity instanceof Activity) {
            this.activity = (Activity) activity;
        } else {
            this.activity = null;
        }
    }

    public void makeText(String text, int length) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView description = (TextView) layout.findViewById(R.id.toastMessage);
        description.setText(text);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();
    }

    public Object validateProfilePicture(String profilePicture) {
        if (!profilePicture.equals(NO_PICTURE)) {
            return profilePicture;
        } else {
            return R.drawable.no_profile;
        }
    }

    public void showInputDialog(boolean username, Context mContext, Activity activity, Utils utils) {
        String type;
        if (username) {
            type = USERNAME;
        } else {
            type = RESET_PASSWORD;
        }
        this.activity = activity;
        this.context = mContext;
        this.utils = utils;
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.customAlertDialog);
        TextInputLayout til = new TextInputLayout(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int px = Application.convertDpToPixel(MARGIN_20, mContext);
        lp.setMargins(px, 0, px, 0);
        til.setLayoutParams(lp);

        TextInputEditText et = new TextInputEditText(mContext);
        et.setLayoutParams(lp);
        et.setHintTextColor(Color.BLACK);
        et.setMaxLines(MAX_LINES);
        et.setTextColor(Color.BLACK);
        et.setHint(getInputHint(username));
        et.setInputType(getInputType(username));
        til.addView(et);

        alert.setMessage(getAlertMessage(type));
        alert.setTitle(getAlertTitle(type));
        alert.setView(til);
        if (username) {
            alert.setCancelable(false);
        }
        alert.setPositiveButton(getPositiveButton(type), null);
        showAlert(alert, et, type, mContext);
    }

    private int getPositiveButton(String type) {
        switch (type) {
            case USERNAME:
                return R.string.ok;
            case RESET_PASSWORD:
                return R.string.Reset;
            default:
                return R.string.ok;
        }
    }

    private int getAlertTitle(String type) {
        switch (type) {
            case USERNAME:
                return R.string.hint_username;
            case RESET_PASSWORD:
                return R.string.reset_password;
            default:
                return R.string.error;
        }
    }

    private int getAlertMessage(String type) {
        switch (type) {
            case USERNAME:
                return R.string.enter_username;
            case RESET_PASSWORD:
                return R.string.enter_signed_up_email;
            default:
                return R.string.alert_dialog_error;
        }
    }

    private int getInputType(boolean username) {
        if (!username) {
            return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        } else {
            return InputType.TYPE_CLASS_TEXT;
        }
    }

    private int getInputHint(boolean username) {
        if (!username) {
            return R.string.hint_email;
        } else {
            return R.string.hint_username;
        }
    }

    private void showAlert(final AlertDialog.Builder alert, final TextInputEditText et, final String type, final Context mContext) {
        dialog = alert.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                Button reset = dialog.getButton(BUTTON_POSITIVE);
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //What ever you want to do with the value
                        String inputText;
                        switch (type) {
                            case USERNAME:
                                inputText = et.getText().toString().toLowerCase();
                                if (inputText.isEmpty()) {
                                    et.setError(context.getString(R.string.empty_err_msg_username));
                                } else if (!isValid(USERNAME_INPUT, et.getText().toString())) {
                                    et.setError(context.getString(R.string.invalid_username));
                                } else {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                                    try {
                                        boolean isUsername = query.whereEqualTo("username", inputText).find().size() > 0;
                                        if (!isUsername) {
                                            dialog.dismiss();
                                            SashidoHelper.addUsername(inputText);
                                            SashidoHelper.goToDashboard(activity);
                                        } else {
                                            et.setError(context.getResources().getString(R.string.username_exists));
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case RESET_PASSWORD:
                                inputText = et.getText().toString();
                                if (inputText.isEmpty()) {
                                    et.setError(context.getString(R.string.empty_err_msg_email));
                                } else if (!isValid(EMAIL, et.getText().toString())) {
                                    et.setError(context.getString(R.string.invalid_email));
                                } else {
                                    ParseUser.requestPasswordResetInBackground(inputText, new RequestPasswordResetCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                makeText(context.getString(R.string.email_sent), LENGTH_LONG);
                                                dialog.dismiss();
                                            } else {
                                                et.setError(context.getString(R.string.email_doesnt_exist));
                                            }
                                        }
                                    });
                                }
                                break;
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void showButtonDialog(Context mContext, Activity activity, Utils utils, String type) {
        this.activity = activity;
        this.context = mContext;
        this.utils = utils;
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext, R.style.customAlertDialog);
        alert.setMessage(getAlertMessage(type));
        alert.setTitle(getAlertTitle(type));
        alert.setPositiveButton(getPositiveButton(type), null);
        showAlert(alert, null, type, mContext);
    }

    private static boolean DIALOG_IS_SHOWING = false;

    public void showStandardDialog(Context mContext, String dialogTitle, String dialogMessage, String pButtonTitle, String nButtonTitle, boolean positiveButton, boolean negativeButton, int actionID, Activity activity) {
        Log.e("tom", "show dialog");
        this.actionID = actionID;
        this.activity = activity;
        alertDialog = new AlertDialog.Builder(mContext, R.style.customAlertDialog).create();
        alertDialog.setTitle(dialogTitle);
        alertDialog.setMessage(dialogMessage);
        alertDialog.setCanceledOnTouchOutside(false);
        if (positiveButton) {
            alertDialog.setButton(BUTTON_POSITIVE, pButtonTitle, customDialogListener);
        }

        if (negativeButton) {
            alertDialog.setButton(BUTTON_NEGATIVE, nButtonTitle, customDialogListener);
        }

        if (dialogTitle.equals(mContext.getString(R.string.location_services))) {
            if (!DIALOG_IS_SHOWING) {
                alertDialog.show();
                DIALOG_IS_SHOWING = true;
            }
        } else {
            alertDialog.show();
        }
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private DialogInterface.OnClickListener customDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    switch (actionID) {
                        case LOCATION_SERVICES:
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
                            DIALOG_IS_SHOWING = false;
                            break;
                        case LOGOUT_ALERT_ID:
                            SashidoHelper.logOut();
                            Intent i = new Intent(activity, LoginActivity.class);
                            activity.startActivity(i);
                            activity.finish();

                            break;
                    }
                    break;
                case BUTTON_NEGATIVE:
                    switch (actionID) {
                        case LOCATION_SERVICES:
                            DIALOG_IS_SHOWING = false;
                            dialogInterface.dismiss();
                            ((DashboardActivity) activity).initLocation();
                            break;
                        case LOGOUT_ALERT_ID:
                        default:
                            dialogInterface.dismiss();
                            break;
                    }
                    break;
            }
        }
    };

    public void requestFocus(View view, Activity mContext) {
        if (view.requestFocus()) {
            mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void hideKeyboard(View view, Context mContext) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showSnackBar(Context context, CoordinatorLayout mCoordinatorLayout, String s, int lengthLong) {
        Snackbar snackBar = Snackbar.make(mCoordinatorLayout, s, lengthLong);
        // Changing action button text color
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        snackBar.show();
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setSQLDatabase(DatabaseHandler SQLDatabase) {
        this.SQLDatabase = SQLDatabase;
    }

    public DatabaseHandler getSQLDatabase() {
        return SQLDatabase;
    }

    public void populateUserDetails(Context context, TextView username, TextView parkName, boolean doesParkExist, ImageView profileImage, TextView hearts, String homePark) throws ParseException {
        username.setText(ParseUser.getCurrentUser().getUsername().toUpperCase());
        if (doesParkExist) {
            parkName.setText(homePark);
        } else {
            parkName.setVisibility(GONE);
        }
        hearts.setText(String.valueOf(ParseUser.getCurrentUser().getInt(USER_HEARTS)));
        if (ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE) != null) {
            if (ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE).equals(NO_PICTURE)) {
                Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(profileImage);
            } else {
                Picasso.with(context).load(ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE)).transform(new CircleTransform()).into(profileImage);
            }
        } else {
            Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(profileImage);
        }
    }

    public static void showAchievementSnackbar(Context context, View view, String string) {
        final Snackbar snackBar = Snackbar.make(view, string, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.okay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });

        snackBar.setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        View sbView = snackBar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.show();
    }

    public boolean populateUserProfileDetails(Context context, TextView fullName, TextView userName, ImageView profileImage, TextView hearts, EditText description, TextView homePark) {
        description.setText(ParseUser.getCurrentUser().getString(USER_DESCRIPTION));
        fullName.setText(ParseUser.getCurrentUser().getString(USER_FULL_NAME));
        userName.setText(ParseUser.getCurrentUser().getUsername());
        homePark.setText(getHomeParkLocation(ParseUser.getCurrentUser()));
        if (ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE) != null) {
            if (!ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE).equals(NO_PICTURE)) {
                Picasso.with(context).load(ParseUser.getCurrentUser().getString(USER_PROFILE_PICTURE)).transform(new CircleTransform()).into(profileImage);
            } else {
                Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(profileImage);
                return true;
            }
        } else {
            Picasso.with(context).load(R.drawable.no_profile).transform(new CircleTransform()).into(profileImage);
        }
        hearts.setText(String.valueOf(ParseUser.getCurrentUser().getInt(USER_HEARTS)));
        return false;
    }

    private String getHomeParkLocation(ParseObject user) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.LOCATIONS_CLASS_KEY);
        query.whereEqualTo("objectId", user.getString(HOME_PARK_KEY));
        try {
            ParseObject park = query.find().get(0);
            return park.getString(Constants.LOCATION_NAME_KEY);
        } catch (ParseException e) {
            Log.e("debug", e.getLocalizedMessage());
            return activity.getString(R.string.not_selected);
        } catch (IndexOutOfBoundsException e) {
            Log.e("debug", e.getLocalizedMessage());
            return activity.getString(R.string.not_selected);
        }
    }

    public String getHomeParkLocation(String homePark) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.LOCATIONS_CLASS_KEY);
        query.whereEqualTo("objectId", homePark);
        try {
            ParseObject park = query.find().get(0);
            return park.getString(Constants.LOCATION_NAME_KEY);
        } catch (ParseException e) {
            Log.e("debug", e.getLocalizedMessage());
            return activity.getString(R.string.not_selected);
        } catch (IndexOutOfBoundsException e) {
            Log.e("debug", e.getLocalizedMessage());
            return activity.getString(R.string.not_selected);
        }
    }

    public int convertDpToPixels(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public String getTodaysDateString() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date today = Calendar.getInstance().getTime();
        return df.format(today);
    }

    public Date getTodaysDate() {
        String date = getTodaysDateString();
        try {
            df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return df;
    }

    public void addedToTrackedEvents(final int totalHearts, final ArrayList<String> exerciseIds) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TRACKED_EVENTS_CLASS_NAME);
        final String date = getTodaysDateString();
        query.whereEqualTo(TRACKED_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(DATE, date);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject j : objects) {
                            int todaysHearts = j.getInt(TRACKED_HEARTS);
                            todaysHearts = todaysHearts + totalHearts;
                            j.put(TRACKED_HEARTS, todaysHearts);
                            int todaysExercises = j.getInt(TRACKED_TOTAL_EXERCISES);
                            todaysExercises = todaysExercises + 1;
                            j.put(TRACKED_TOTAL_EXERCISES, todaysExercises);
                            j.addAll(TRACKED_EXERCISES_USED, exerciseIds);
                            j.saveInBackground();
                        }
                    } else {
                        ParseObject trackedEvent = new ParseObject(TRACKED_EVENTS_CLASS_NAME);
                        trackedEvent.put(DATE, date);
                        trackedEvent.put(TRACKED_HEARTS, totalHearts);
                        trackedEvent.put(TRACKED_USER, ParseUser.getCurrentUser());
                        trackedEvent.put(TRACKED_TOTAL_EXERCISES, 1);
                        trackedEvent.addAll(TRACKED_EXERCISES_USED, exerciseIds);
                        trackedEvent.saveInBackground();
                    }
                } else {
                    Log.e("Failed", "failed" + e.getLocalizedMessage());
                }
            }
        });
    }

    public void setHomeObjectId(String homeObjectId) {
        this.homeObjectId = homeObjectId;
    }

    public String getHomeObjectId() {
        return homeObjectId;
    }

    public List<String> getDates(String dateString1, String dateString2)
    {
        List<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            while(!cal1.after(cal2))
            {
                dates.add(dateToString(cal1.getTime()));
                cal1.add(Calendar.DATE, 1);
            }
        } catch (java.text.ParseException e) {
            Log.e("failed", "PE" + e.getLocalizedMessage());
        }
        return dates;
    }

    public static void showCautionDialog(final String name, String type, final Context context) {
        String warning;
        if (name.toLowerCase().endsWith(type.toLowerCase())) {
            warning = context.getResources().getString(R.string.caution_start) + " " + name.toLowerCase() +
                    " " + context.getResources().getString(R.string.caution_end);
        } else {
            warning = context.getResources().getString(R.string.caution_start) + " " + name.toLowerCase() +
                    " " + type + " " + context.getResources().getString(R.string.caution_end);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_caution, null);
        builder.setView(view);

        final CheckBox do_not_warn = view.findViewById(R.id.do_not_warn);
        final TextView tv_warning =  view.findViewById(R.id.warning_message);
        tv_warning.setText(warning);

        final AlertDialog dialog = builder.create();

        view.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (do_not_warn.isChecked()) {
                    SharedPreferencesManager.setBoolean(context, WARNING_DISABLED, true);
                }
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private String dateToString(Date time) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return df.format(time);
    }

    public ParseQuery<ParseObject> getQuery(ParseObject user, ParseObject otherUser, boolean isChallenge, boolean isAccepted, boolean isPending, boolean b) {
        ParseQuery<ParseObject> aQuery = ParseQuery.getQuery(FRIEND_REQUESTS_CLASS_NAME);
        if (b) {
            aQuery.whereEqualTo(FRIEND_REQUESTS_USER_REQUESTED, user);
            aQuery.whereEqualTo(FRIEND_REQUESTS_USER_REQUESTING, otherUser);
        } else {
            aQuery.whereEqualTo(FRIEND_REQUESTS_USER_REQUESTED, otherUser);
            aQuery.whereEqualTo(FRIEND_REQUESTS_USER_REQUESTING, user);
        }
        aQuery.whereEqualTo(FRIEND_REQUESTS_ACCEPTED, isAccepted);
        aQuery.whereEqualTo(FRIEND_REQUESTS_IS_PENDING, isPending);
        aQuery.whereEqualTo(FRIEND_REQUESTS_IS_CHALLENGE, isChallenge);
        aQuery.whereEqualTo(FRIEND_REQUESTS_IS_COMPLETE, false);
        return aQuery;
    }

    public String getDateBefore(Date startDate, int days, boolean isBefore) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if (isBefore) {
            calendar.add(Calendar.DAY_OF_YEAR, -days);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, days);
        }
        return df.format(calendar.getTime());
    }

    public String getLastMonthsDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getTodaysDate());
        calendar.add(Calendar.MONTH, -1);
        return df.format(calendar.getTime());
    }

    public String getDateAndTime(Date createdAt) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdAt);
        return df.format(calendar.getTime());
    }

    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public boolean isNetworkAvailable(Context context) {
        return getNetworkInfo(context) != null && getNetworkInfo(context).isConnectedOrConnecting();
    }

    public ArrayList<String> convertJSONtoArrayList(JSONArray jsonArray) {
        ArrayList<String> tempList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tempList.add(jsonArray.optString(i));
        }
        return tempList;
    }
}
