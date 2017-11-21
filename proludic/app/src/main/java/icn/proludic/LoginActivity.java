package icn.proludic;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import icn.proludic.listeners.ViewAnimatorAnimatorListener;
import icn.proludic.misc.Constants;
import icn.proludic.misc.SashidoHelper;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.misc.Utils;

import static icn.proludic.misc.Constants.AGE;
import static icn.proludic.misc.Constants.EMAIL;
import static icn.proludic.misc.Constants.EMPTY;
import static icn.proludic.misc.Constants.FACEBOOK;
import static icn.proludic.misc.Constants.FAVOURITE_EXERCISES;
import static icn.proludic.misc.Constants.FAVOURITE_WORKOUTS;
import static icn.proludic.misc.Constants.FB_EMAIL;
import static icn.proludic.misc.Constants.FB_PUBLIC_PROFILE;
import static icn.proludic.misc.Constants.FB_USER_BIRTHDAY;
import static icn.proludic.misc.Constants.FB_USER_FRIENDS;
import static icn.proludic.misc.Constants.FIRST_RUN;
import static icn.proludic.misc.Constants.HEIGHT_ADJUSTMENT;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.LENGTH_LONG;
import static icn.proludic.misc.Constants.LOGIN_TYPE;
import static icn.proludic.misc.Constants.LOGIN_VA_CHILD_ONE;
import static icn.proludic.misc.Constants.LOGIN_VA_CHILD_THREE;
import static icn.proludic.misc.Constants.LOGIN_VA_CHILD_TWO;
import static icn.proludic.misc.Constants.NAME;
import static icn.proludic.misc.Constants.NORMAL_RUN;
import static icn.proludic.misc.Constants.NOT_SELECTED;
import static icn.proludic.misc.Constants.ONE_SECOND;
import static icn.proludic.misc.Constants.PASSWORD;
import static icn.proludic.misc.Constants.SHARED_PREFS_GENDER;
import static icn.proludic.misc.Constants.SHARED_PREFS_HEIGHT;
import static icn.proludic.misc.Constants.SHARED_PREFS_OVER_18;
import static icn.proludic.misc.Constants.SHARED_PREFS_USERNAME_OR_UPLOAD;
import static icn.proludic.misc.Constants.SHARED_PREFS_WEIGHT;
import static icn.proludic.misc.Constants.TWO_SECONDS;
import static icn.proludic.misc.Constants.UPGRADED_RUN;
import static icn.proludic.misc.Constants.USERNAME;
import static icn.proludic.misc.Constants.USERNAME_INPUT;
import static icn.proludic.misc.Constants.USER_ACHIEVEMENTS;
import static icn.proludic.misc.Constants.USER_DESCRIPTION;
import static icn.proludic.misc.Constants.USER_DRAW;
import static icn.proludic.misc.Constants.USER_EXERCISES;
import static icn.proludic.misc.Constants.USER_FACEBOOK_ID;
import static icn.proludic.misc.Constants.USER_FULL_NAME;
import static icn.proludic.misc.Constants.USER_HEARTS;
import static icn.proludic.misc.Constants.USER_LOSSES;
import static icn.proludic.misc.Constants.USER_PROFILE_PICTURE;
import static icn.proludic.misc.Constants.USER_WEIGHT;
import static icn.proludic.misc.Constants.USER_WINS;
import static icn.proludic.misc.Constants.WARNING_DISABLED;
import static icn.proludic.misc.Validate.isValid;

