/****************************************************************************
 * Copyright 2010 kraigs.android@gmail.com
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ****************************************************************************/

package io.github.carlorodriguez.alarmon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * This is the activity responsible for alerting the user when an alarm goes
 * off.  It is the activity triggered by the NotificationService.  It assumes
 * that the intent sender has acquired a screen wake lock.
 * NOTE: This class assumes that it will never be instantiated nor active
 * more than once at the same time. (ie, it assumes
 * android:launchMode="singleInstance" is set in the manifest file).
 */
public final class ActivityAlarmNotification extends AppCompatActivity {

    public static final String TIMEOUT_COMMAND = "timeout";

    public static final int TIMEOUT = 0;

    private NotificationServiceBinder notifyService;
    private DbAccessor db;
    private Handler handler;
    private Context context = this;

    // Dialog state
    int snoozeMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        // Make sure this window always shows over the lock screen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        db = new DbAccessor(getApplicationContext());

        // Start the notification service and bind to it.
        notifyService = new NotificationServiceBinder(getApplicationContext());
        notifyService.bind();

        redraw();

        TextView dc = (TextView) findViewById(R.id.toolbar_title);
        setCustomFont(this, "fonts/alarm_clock.ttf", dc);

        final FrameLayout snoozeButton = (FrameLayout) findViewById(R.id.notify_snooze);
        snoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyService.acknowledgeCurrentNotification(snoozeMinutes);
                finish();
            }
        });

        final Slider dismiss = (Slider) findViewById(R.id.dismiss_slider);
        final TextView tray = (TextView) dismiss.findViewById(R.id.tray);
        dismiss.setOnCompleteListener(new Slider.OnCompleteListener() {
            @Override
            public void complete() {
                if (tray.getText().toString().equals(context.getResources().getString(R.string.slide_to_view))) {
                    notifyService.acknowledgeCurrentNotification(0);
                    tray.setText(context.getString(R.string.slide_to_close));
                    animateSlider(dismiss);
                    fadeOut(findViewById(R.id.background_fade));
                    fadeOut(findViewById(R.id.snooze_container));
                    fadeOut(findViewById(R.id.toolbar_title));
                } else {
                    finish();
                }
            }
        });
    }

    private void fadeOut(final View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(1500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fadeOut);
    }

    private void animateSlider(Slider dismiss) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels / 2;
        dismiss.animate().translationY(height - dismiss.getHeight()).setDuration(1500).start();
    }

    public boolean setCustomFont(Context ctx, String asset, TextView dc) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e("textView", "Could not get typeface", e);
            return false;
        }
        dc.setTypeface(tf);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        redraw();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeConnections();
        notifyService.unbind();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Bundle extras = intent.getExtras();

        if (extras == null || !extras.getBoolean(TIMEOUT_COMMAND, false)) {
            return;
        }
        // The notification service has signaled this activity for a second time.
        // This represents a acknowledgment timeout.  Display the appropriate error.
        // (which also finish()es this activity.
        showDialogFragment(TIMEOUT);
    }

    private void redraw() {
        notifyService.call(new NotificationServiceBinder.ServiceCallback() {
            @Override
            public void run(NotificationServiceInterface service) {
                long alarmId;
                try {
                    alarmId = service.currentAlarmId();
                } catch (RemoteException e) {
                    return;
                }

                if (snoozeMinutes == 0) {
                    snoozeMinutes = db.readAlarmSettings(alarmId).
                            getSnoozeMinutes();
                    // Setup individual UI elements.
                    TextView snoozeMinutesTV = (TextView) findViewById(R.id.snooze_minutes);
                    snoozeMinutesTV.setText(String.valueOf(snoozeMinutes));
                }
            }
        });
    }

    private void showDialogFragment(int id) {
        DialogFragment dialog = new ActivityDialogFragment().newInstance(
                id);
        dialog.show(getFragmentManager(), "ActivityDialogFragment");
    }

    public static class ActivityDialogFragment extends DialogFragment {

        public ActivityDialogFragment newInstance(int id) {
            ActivityDialogFragment fragment = new ActivityDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            switch (getArguments().getInt("id")) {
                case TIMEOUT:
                    final AlertDialog.Builder timeoutBuilder =
                            new AlertDialog.Builder(getActivity());
                    timeoutBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                    timeoutBuilder.setTitle(R.string.time_out_title);
                    timeoutBuilder.setMessage(R.string.time_out_error);
                    timeoutBuilder.setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    return timeoutBuilder.create();
                default:
                    return super.onCreateDialog(savedInstanceState);
            }
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            getActivity().finish();
        }
    }

}
