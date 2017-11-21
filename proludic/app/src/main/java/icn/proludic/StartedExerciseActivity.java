package icn.proludic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import icn.proludic.misc.Constants;
import icn.proludic.misc.DatabaseHandler;
import icn.proludic.misc.SashidoHelper;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.misc.Stopwatch;
import icn.proludic.misc.Utils;
import icn.proludic.models.Fitness;

import static android.widget.Toast.LENGTH_LONG;
import static icn.proludic.misc.Constants.ACHIEVEMENTS_CLASS;
import static icn.proludic.misc.Constants.AFRICAN_ELEPHANT_COL;
import static icn.proludic.misc.Constants.AFRICAN_ELEPHANT_WEIGHT;
import static icn.proludic.misc.Constants.ASIAN_ELEPHANT_COL;
import static icn.proludic.misc.Constants.ASIAN_ELEPHANT_WEIGHT;
import static icn.proludic.misc.Constants.ASIAN_GUAR_COL;
import static icn.proludic.misc.Constants.ASIAN_GUAR_WEIGHT;
import static icn.proludic.misc.Constants.BODYWEIGHT_BRONZE_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_COPPER_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_DIAMOND_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_GOLD_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_PLATINUM_COL;
import static icn.proludic.misc.Constants.BODYWEIGHT_SILVER_COL;
import static icn.proludic.misc.Constants.BURJ_KHALIFA_COL;
import static icn.proludic.misc.Constants.BURJ_KHALIFA_WEIGHT;
import static icn.proludic.misc.Constants.CROCODILE_COL;
import static icn.proludic.misc.Constants.CROCODILE_WEIGHT;
import static icn.proludic.misc.Constants.EIFFEL_TOWER_COL;
import static icn.proludic.misc.Constants.EIFFEL_TOWER_WEIGHT;
import static icn.proludic.misc.Constants.EMPIRE_STATE_BUILDING_COL;
import static icn.proludic.misc.Constants.EMPIRE_STATE_BUILDING_WEIGHT;
import static icn.proludic.misc.Constants.EXERCISES_CLASS_NAME;
import static icn.proludic.misc.Constants.EXERCISE_AVERAGE_REPS;
import static icn.proludic.misc.Constants.EXERCISE_AVERAGE_TIME;
import static icn.proludic.misc.Constants.EXERCISE_IS_WEIGHT;
import static icn.proludic.misc.Constants.EXERCISE_MUSCLE_GROUP_IMAGE_KEY;
import static icn.proludic.misc.Constants.EXERCISE_NAME_KEY;
import static icn.proludic.misc.Constants.EXERCISE_NO_WEIGHT_DESCRIPTION;
import static icn.proludic.misc.Constants.EXERCISE_OF_THE_WEEK;
import static icn.proludic.misc.Constants.EXERCISE_REST_TIME;
import static icn.proludic.misc.Constants.EXERCISE_TOTAL_REPS;
import static icn.proludic.misc.Constants.EXERCISE_TOTAL_SETS;
import static icn.proludic.misc.Constants.EXERCISE_TOTAL_WEIGHT;
import static icn.proludic.misc.Constants.EXERCISE_WEIGHT;
import static icn.proludic.misc.Constants.EXTRAS_CLASS_NAME;
import static icn.proludic.misc.Constants.GETTING_STARTED_COL;
import static icn.proludic.misc.Constants.GIRAFFE_COL;
import static icn.proludic.misc.Constants.GIRAFFE_WEIGHT;
import static icn.proludic.misc.Constants.GOLDEN_GATE_BRIDGE_COL;
import static icn.proludic.misc.Constants.GOLDEN_GATE_BRIDGE_WEIGHT;
import static icn.proludic.misc.Constants.HIPPOPOTAMUS_COL;
import static icn.proludic.misc.Constants.HIPPOPOTAMUS_WEIGHT;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.KODIAK_BEAR_COL;
import static icn.proludic.misc.Constants.KODIAK_BEAR_WEIGHT;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.LOCATION_TOTAL_HEARTS;
import static icn.proludic.misc.Constants.LONDON_EYE_COL;
import static icn.proludic.misc.Constants.LONDON_EYE_WEIGHT;
import static icn.proludic.misc.Constants.ONE_SECOND;
import static icn.proludic.misc.Constants.PROLUDIC_BRONZE_COL;
import static icn.proludic.misc.Constants.PROLUDIC_COPPER_COL;
import static icn.proludic.misc.Constants.PROLUDIC_DIAMOND_COL;
import static icn.proludic.misc.Constants.PROLUDIC_GOLD_COL;
import static icn.proludic.misc.Constants.PROLUDIC_PLATINUM_COL;
import static icn.proludic.misc.Constants.PROLUDIC_SILVER_COL;
import static icn.proludic.misc.Constants.TOTAL_NON_WEIGHT_EXERCISES;
import static icn.proludic.misc.Constants.TOTAL_WEIGHT_EXERCISES;
import static icn.proludic.misc.Constants.TWO_SECONDS;
import static icn.proludic.misc.Constants.USER;
import static icn.proludic.misc.Constants.USER_ACHIEVEMENTS;
import static icn.proludic.misc.Constants.USER_ACHIEVEMENTS_CLASS;
import static icn.proludic.misc.Constants.USER_EXERCISES;
import static icn.proludic.misc.Constants.USER_HEARTS;
import static icn.proludic.misc.Constants.USER_WEIGHT;
import static icn.proludic.misc.Constants.WEEKLY_WORKER_COL;
import static icn.proludic.misc.Constants.WHALE_SHARK_COL;
import static icn.proludic.misc.Constants.WHALE_SHARK_WEIGHT;
import static icn.proludic.misc.Constants.WHITE_RHINOCEROS_COL;
import static icn.proludic.misc.Constants.WHITE_RHINOCEROS_WEIGHT;