/**
 * Author:  Bradley Wilson
 * Date: 04/04/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class LoginActivity extends AppCompatActivity {

    private ViewAnimator login_va;
    private EditText inputUsername, inputPassword, inputRegisterName, inputRegisterUsername, inputRegisterEmail,
            inputRegisterConfirmEmail, inputRegisterPassword, inputRegisterConfirmPassword;
    private long mBackPressed;
    private TextInputLayout inputLayoutUsername, inputLayoutPassword, inputRegisterLayoutName, inputRegisterLayoutUsername,
            inputRegisterLayoutEmail, inputRegisterLayoutConfirmEmail, inputRegisterLayoutPassword, inputRegisterLayoutConfirmPassword;
    private ImageView login_logo;
    private final List<String> pPermissions = Arrays.asList(FB_PUBLIC_PROFILE, FB_EMAIL, FB_USER_BIRTHDAY, FB_USER_FRIENDS);
    private Context mContext = this;
    private Utils utils;
    private String type;
    private String age;

    private void initAnimation() {
        Animation bottomUp = AnimationUtils.loadAnimation(mContext,
                R.anim.login_button_anim);
        login_va.startAnimation(bottomUp);
        login_va.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        type = Application.checkFirstRun(mContext);
        utils = new Utils(mContext);
        initViews();
        goToActivity();
    }

    private void initViews() {
        //main logo init
        login_logo = (ImageView) findViewById(R.id.activity_login_logo);

        //login page view animator init + assingment of animations
        login_va = (ViewAnimator) findViewById(R.id.activity_login_view_animator);
        Animation inAnim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation outAnim = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        login_va.setInAnimation(inAnim);
        login_va.setOutAnimation(outAnim);

        //all buttons on the login page init
        ImageButton login_email, login_register, login_facebook, login_email_child, login_register_child;
        login_email = (ImageButton) findViewById(R.id.activity_login_email_button);
        login_register = (ImageButton) findViewById(R.id.activity_login_register_button);
        login_facebook = (ImageButton) findViewById(R.id.activity_login_facebook_button);
        login_email_child = (ImageButton) findViewById(R.id.activity_login_email_button_child);
        login_register_child = (ImageButton) findViewById(R.id.activity_register_email_button_child);
        login_email.setOnTouchListener(customTouchListener);
        login_register.setOnTouchListener(customTouchListener);
        login_facebook.setOnTouchListener(customTouchListener);
        login_email_child.setOnTouchListener(customTouchListener);
        login_register_child.setOnTouchListener(customTouchListener);

        //all Textinputlayouts and edit texts init
        inputLayoutUsername = (TextInputLayout) findViewById(R.id.login_email_username);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.login_email_password);
        inputRegisterLayoutName = (TextInputLayout)findViewById(R.id.register_email_name);
        inputRegisterLayoutUsername = (TextInputLayout)findViewById(R.id.register_email_username);
        inputRegisterLayoutEmail = (TextInputLayout)findViewById(R.id.register_email_email);
        inputRegisterLayoutConfirmEmail = (TextInputLayout)findViewById(R.id.register_email_confirm_email);
        inputRegisterLayoutPassword = (TextInputLayout)findViewById(R.id.register_email_password);
        inputRegisterLayoutConfirmPassword = (TextInputLayout)findViewById(R.id.register_email_confirm_password);

        inputUsername = (EditText) findViewById(R.id.login_input_username);
        inputUsername.setOnFocusChangeListener(customFocusChangeListener);
        inputPassword = (EditText) findViewById(R.id.login_input_password);
        inputPassword.setOnFocusChangeListener(customFocusChangeListener);
        inputRegisterName = (EditText) findViewById(R.id.register_input_name);
        inputRegisterName.setOnFocusChangeListener(customFocusChangeListener);
        inputRegisterUsername = (EditText) findViewById(R.id.register_input_username);
        inputRegisterUsername.setOnFocusChangeListener(customFocusChangeListener);
        inputRegisterEmail = (EditText) findViewById(R.id.register_input_email);
        inputRegisterEmail.setOnFocusChangeListener(customFocusChangeListener);
        inputRegisterConfirmEmail = (EditText) findViewById(R.id.register_input_confirm_email);
        inputRegisterConfirmEmail.setOnFocusChangeListener(customFocusChangeListener);
        inputRegisterPassword = (EditText) findViewById(R.id.register_input_password);
        inputRegisterPassword.setOnFocusChangeListener(customFocusChangeListener);
        inputRegisterConfirmPassword = (EditText) findViewById(R.id.register_input_confirm_password);
        inputRegisterConfirmPassword.setOnFocusChangeListener(customFocusChangeListener);

        TextView forgotPassword = (TextView) findViewById(R.id.activity_login_email_forgot_password);
        forgotPassword.setOnTouchListener(customTouchListener);
    }

    private View.OnTouchListener customTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                switch(view.getId()) {
                    case R.id.activity_login_email_button:
                        login_va.setDisplayedChild(LOGIN_VA_CHILD_TWO);
                        break;
                    case R.id.activity_login_register_button:
                        login_va.setDisplayedChild(LOGIN_VA_CHILD_THREE);
                        defineLayoutChanges();
                        break;
                    case R.id.activity_login_facebook_button:
                        Log.e(getString(R.string.login_activity_event), getString(R.string.Facebook_login));
                        ParseFacebookUtils.logInWithReadPermissionsInBackground((Activity) mContext, pPermissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (user == null) {
                                    // Log.e(getString(R.string.login_activity_event), err.getLocalizedMessage());
                                    Log.e(getString(R.string.login_activity_event), getString(R.string.facebook_login_cancel));
                                } else if (user.isNew()) {
                                    Log.e(getString(R.string.login_activity_event), getString(R.string.facebook_new_user));
                                    sendGraphRequest(mContext);
                                } else {
                                    Log.e(getString(R.string.login_activity_event), getString(R.string.facebook_login_success));
                                    SashidoHelper.goToDashboard(LoginActivity.this);
                                }
                            }
                        });
                        break;
                    case R.id.activity_login_email_button_child:
                        String lUsername = inputUsername.getText().toString().trim();
                        String lPassword = inputPassword.getText().toString().trim();
                        if (!validateLoginDetails(USERNAME, lUsername, lPassword)){
                            break;
                        }
                        if (!validateLoginDetails(PASSWORD, lUsername, lPassword)) {
                            break;
                        }
                        SashidoHelper.logIn(mContext, LoginActivity.this, lUsername, lPassword);
                        break;
                    case R.id.activity_register_email_button_child:
                        String rName = inputRegisterName.getText().toString().trim();
                        String rUsername = inputRegisterUsername.getText().toString().trim();
                        String rEmail = inputRegisterEmail.getText().toString().trim();
                        String rConfirmEmail = inputRegisterConfirmEmail.getText().toString().trim();
                        String rPassword = inputRegisterPassword.getText().toString().trim();
                        String rConfirmPassword = inputRegisterConfirmPassword.getText().toString().trim();
                        if (!validateRegistrationDetails(NAME, rName, EMPTY)) {
                            break;
                        }

                        if (!validateRegistrationDetails(USERNAME, rUsername, EMPTY)) {
                            break;
                        }

                        if (!validateRegistrationDetails(EMAIL, rEmail, rConfirmEmail)) {
                            break;
                        }

                        if (!validateRegistrationDetails(PASSWORD, rPassword, rConfirmPassword)) {
                            break;
                        }

                        goToExtendedReg(EMAIL, rName, null, rUsername, rConfirmEmail, rConfirmPassword, null, null);
//                      SashidoHelper.register(mContext, LoginActivity.this, rName, rUsername, rConfirmEmail, rConfirmPassword);
                        break;
                    case R.id.activity_login_email_forgot_password:
                        utils.showInputDialog(false, mContext, LoginActivity.this, utils);
                        break;
                }
            }
            return false;
        }
    };

    private void goToExtendedReg(String loginType, String rName, String rAge, String rUsername, String rConfirmEmail, String rConfirmPassword, String profilePicture, String facebookID) {
        Intent intent = new Intent(LoginActivity.this, ActivityExtendedRegistration.class);
        intent.putExtra(LOGIN_TYPE, loginType);
        intent.putExtra(NAME, rName);
        intent.putExtra(AGE, rAge);
        intent.putExtra(USERNAME, rUsername);
        intent.putExtra(EMAIL, rConfirmEmail);
        intent.putExtra(PASSWORD, rConfirmPassword);
        intent.putExtra(USER_PROFILE_PICTURE, profilePicture);
        intent.putExtra(USER_FACEBOOK_ID, facebookID);
        Log.e("debug", "loginType is " + loginType);
        startActivity(intent);
    }

    private void defineLayoutChanges() {
        animate(true, login_logo);
        animate(true, null);
    }

    private EditText.OnFocusChangeListener customFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {
                switch (view.getId()) {
                    case R.id.register_input_name:
                        if (!isValid(NAME, inputRegisterName.getText().toString().trim())) {
                            inputRegisterLayoutName.setError(getString(R.string.invalid_name));
                        } else {
                            inputRegisterLayoutName.setErrorEnabled(false);
                        }
                        break;
                    case R.id.register_input_username:
                        if (!isValid(USERNAME_INPUT, inputRegisterUsername.getText().toString().trim())) {
                            inputRegisterLayoutUsername.setError(getString(R.string.wrong_format_username));
                        } else {
                            inputRegisterLayoutUsername.setErrorEnabled(false);
                        }
                        break;
                    case R.id.register_input_email:
                        if (!isValid(EMAIL, inputRegisterEmail.getText().toString().trim())) {
                            inputRegisterLayoutEmail.setError(getString(R.string.invalid_email));
                        } else {
                            inputRegisterLayoutEmail.setErrorEnabled(false);
                        }
                        break;
                    case R.id.register_input_confirm_email:
                        if (!isValid(EMAIL, inputRegisterConfirmEmail.getText().toString().trim())) {
                            inputRegisterLayoutConfirmEmail.setError(getString(R.string.invalid_email));
                        } else {
                            inputRegisterLayoutConfirmEmail.setErrorEnabled(false);
                        }
                        break;
                    case R.id.register_input_password:
                        if (!isValid(PASSWORD, inputRegisterPassword.getText().toString().trim())) {
                            inputRegisterLayoutPassword.setError(getString(R.string.invalid_password));
                        } else {
                            inputRegisterLayoutPassword.setErrorEnabled(false);
                        }
                        break;
                    case R.id.register_input_confirm_password:
                        if (!isValid(PASSWORD, inputRegisterConfirmPassword.getText().toString().trim())) {
                            inputRegisterLayoutConfirmPassword.setError(getString(R.string.invalid_password));
                        } else {
                            inputRegisterLayoutConfirmPassword.setErrorEnabled(false);
                        }
                        utils.hideKeyboard(view, mContext);
                        break;
                }
            }
        }
    };


    public void onBackPressed() {
        if (login_va.getDisplayedChild() != LOGIN_VA_CHILD_ONE) {
            if (login_va.getDisplayedChild() == LOGIN_VA_CHILD_THREE) {
                animate(false, login_logo);
                animate(false, null);
                removeErrorLabels();
            }
            login_va.setDisplayedChild(LOGIN_VA_CHILD_ONE);
        } else {
            if (mBackPressed + TWO_SECONDS > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                utils.makeText(getString(R.string.back_button_exit), LENGTH_LONG);
            }
            mBackPressed = System.currentTimeMillis();
        }
    }

    private void removeErrorLabels() {
        inputRegisterLayoutConfirmEmail.setErrorEnabled(false);
        inputRegisterConfirmEmail.setText(EMPTY);
        inputRegisterLayoutConfirmPassword.setErrorEnabled(false);
        inputRegisterConfirmPassword.setText(EMPTY);
        inputRegisterLayoutEmail.setErrorEnabled(false);
        inputRegisterEmail.setText(EMPTY);
        inputRegisterLayoutName.setErrorEnabled(false);
        inputRegisterName.setText(EMPTY);
        inputRegisterLayoutPassword.setErrorEnabled(false);
        inputRegisterPassword.setText(EMPTY);
        inputRegisterLayoutUsername.setErrorEnabled(false);
        inputRegisterUsername.setText(EMPTY);

    }

    private boolean validateLoginDetails(String type, String username, String password) {
        switch (type) {
            case USERNAME:
                if (username.isEmpty()) {
                    inputLayoutUsername.setError(getString(R.string.empty_err_msg_username));
                    utils.requestFocus(inputUsername, LoginActivity.this);
                    return false;
                } else {
                    inputLayoutUsername.setErrorEnabled(false);
                }
                break;
            case PASSWORD:
                if (password.isEmpty()) {
                    inputLayoutPassword.setError(getString(R.string.empty_err_msg_password));
                    utils.requestFocus(inputPassword, LoginActivity.this);
                    return false;
                } else {
                    inputLayoutPassword.setErrorEnabled(false);
                }
                break;
        }
        return true;
    }

    private boolean validateRegistrationDetails(String type, String input, String confirmInput) {
        switch(type) {
            case NAME:
                if (input.isEmpty()){
                    inputRegisterLayoutName.setError(getString(R.string.empty_err_msg_name));
                    utils.requestFocus(inputRegisterName, LoginActivity.this);
                    return false;
                } else {
                    inputRegisterLayoutName.setErrorEnabled(false);
                }
                break;
            case USERNAME:
                if (input.isEmpty()){
                    inputRegisterLayoutUsername.setError(getString(R.string.empty_err_msg_username));
                    utils.requestFocus(inputRegisterUsername, LoginActivity.this);
                    return false;
                } else {
                    inputRegisterLayoutUsername.setErrorEnabled(false);
                }
                break;
            case EMAIL:
                if (!input.equals(confirmInput)) {
                    inputRegisterLayoutConfirmEmail.setError(getString(R.string.emails_must_match_err));
                    utils.requestFocus(inputRegisterConfirmEmail, LoginActivity.this);
                    return false;
                } else {
                    inputRegisterLayoutConfirmEmail.setErrorEnabled(false);
                }
                break;
            case PASSWORD:
                if (!input.equals(confirmInput)) {
                    inputRegisterLayoutConfirmPassword.setError(getString(R.string.passwords_must_match_err));
                    utils.requestFocus(inputRegisterConfirmPassword, LoginActivity.this);
                    return false;
                } else {
                    inputRegisterLayoutConfirmPassword.setErrorEnabled(false);
                }
                break;
        }
        return true;
    }

    private void animate(boolean up, ImageView logo) {
        ObjectAnimator animation;
        if (up) {
            if (isLoginPage(logo)) {
                animation = ObjectAnimator.ofFloat(logo, getString(R.string.y), logo.getY(), logo.getY() - logo.getHeight() * HEIGHT_ADJUSTMENT);
            } else {
                animation = ObjectAnimator.ofFloat(login_va, getString(R.string.y), login_va.getY(), login_va.getY() - login_logo.getHeight() * HEIGHT_ADJUSTMENT);
            }
        } else {
            if (isLoginPage(logo)) {
                animation = ObjectAnimator.ofFloat(logo, getString(R.string.y), logo.getY(), logo.getY() + logo.getHeight() * HEIGHT_ADJUSTMENT);
            } else {
                animation = ObjectAnimator.ofFloat(login_va, getString(R.string.y), login_va.getY(), login_va.getY() + login_logo.getHeight() * HEIGHT_ADJUSTMENT);
                animation.addListener(new ViewAnimatorAnimatorListener(login_va, mContext));
            }
        }
        animation.setDuration(ONE_SECOND);//set duration
        animation.start();//start animation
    }

    public boolean isLoginPage(ImageView login) {
        return login != null;
    }

    private String name, email, facebookID, profilePicture;
    // private JSONArray fbFriends;
    public void sendGraphRequest(final Context mContext) {
        Log.e("debug", "enters function");
        final GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.e("debug", "enters graph request");
                    name = object.getString("name");
                    Log.e(mContext.getString(R.string.login_activity_event), name);
                    email = object.getString("email");
                    Log.e(mContext.getString(R.string.login_activity_event), email);
                    facebookID = object.getString("id");
                    Log.e(mContext.getString(R.string.login_activity_event), facebookID);
                    profilePicture = "https://graph.facebook.com/"+facebookID+"/picture?type=large";
                    Log.e(mContext.getString(R.string.login_activity_event), profilePicture);
                    age = object.getString("age_range");
                    Log.e(mContext.getString(R.string.login_activity_event), age);
                    Log.e("debug", age);
                } catch(JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            goToExtendedReg(FACEBOOK, name, age, null, email, null, profilePicture, facebookID);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, age_range, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (utils.getDialog() != null) {
            if (utils.getDialog().isShowing()) {
                utils.getDialog().dismiss();
            }
        }
    }

    private void goToActivity() {
        if (SashidoHelper.isLogged()) {
            Log.e("debug", "goToActivity");
            // SharedPreferencesManager.setBoolean(mContext, WARNING_DISABLED, false);
            try {
                boolean b = ParseUser.getCurrentUser().getString(HOME_PARK_KEY).equals(NOT_SELECTED);
                Intent intent = new Intent(mContext, DashboardActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("LoginActivity", e.getLocalizedMessage());
                SashidoHelper.logOut();
                initAnimation();
                // sendGraphRequest(mContext);
            }
        } else {
            switch (type) {
                case FIRST_RUN:
                    initAnimation();
                    break;
                case NORMAL_RUN:
                    initAnimation();
                    break;
                case UPGRADED_RUN:
                    login_va.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
