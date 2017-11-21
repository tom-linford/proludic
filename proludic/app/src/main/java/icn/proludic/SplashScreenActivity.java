package icn.proludic;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewAnimator;

import com.parse.ParseException;
import com.parse.ParseQuery;

import icn.proludic.misc.Utils;

import static icn.proludic.misc.Constants.FIRST_RUN;
import static icn.proludic.misc.Constants.FIVE_SECONDS;
import static icn.proludic.misc.Constants.INTRODUCTION_VIDEO;
import static icn.proludic.misc.Constants.NORMAL_RUN;
import static icn.proludic.misc.Constants.ONE_SECOND;
import static icn.proludic.misc.Constants.ONE_SEVEN_FIVE_SECONDS;
import static icn.proludic.misc.Constants.RUN_TYPE;
import static icn.proludic.misc.Constants.STANDARD_RUN;
import static icn.proludic.misc.Constants.UPGRADED_RUN;

/**
 * Author:  Bradley Wilson
 * Date: 13/04/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class SplashScreenActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private ViewAnimator vaSplash;
    private int countSplash = 0;
    private Context mContext = this;
    private String type;
    private TextView splashWelcomeTitle, splashWelcomeDescription;
    private String uriPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initViews();
        initCarousel();
        detemineWhereToGo();
    }

    private void initViews() {
        vaSplash = (ViewAnimator) findViewById(R.id.splash_view_animator);
        vaSplash.setInAnimation(this, R.anim.slide_in_left);
        vaSplash.setOutAnimation(this, R.anim.slide_out_right);
        splashWelcomeTitle = (TextView) findViewById(R.id.splash_screen_update_title);
        splashWelcomeDescription = (TextView) findViewById(R.id.splash_screen_update_description);

    }

    private void detemineWhereToGo() {
        type = Application.checkFirstRun(mContext);
        switch (type) {
            case FIRST_RUN:
                splashWelcomeTitle.setText(mContext.getString(R.string.welcome));
                splashWelcomeDescription.setText(mContext.getString(R.string.first_run_description));
                navigateAccordingly(INTRODUCTION_VIDEO);
                break;
            case UPGRADED_RUN:
                splashWelcomeTitle.setText(mContext.getString(R.string.welcomeBack));
                splashWelcomeDescription.setText(mContext.getString(R.string.upgraded_run_description));
                navigateAccordingly(STANDARD_RUN);
                break;
            case NORMAL_RUN:
                splashWelcomeTitle.setText(mContext.getString(R.string.welcomeBack));
                splashWelcomeDescription.setText(mContext.getString(R.string.normal_run_description));
                navigateAccordingly(STANDARD_RUN);
                break;
        }
    }

    private void navigateAccordingly(final String navType) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (navType) {
                    case INTRODUCTION_VIDEO:
                        try {
                            goToIntroductionVideo();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case STANDARD_RUN:
                    default:
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        intent.putExtra(RUN_TYPE, type);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        }, FIVE_SECONDS);
    }

    private void goToIntroductionVideo() throws ParseException {
        //goes to Introduction Video based on your language locality
        final Dialog dialog = new Dialog(SplashScreenActivity.this);// add here your class name
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_intro_video);//add your own xml with defied with and height of videoview
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        final VideoView mVideoView = (VideoView) dialog.findViewById(R.id.introVideo);
        uriPath = ParseQuery.getQuery("Extras").getFirst().getString("IntroductionVideo");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Log.v("Vidoe-URI", uriPath+ "");
        mVideoView.setVideoURI(Uri.parse(uriPath));
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.e("position", String.valueOf(mVideoView.getCurrentPosition()));
                        if (mVideoView.getCurrentPosition() >= 30000) {    // set to 30000
                            dialog.findViewById(R.id.tap_to_skip).setVisibility(View.VISIBLE);
                            dialog.findViewById(R.id.tap_to_skip).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils utils = new Utils(SplashScreenActivity.this);
                                    utils.makeText(getString(R.string.skipping), Toast.LENGTH_LONG);
                                    navigateAccordingly(STANDARD_RUN);
                                }
                            });
                            handler.removeCallbacks(this);
                        } else {
                            handler.postDelayed(this, ONE_SECOND);
                        }
                    }
                };
                handler.postDelayed(runnable, ONE_SECOND);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                navigateAccordingly(STANDARD_RUN);
            }
        });

    }

    private void initCarousel() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (countSplash == 0) {
                    vaSplash.setDisplayedChild(1);
                    countSplash = 1;
                } else {
                    vaSplash.setDisplayedChild(0);
                    countSplash = 0;
                }
                handler.postDelayed(this, ONE_SEVEN_FIVE_SECONDS);
            }
        }, ONE_SEVEN_FIVE_SECONDS);
    }
}