/**
 * Author:  Bradley Wilson
 * Date: 08/05/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class StartedExerciseActivity extends AppCompatActivity {

    private TextView startExerciseButton;
    private TextView exerciseTimerTV;
    private FrameLayout timerContainer;
    private Context context = this;
    private Stopwatch stopwatch;
    private ImageView muscleGroup;
    private RelativeLayout exerciseFinishContainer;
    private Handler handler = new Handler();
    private String time = "";
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long eStopwatchMillis = stopwatch.getElapsedTimeMili();
            time = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(eStopwatchMillis),
                    TimeUnit.MILLISECONDS.toSeconds(eStopwatchMillis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eStopwatchMillis))
            );
            exerciseTimerTV.setText(time);
            handler.postDelayed(this, ONE_SECOND);
        }
    };
    private long totalTime;
    private TextView exerciseCompleteHearts;
    private Utils utils;
    private TextView totalHearts;
    private DatabaseHandler db;
    private String fitnessName;
    private boolean validExercise;
    private Fitness fitness;
    private String weight;
    private int totalWeight;
    private CoordinatorLayout parentLayout;
    private int sets;
    private long totalTimeForExercise;
    private boolean isWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exercise);
        utils = new Utils(context);
        db = new DatabaseHandler(context);
        Bundle bundle = getIntent().getExtras();
        fitnessName = bundle.getString(EXERCISE_NAME_KEY);
        TextView fitnessTV = (TextView) findViewById(R.id.start_exercise_title);
        fitnessTV.setText(fitnessName);
        LinearLayout lp = (LinearLayout) findViewById(R.id.bottom_level_container);
        isWeight = bundle.getBoolean(EXERCISE_IS_WEIGHT);
        if (bundle.getBoolean(EXERCISE_IS_WEIGHT)) {
            lp.setVisibility(View.INVISIBLE);
            TextView weight_description = (TextView) findViewById(R.id.no_weight_description);
            weight_description.setText(bundle.getString(EXERCISE_NO_WEIGHT_DESCRIPTION));
            weight_description.setVisibility(View.VISIBLE);
        }

        long averageTime = bundle.getLong(EXERCISE_AVERAGE_TIME);
        String time = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(averageTime),
                TimeUnit.MILLISECONDS.toSeconds(averageTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(averageTime))
        );

        TextView exerciseTimeAvg = (TextView) findViewById(R.id.single_exercise_time);
        exerciseTimeAvg.setText(time);

        TextView repsAvg = (TextView) findViewById(R.id.single_exercise_reps);
        repsAvg.setText(bundle.getString(EXERCISE_AVERAGE_REPS));

        long restTime = bundle.getLong(EXERCISE_REST_TIME);
        String rTime = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(restTime),
                TimeUnit.MILLISECONDS.toSeconds(restTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(restTime))
        );
        weight = bundle.getString(EXERCISE_WEIGHT);

        TextView restTimeTV = (TextView) findViewById(R.id.single_exercise_rest_time);
        restTimeTV.setText(rTime);

        TextView weightTV = (TextView) findViewById(R.id.single_exercise_weight);
        weightTV.setText(weight);

        Log.e("StartedExerciseActivity", "gets here!");
        fitness = db.getExercise(fitnessName);
        Log.e("fitness", fitness.getName() + String.valueOf(fitness.getUses()) + String.valueOf(fitness.getID()));
        totalTime = bundle.getLong(EXERCISE_AVERAGE_TIME);

        startExerciseButton = (TextView) findViewById(R.id.tap_to_start);
        startExerciseButton.setOnClickListener(customClickListener);
        stopwatch = new Stopwatch();

        exerciseTimerTV = (TextView) findViewById(R.id.start_exercise_time);
        exerciseFinishContainer = (RelativeLayout) findViewById(R.id.finish_exercise_container);

        timerContainer = (FrameLayout) findViewById(R.id.exercise_time_container);

        exerciseCompleteHearts = (TextView) findViewById(R.id.workout_complete_hearts);
        totalHearts = (TextView) findViewById(R.id.finish_exercise_hearts);

        sets = bundle.getInt(EXERCISE_TOTAL_SETS);
        long totalTimeExercising = (totalTime * sets);
        long totalTimeResting = restTime * (sets - 1);    // you don't need to rest if you've finished the workout
        totalTimeForExercise = totalTimeExercising + totalTimeResting;
        String tTime = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(totalTimeForExercise),
                TimeUnit.MILLISECONDS.toSeconds(totalTimeForExercise) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTimeForExercise))
        );

        TextView totalTimeExercisingTV = (TextView) findViewById(R.id.total_time);
        totalTimeExercisingTV.setText(tTime);

        totalWeight = bundle.getInt(EXERCISE_TOTAL_WEIGHT) * bundle.getInt(EXERCISE_TOTAL_REPS) * bundle.getInt(EXERCISE_TOTAL_SETS);
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);

        String imageUrl = bundle.getString(EXERCISE_MUSCLE_GROUP_IMAGE_KEY);

        muscleGroup = (ImageView) findViewById(R.id.img_muscle_group);
        Picasso.with(context).load(imageUrl).into(muscleGroup);

        if (!SharedPreferencesManager.getBoolean(context, Constants.WARNING_DISABLED)) {
            Utils.showCautionDialog(fitnessName, getString(R.string.exercise), context);
        }

        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent("Started Exercise");
        Map<String, String> articleParams = new HashMap<String, String>();
        //param keys and values have to be of String type
        articleParams.put("Started Exercise", ParseUser.getCurrentUser().getEmail() + " is doing " + fitnessName + ".");
        //up to 10 params can be logged with each event
        FlurryAgent.logEvent("Started Exercise", articleParams);
    }

    private View.OnClickListener customClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tap_to_start:
                    if (startExerciseButton.getText().toString().equals(context.getResources().getString(R.string.taptostart))) {
                        exerciseTimerTV.setTextColor(Color.parseColor("#00cc00"));
                        startExerciseButton.setText(R.string.taptofinish);
                        startTimer();
                    } else if (startExerciseButton.getText().toString().equals(context.getResources().getString(R.string.taptofinish))) {
                        exerciseTimerTV.setTextColor(Color.parseColor("#cc0000"));
                        startExerciseButton.setText(R.string.finishexercise);
                        stopTimer();
                        // this used to be in next 'else-if'
                        if (validExercise) {
                            fitness.setUses(fitness.getUses() + 1);
                            Log.e("uses", String.valueOf(fitness.getUses()));
                            db.updateFitness(fitness, fitnessName);
                        }
                    } else if (startExerciseButton.getText().toString().equals(context.getResources().getString(R.string.finishexercise))) {
                        finish();
                    }
                    break;
            }
        }
    };

    private void stopTimer() {
        int totalHeartsForThisExercise = 0;
        // TODO fix issue with "you won't be receiving hearts" message appearing
        Log.e("StartedExerciseActivity", "current time: " + Long.toString(stopwatch.getElapsedTimeMili()));
        if (stopwatch.getElapsedTimeMili() > totalTimeForExercise / 4) {
            validExercise = true;
            try {
                ParseObject eotw = ParseQuery.getQuery(EXTRAS_CLASS_NAME).getFirst().getParseObject(EXERCISE_OF_THE_WEEK);
                if (eotw.fetchIfNeeded().getString(EXERCISE_NAME_KEY).toLowerCase().equals(fitness.getName().toLowerCase())) {
                    totalHeartsForThisExercise = 500;
                    SashidoHelper.earnAchievement(WEEKLY_WORKER_COL, getString(R.string.weekly_worker), context, parentLayout);
                } else {
                    totalHeartsForThisExercise = 300;
                }
                exerciseCompleteHearts.setText(String.valueOf(totalHeartsForThisExercise));
                totalHearts.setText(String.valueOf(totalHeartsForThisExercise));
                saveHearts(totalHeartsForThisExercise);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            TextView dontAwardHearts = (TextView) findViewById(R.id.dont_award_hearts);
            dontAwardHearts.setVisibility(View.VISIBLE);
            exerciseCompleteHearts.setText(String.valueOf(0));
            totalHearts.setText(String.valueOf(totalHeartsForThisExercise));
            validExercise = false;
        }
        sortTime();
    }

    private void saveHearts(final int totalNumber) throws ParseException {
        int totalHeartsForThisExercise = ParseUser.getCurrentUser().getInt(USER_HEARTS) + totalNumber;
        int totalExercises = ParseUser.getCurrentUser().getInt(USER_EXERCISES);
        Log.e("totalHearts", String.valueOf(totalHeartsForThisExercise));
        totalWeight = ParseUser.getCurrentUser().getInt(EXERCISE_TOTAL_WEIGHT) + totalWeight;
        ParseUser.getCurrentUser().put(USER_HEARTS, totalHeartsForThisExercise);
        ParseUser.getCurrentUser().put(EXERCISE_TOTAL_WEIGHT, totalWeight);
        ParseUser.getCurrentUser().put(USER_EXERCISES, totalExercises + 1);
        if (isWeight) {
            int totalNonWeightExercises = ParseUser.getCurrentUser().getInt(TOTAL_NON_WEIGHT_EXERCISES);
            ParseUser.getCurrentUser().put(TOTAL_NON_WEIGHT_EXERCISES, totalNonWeightExercises + 1);
        } else {
            int totalWeightExercises = ParseUser.getCurrentUser().getInt(TOTAL_WEIGHT_EXERCISES);
            ParseUser.getCurrentUser().put(TOTAL_WEIGHT_EXERCISES, totalWeightExercises + 1);
        }
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                checkAchievements();
            }
        });

        String homePark = ParseUser.getCurrentUser().getString(HOME_PARK_KEY);
        ParseObject homeParkObj = ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, homePark);
        int currentHearts = homeParkObj.fetchIfNeeded().getInt(LOCATION_TOTAL_HEARTS) + totalNumber;
        homeParkObj.put(LOCATION_TOTAL_HEARTS, currentHearts);
        homeParkObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseQuery<ParseObject> getId = ParseQuery.getQuery(EXERCISES_CLASS_NAME);
                getId.whereEqualTo(EXERCISE_NAME_KEY, fitnessName);
                getId.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            ArrayList<String> exerciseIds = new ArrayList<>();
                            exerciseIds.add(object.getObjectId());
                            utils.addedToTrackedEvents(totalNumber, exerciseIds);
                        } else {
                            Log.e("debug", "failed to add to tracked events: " + e.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }

    private final ArrayList<Integer> weightHearts = new ArrayList<>();

    private void checkAchievements() {
        Log.e(StartedExerciseActivity.class.getSimpleName(), "gets into weight achievements");
        ParseQuery<ParseObject> aQuery = ParseQuery.getQuery(ACHIEVEMENTS_CLASS);
        // added this
        aQuery.whereEqualTo("isFrench", SharedPreferencesManager.getString(context, "locale").equals("fr"));
        aQuery.orderByAscending(EXERCISE_WEIGHT);
        aQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (ParseObject j : objects) {
                    weightHearts.add(j.getInt("HeartsReceived"));
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_ACHIEVEMENTS_CLASS);
                final List<String> achievementsList = new ArrayList<>();
                query.whereEqualTo(USER, ParseUser.getCurrentUser());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            for (ParseObject j : objects) {
                                final int[] totalHearts = {0};
                                if (totalWeight > KODIAK_BEAR_WEIGHT) {
                                    if (!j.getBoolean(KODIAK_BEAR_COL)) {
                                        achievementsList.add(getString(R.string.kodiak_bear));
                                        j.put(KODIAK_BEAR_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(22);
                                        addFlurryAchievement(getString(R.string.kodiak_bear));
                                    }
                                }
                                if (totalWeight > CROCODILE_WEIGHT) {
                                    if (!j.getBoolean(CROCODILE_COL)) {
                                        j.put(CROCODILE_COL, true);
                                        achievementsList.add(getString(R.string.crocodile));
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(23);
                                        addFlurryAchievement(getString(R.string.crocodile));
                                    }
                                }
                                if (totalWeight > ASIAN_GUAR_WEIGHT) {
                                    if (!j.getBoolean(ASIAN_GUAR_COL)) {
                                        achievementsList.add(getString(R.string.asian_guar));
                                        j.put(ASIAN_GUAR_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(24);
                                        addFlurryAchievement(getString(R.string.asian_guar));
                                    }
                                }
                                if (totalWeight > GIRAFFE_WEIGHT) {
                                    if (!j.getBoolean(GIRAFFE_COL)) {
                                        achievementsList.add(getString(R.string.giraffe));
                                        j.put(GIRAFFE_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(25);
                                        addFlurryAchievement(getString(R.string.giraffe));
                                    }
                                }
                                if (totalWeight > HIPPOPOTAMUS_WEIGHT) {
                                    if (!j.getBoolean(HIPPOPOTAMUS_COL)) {
                                        achievementsList.add(getString(R.string.hippopotamus));
                                        j.put(HIPPOPOTAMUS_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(26);
                                        addFlurryAchievement(getString(R.string.hippopotamus));
                                    }
                                }
                                if (totalWeight > WHITE_RHINOCEROS_WEIGHT) {
                                    if (!j.getBoolean(WHITE_RHINOCEROS_COL)) {
                                        achievementsList.add(getString(R.string.white_rhinoceros));
                                        j.put(WHITE_RHINOCEROS_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(27);
                                        addFlurryAchievement(getString(R.string.white_rhinoceros));
                                    }
                                }
                                if (totalWeight > ASIAN_ELEPHANT_WEIGHT) {
                                    if (!j.getBoolean(ASIAN_ELEPHANT_COL)) {
                                        achievementsList.add(getString(R.string.asian_elephant));
                                        j.put(ASIAN_ELEPHANT_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(28);
                                        addFlurryAchievement(getString(R.string.asian_elephant));
                                    }
                                }
                                if (totalWeight > AFRICAN_ELEPHANT_WEIGHT) {
                                    if (!j.getBoolean(AFRICAN_ELEPHANT_COL)) {
                                        achievementsList.add(getString(R.string.african_elephant));
                                        j.put(AFRICAN_ELEPHANT_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(29);
                                        addFlurryAchievement(getString(R.string.african_elephant));
                                    }
                                }
                                if (totalWeight > WHALE_SHARK_WEIGHT) {
                                    if (!j.getBoolean(WHALE_SHARK_COL)) {
                                        achievementsList.add(getString(R.string.whale_shark));
                                        j.put(WHALE_SHARK_COL, true);
                                        totalHearts[0] = totalHearts[0] + weightHearts.get(30);
                                        addFlurryAchievement(getString(R.string.whale_shark));
                                    }
                                }
                                if (!j.getBoolean(GETTING_STARTED_COL)) {
                                    achievementsList.add(getString(R.string.getting_started));
                                    j.put(GETTING_STARTED_COL, true);
                                    Log.e("achievement", "getting started");
                                    totalHearts[0] += weightHearts.get(20);
                                    addFlurryAchievement(getString(R.string.getting_started));
                                }
                                if (isWeight && j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 10) {
                                    if (!j.getBoolean(BODYWEIGHT_COPPER_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_copper));
                                        j.put(BODYWEIGHT_COPPER_COL, true);
                                        totalHearts[0] += weightHearts.get(5);
                                        addFlurryAchievement(getString(R.string.bodyweight_copper));
                                    }
                                }
                                if (isWeight && j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 25) {
                                    if (!j.getBoolean(BODYWEIGHT_BRONZE_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_bronze));
                                        j.put(BODYWEIGHT_BRONZE_COL, true);
                                        totalHearts[0] += weightHearts.get(4);
                                        addFlurryAchievement(getString(R.string.bodyweight_bronze));
                                    }
                                }
                                if (isWeight && j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 50) {
                                    if (!j.getBoolean(BODYWEIGHT_SILVER_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_silver));
                                        j.put(BODYWEIGHT_SILVER_COL, true);
                                        totalHearts[0] += weightHearts.get(3);
                                        addFlurryAchievement(getString(R.string.bodyweight_silver));
                                    }
                                }
                                if (isWeight && j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 100) {
                                    if (!j.getBoolean(BODYWEIGHT_GOLD_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_gold));
                                        j.put(BODYWEIGHT_GOLD_COL, true);
                                        totalHearts[0] += weightHearts.get(2);
                                        addFlurryAchievement(getString(R.string.bodyweight_gold));
                                    }
                                }
                                if (isWeight && j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 150) {
                                    if (!j.getBoolean(BODYWEIGHT_PLATINUM_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_platinum));
                                        j.put(BODYWEIGHT_PLATINUM_COL, true);
                                        totalHearts[0] += weightHearts.get(1);
                                        addFlurryAchievement(getString(R.string.bodyweight_platinum));
                                    }
                                }
                                if (isWeight && j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 200) {
                                    if (!j.getBoolean(BODYWEIGHT_DIAMOND_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_diamond));
                                        j.put(BODYWEIGHT_DIAMOND_COL, true);
                                        totalHearts[0] += weightHearts.get(0);
                                        addFlurryAchievement(getString(R.string.bodyweight_diamond));
                                    }
                                }
                                if ((!isWeight) && j.getInt(TOTAL_WEIGHT_EXERCISES) >= 25) {
                                    if (!j.getBoolean(PROLUDIC_COPPER_COL)) {
                                        achievementsList.add(getString(R.string.proludic_copper));
                                        j.put(PROLUDIC_COPPER_COL, true);
                                        totalHearts[0] += weightHearts.get(11);
                                        addFlurryAchievement(getString(R.string.proludic_copper));
                                    }
                                }
                                if ((!isWeight) && j.getInt(TOTAL_WEIGHT_EXERCISES) >= 50) {
                                    if (!j.getBoolean(PROLUDIC_BRONZE_COL)) {
                                        achievementsList.add(getString(R.string.proludic_bronze));
                                        j.put(PROLUDIC_BRONZE_COL, true);
                                        totalHearts[0] += weightHearts.get(10);
                                        addFlurryAchievement(getString(R.string.proludic_bronze));
                                    }
                                }
                                if ((!isWeight) && j.getInt(TOTAL_WEIGHT_EXERCISES) >= 75) {
                                    if (!j.getBoolean(PROLUDIC_SILVER_COL)) {
                                        achievementsList.add(getString(R.string.proludic_silver));
                                        j.put(PROLUDIC_SILVER_COL, true);
                                        totalHearts[0] += weightHearts.get(9);
                                        addFlurryAchievement(getString(R.string.proludic_silver));
                                    }
                                }
                                if ((!isWeight) && j.getInt(TOTAL_WEIGHT_EXERCISES) >= 100) {
                                    if (!j.getBoolean(PROLUDIC_GOLD_COL)) {
                                        achievementsList.add(getString(R.string.proludic_gold));
                                        j.put(PROLUDIC_GOLD_COL, true);
                                        totalHearts[0] += weightHearts.get(8);
                                        addFlurryAchievement(getString(R.string.proludic_gold));
                                    }
                                }
                                if ((!isWeight) && j.getInt(TOTAL_WEIGHT_EXERCISES) >= 150) {
                                    if (!j.getBoolean(PROLUDIC_PLATINUM_COL)) {
                                        achievementsList.add(getString(R.string.proludic_platinum));
                                        j.put(PROLUDIC_PLATINUM_COL, true);
                                        totalHearts[0] += weightHearts.get(7);
                                        addFlurryAchievement(getString(R.string.proludic_platinum));
                                    }
                                }
                                if ((!isWeight) && j.getInt(TOTAL_WEIGHT_EXERCISES) >= 200) {
                                    if (!j.getBoolean(PROLUDIC_DIAMOND_COL)) {
                                        achievementsList.add(getString(R.string.proludic_diamond));
                                        j.put(PROLUDIC_DIAMOND_COL, true);
                                        totalHearts[0] += weightHearts.get(6);
                                        addFlurryAchievement(getString(R.string.proludic_diamond));
                                    }
                                }
                                checkCommunityAchievements(getTotalParkWeight());

                                j.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (achievementsList.size() > 0) {
                                            showAchievementSnackbar(context.getResources().getQuantityString(R.plurals.achievementsPlural, achievementsList.size(), achievementsList.size()), totalHearts[0]);
                                            int totalAchievements = ParseUser.getCurrentUser().getInt(USER_ACHIEVEMENTS);
                                            totalAchievements = totalAchievements + achievementsList.size();
                                            ParseUser.getCurrentUser().put(USER_ACHIEVEMENTS, totalAchievements);
                                            totalHearts[0] = totalHearts[0] + ParseUser.getCurrentUser().getInt(USER_HEARTS);
                                            ParseUser.getCurrentUser().put(USER_HEARTS, totalHearts[0]);
                                            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
                                                        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getString(HOME_PARK_KEY));
                                                        query.findInBackground(new FindCallback<ParseObject>() {
                                                            @Override
                                                            public void done(List<ParseObject> objects, ParseException e) {
                                                                if (e == null) {
                                                                    int totalAchievements = objects.get(0).getInt(USER_ACHIEVEMENTS);
                                                                    totalAchievements = totalAchievements + achievementsList.size();
                                                                    objects.get(0).put(USER_ACHIEVEMENTS, totalAchievements);
                                                                    objects.get(0).saveInBackground();
                                                                } else {
                                                                    Log.e("failed", "failed" + e.getLocalizedMessage());
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        Log.e("Failed", "failed" + e.getLocalizedMessage());
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        } else {
                            Log.e("failed", "failed" + e.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }

    private void addFlurryAchievement(String achievement) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent("Achievement Earned");
        Map<String, String> articleParams = new HashMap<String, String>();
        //param keys and values have to be of String type
        articleParams.put("Achievement Earned", ParseUser.getCurrentUser().getEmail() + " has earned the '" + achievement + "' achievement.");
        //up to 10 params can be logged with each event
        FlurryAgent.logEvent("Achievement Earned", articleParams);
    }

    private void addFlurryCommunityAchievement(String achievement) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent("Community Achievement Earned");
        Map<String, String> articleParams = new HashMap<String, String>();
        //param keys and values have to be of String type
        articleParams.put("Community Achievement Earned", "Park " + ParseUser.getCurrentUser().get(HOME_PARK_KEY) + " has earned the '" + achievement + "' achievement.");
        //up to 10 params can be logged with each event
        FlurryAgent.logEvent("Community Achievement Earned", articleParams);
    }

    // gets all users at the current user's home park
    private List<ParseObject> getAllParkUsers() {
        String park = ParseUser.getCurrentUser().getString(HOME_PARK_KEY);
        ParseQuery<ParseObject> getAllParkUsers = ParseQuery.getQuery(USER);
        getAllParkUsers.whereEqualTo(HOME_PARK_KEY, park);
        try {
            return getAllParkUsers.find();
        } catch (ParseException e) {
            Log.e("debug", e.getLocalizedMessage());
            return null;
        }
    }

    // gets the total weight of all users at the park
    private int getTotalParkWeight() {
        List<ParseObject> users = getAllParkUsers();
        if (users == null)
            return -1;

        int totalParkWeight = 0;
        for (ParseObject user : users) {
            totalParkWeight += user.getInt(USER_WEIGHT);
        }
        Log.e("debug", Integer.toString(totalParkWeight));
        return totalParkWeight;
    }

    // uses the total weight to check if community has earned an achievement
    private void checkCommunityAchievements(int totalParkWeight) {
        if (totalParkWeight == -1) {
            Toast.makeText(context, R.string.error_updating_comm, Toast.LENGTH_SHORT).show();
            return;
        }
        if (totalParkWeight >= LONDON_EYE_WEIGHT) {
            awardCommunityAchievement(LONDON_EYE_COL, weightHearts.get(31));
            addFlurryCommunityAchievement(getString(R.string.london_eye));
        }
        if (totalParkWeight >= EIFFEL_TOWER_WEIGHT) {
            awardCommunityAchievement(EIFFEL_TOWER_COL, weightHearts.get(32));
            addFlurryCommunityAchievement(getString(R.string.eiffel_tower));
        }
        if (totalParkWeight >= EMPIRE_STATE_BUILDING_WEIGHT) {
            awardCommunityAchievement(EMPIRE_STATE_BUILDING_COL, weightHearts.get(33));
            addFlurryCommunityAchievement(getString(R.string.empire_state_building));
        }
        if (totalParkWeight >= BURJ_KHALIFA_WEIGHT) {
            awardCommunityAchievement(BURJ_KHALIFA_COL, weightHearts.get(34));
            addFlurryCommunityAchievement(getString(R.string.burj_khalifa));
        }
        if (totalParkWeight >= GOLDEN_GATE_BRIDGE_WEIGHT) {
            awardCommunityAchievement(GOLDEN_GATE_BRIDGE_COL, weightHearts.get(35));
            addFlurryCommunityAchievement(getString(R.string.golden_gate_bridge));
        }
    }

    // awards each eligible member of the park the achievement
    private void awardCommunityAchievement(final String col, final int earnedHearts) {
        final List<ParseObject> parkUsers = getAllParkUsers();
        ParseQuery<ParseObject> achievements = ParseQuery.getQuery(USER_ACHIEVEMENTS_CLASS);
        achievements.whereContainedIn("User", parkUsers);
        achievements.whereEqualTo(col, false);
        ArrayList<ParseObject> usersToAward = new ArrayList<>();
        try {
            List<ParseObject> rows = achievements.find();
            if (rows.size() == 0)
                return;
            // set column to 'true' in UserAchievements
            for (ParseObject row : rows) {
                row.put(col, true);
                row.saveInBackground();
                usersToAward.add(row.getParseObject(USER));
            }
            // update user hearts and achievements for eligible users
            for (ParseObject user : usersToAward) {
                int hearts = user.getInt(USER_HEARTS) + earnedHearts;
                int achievementsTotal = user.getInt(USER_ACHIEVEMENTS) + 1;
                user.put(USER_HEARTS, hearts);
                user.put(USER_ACHIEVEMENTS, achievementsTotal);
                user.saveInBackground();
            }
            updateParkStats(earnedHearts * usersToAward.size(), usersToAward.size());
        } catch (ParseException e) {
            Log.e("debug", e.getLocalizedMessage());
            Toast.makeText(context, R.string.error_updating_comm, Toast.LENGTH_SHORT).show();
        }
    }

    // update the park's total achievements and hearts
    private void updateParkStats(final int parkEarnedHearts, final int totalAchievements) {
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
    
    private void showAchievementSnackbar(String string, int totalHearts) {
        final Snackbar snackBar = Snackbar.make(parentLayout, string, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(context.getResources().getString(R.string.okay), new View.OnClickListener() {
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


    private void sortTime() {
        setTime(time);
        handler.removeCallbacks(runnable);
        // timerContainer.setVisibility(View.GONE);
        muscleGroup.setVisibility(View.GONE);
        exerciseFinishContainer.setVisibility(View.VISIBLE);
        /*exerciseTimerTV = (TextView) findViewById(R.id.finish_exercise_time);
        if (time.equals("")) {
            exerciseTimerTV.setText("00:00");
        } else {
            exerciseTimerTV.setText(time);
        }*/
        stopwatch.stop();
    }

    private void startTimer() {
        stopwatch.start();
        handler.postDelayed(runnable, ONE_SECOND);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    private long mBackPressed;
    @Override
    public void onBackPressed() {
        if (mBackPressed + TWO_SECONDS > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            utils.makeText(getString(R.string.press_back_exercise), LENGTH_LONG);
        }
        mBackPressed = System.currentTimeMillis();
    }
}
