package icn.proludic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import icn.proludic.adapters.RecyclerViewExerciseListAdapter;
import icn.proludic.misc.Constants;
import icn.proludic.misc.DatabaseHandler;
import icn.proludic.misc.SashidoHelper;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.misc.Stopwatch;
import icn.proludic.misc.Utils;
import icn.proludic.models.ExerciseListModel;
import icn.proludic.models.Fitness;

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
import static icn.proludic.misc.Constants.EXERCISE_LIST;
import static icn.proludic.misc.Constants.EXERCISE_NAME_KEY;
import static icn.proludic.misc.Constants.EXERCISE_OF_THE_WEEK;
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
import static icn.proludic.misc.Constants.TOTAL_EXERCISES;
import static icn.proludic.misc.Constants.TOTAL_NON_WEIGHT_EXERCISES;
import static icn.proludic.misc.Constants.TOTAL_WEIGHT_EXERCISES;
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
import static icn.proludic.misc.Constants.WORKING_OUT_COL;
import static icn.proludic.misc.Constants.WORKOUT_NAME_KEY;

/**
 * Author:  Bradley Wilson
 * Date: 18/05/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class StartedWorkoutActivity extends AppCompatActivity {

    private Handler handler = new Handler(), eHandler = new Handler();
    private TextView workoutTimerTV, brandName;
    private FrameLayout timerContainer;
    private ImageView brandImage;
    private Stopwatch stopwatch;
    private String time, eTime;
    int count = 0;
    private Runnable mainRunnable = new Runnable() {
        @Override
        public void run() {
            setCurrentTime(workoutTimerTV);
            handler.postDelayed(this, ONE_SECOND);
        }
    };

    private Runnable exerciseRunnable = new Runnable() {
        @Override
        public void run() {
            if (count == 0) {
                setCurrentETime((TextView) recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.exercise_time));
            } else {
                if (count < totalExercises) {
                    setCurrentETime((TextView) recyclerView.findViewHolderForAdapterPosition(count).itemView.findViewById(R.id.exercise_time));
                }
            }
            handler.postDelayed(this, ONE_SECOND);
        }
    };
    private TextView startWorkoutButton;
    private RecyclerView recyclerView;
    private int totalExercises;
    private Stopwatch eStopwatch;
    private boolean changing = false;
    private ArrayList<ExerciseListModel> exerciseList;
    private List<String> achievementsList;
    private TextView heartCounter;
    private TextView exerciseTime;
    private Utils utils;
    private DatabaseHandler db;
    private Fitness fitness;
    private String workoutName;
    private CoordinatorLayout parentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);
        parentLayout = (CoordinatorLayout) findViewById(R.id.parentLayout);
        Bundle bundle = getIntent().getExtras();
        workoutName = bundle.getString(WORKOUT_NAME_KEY);

        utils = new Utils(context);
        db = new DatabaseHandler(context);
        List<Fitness> workouts = db.getAllExercises(true);
        fitness = db.getExercise(workoutName);
        exerciseList = (ArrayList<ExerciseListModel>) getIntent().getSerializableExtra(EXERCISE_LIST);
        totalExercises = bundle.getInt(TOTAL_EXERCISES);
        recyclerView = (RecyclerView) findViewById(R.id.workout_exercise_list_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(eLayoutManager);

        heartCounter = (TextView) findViewById(R.id.total_hearts);
        exerciseTime = (TextView) findViewById(R.id.single_exercise_in_workout_time);

        RecyclerViewExerciseListAdapter adapter = new RecyclerViewExerciseListAdapter(this, exerciseList, false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        stopwatch = new Stopwatch();
        eStopwatch = new Stopwatch();

        startWorkoutButton = (TextView) findViewById(R.id.tap_to_start);
        startWorkoutButton.setOnClickListener(customListener);
        TextView workoutNameTV = (TextView) findViewById(R.id.start_workout_title);
        workoutNameTV.setText(workoutName);
        workoutTimerTV = (TextView) findViewById(R.id.start_workout_time);

        timerContainer = (FrameLayout) findViewById(R.id.workout_time_container);

        if (!SharedPreferencesManager.getBoolean(context, Constants.WARNING_DISABLED)) {
            Utils.showCautionDialog(workoutName, getString(R.string.workout), context);
        }

        achievementsList = new ArrayList<>();
        // checkIfBranded();

        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent("Started Workout");
        Map<String, String> articleParams = new HashMap<String, String>();
        //param keys and values have to be of String type
        articleParams.put("Started Workout", ParseUser.getCurrentUser().getEmail() + " is doing " + workoutName + ".");
        //up to 10 params can be logged with each event
        FlurryAgent.logEvent("Started Workout", articleParams);
    }

    private Context context = StartedWorkoutActivity.this;
    private int hearts;
    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tap_to_start:
                    if (startWorkoutButton.getText().toString().equals(context.getResources().getString(R.string.taptostart))) {
                        workoutTimerTV.setTextColor(Color.parseColor("#00cc00"));
                        startWorkoutButton.setText(R.string.stopexercise);
                        recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.el_container).setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryHalf));
                        displayExerciseTime(0);
                        startMainTimer(true);
                        startMainTimer(false);
                    } else if (startWorkoutButton.getText().toString().equals(context.getResources().getString(R.string.stopexercise))) {
                        exerciseList.get(count).setFinalTime(eStopwatch.getElapsedTimeMili());
                        workoutTimerTV.setTextColor(Color.parseColor("#cc0000"));
                        pauseMainTimer();
                        Log.e("debug", Long.toString(eStopwatch.getElapsedTimeMili()));
                        stopMainTimer(false);
                        count++;
                        if (count <= totalExercises) {
                            startWorkoutButton.setText(context.getResources().getString(R.string.start_exercise));
                            if (count > 0) {
                                recyclerView.findViewHolderForAdapterPosition(count - 1).itemView.findViewById(R.id.el_container).setBackgroundColor(ContextCompat.getColor(context, R.color.unselectedGrey));
                            }
                            if (count < totalExercises) {
                                recyclerView.findViewHolderForAdapterPosition(count).itemView.findViewById(R.id.el_container).setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryHalf));
                                displayExerciseTime(count);
                            }
                            int currentHearts = Integer.parseInt(heartCounter.getText().toString());
                            Log.e("validity", String.valueOf(exerciseList.get(count - 1).getTotalMillis()));
                            Log.e("validity", String.valueOf(eStopwatch.getElapsedTimeMili()));
                            if (eStopwatchMillis > exerciseList.get(count - 1).getTotalMillis() / 4) {
                                Log.e("validity", "validExercise");
                                try {
                                    ParseObject eotw = ParseQuery.getQuery(EXTRAS_CLASS_NAME).getFirst().getParseObject(EXERCISE_OF_THE_WEEK);
                                    if (eotw.fetchIfNeeded().getString(EXERCISE_NAME_KEY).toLowerCase().equals(exerciseList.get(count - 1).getExerciseName().toLowerCase())) {
                                        currentHearts = currentHearts + 500;
                                        SashidoHelper.earnAchievement(WEEKLY_WORKER_COL, getString(R.string.weekly_worker), context, parentLayout);
                                    } else {
                                        currentHearts = currentHearts + 300;
                                    }
                                    heartCounter.setText(String.valueOf(currentHearts));
                                    saveHearts(currentHearts, exerciseList.get(count - 1).getTotalWeight());
                                    Fitness eFitness = db.getExercise(exerciseList.get(count - 1).getExerciseName());
                                    eFitness.setUses(eFitness.getUses() + 1);
                                    db.updateFitness(eFitness, exerciseList.get(count - 1).getExerciseName());
                                } catch (ParseException e) {
                                    Log.e("failed", "workoutfail" + e.getLocalizedMessage());
                                }
                            } else {
                                Log.e("validity", "invalidExercise");
                            }

                            if (count == totalExercises) {
                                startWorkoutButton.setText(R.string.taptofinish);
                                int uses = fitness.getUses() + 1;
                                fitness.setUses(uses);
                                db.updateFitness(fitness, workoutName);
                            }
                            sortTime(false);
                        }
                    }  else if (startWorkoutButton.getText().toString().equals(context.getResources().getString(R.string.start_exercise))) {
                        startWorkoutButton.setText(context.getResources().getString(R.string.stopexercise));
                        workoutTimerTV.setTextColor(Color.parseColor("#00cc00"));
                        resumeMainTimer();
                        startMainTimer(false);
                    } else if (startWorkoutButton.getText().toString().equals(context.getResources().getString(R.string.taptofinish))) {
                        stopMainTimer(true);
                        stopMainTimer(false);
                        showWorkoutSummary();
                    }
                    break;
            }
        }
    };

    /*private void checkIfBranded() {
        ParseQuery<ParseObject> workout = ParseQuery.getQuery(PRESET_WORKOUT_CLASS_NAME);
        workout.whereEqualTo(WORKOUT_NAME_KEY, workoutName);
        workout.whereExists(WORKOUT_BRAND_NAME_KEY);
        workout.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    brandName = (TextView) findViewById(R.id.workout_brand_name);
                    brandImage = (ImageView) findViewById(R.id.workout_brand_image);
                    brandName.setVisibility(View.VISIBLE);
                    brandImage.setVisibility(View.VISIBLE);
                    brandName.setText(object.getString(WORKOUT_BRAND_NAME_KEY));
                    Picasso.with(context).load(object.getParseFile(WORKOUT_BRAND_IMAGE_KEY).getUrl()).transform(new CircleTransform()).into(brandImage);
                } else {
                    brandName.setVisibility(View.GONE);
                    brandImage.setVisibility(View.GONE);
                }
            }
        });
    }*/

    private void showWorkoutSummary() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_workout_summary, null);
        builder.setView(view);

        TextView tv_workoutName = view.findViewById(R.id.workout_name);
        TextView tv_timeTaken = view.findViewById(R.id.time_taken);
        TextView tv_achievementsEarned = view.findViewById(R.id.achievements_earned);
        TextView tv_heartsEarned = view.findViewById(R.id.hearts_earned);

        tv_workoutName.setText(workoutName);
        tv_timeTaken.setText(time);
        tv_heartsEarned.setText(heartCounter.getText());
        tv_achievementsEarned.setText(Integer.toString(achievementsList.size()));

        RecyclerView recyclerView = view.findViewById(R.id.workout_summary_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(eLayoutManager);

        RecyclerViewExerciseListAdapter adapter = new RecyclerViewExerciseListAdapter(this, exerciseList, true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        final AlertDialog dialog = builder.create();
        view.findViewById(R.id.btn_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog.show();
    }

    private void saveHearts(final int totalNumber, int totalWeight) throws ParseException {
        int totalHeartsForThisExercise = ParseUser.getCurrentUser().getInt(USER_HEARTS) + totalNumber;
        Log.e("totalHearts", String.valueOf(totalHeartsForThisExercise));
        int totalExercises = ParseUser.getCurrentUser().getInt(USER_EXERCISES);
        totalWeight = ParseUser.getCurrentUser().getInt(EXERCISE_TOTAL_WEIGHT) + totalWeight;
        ParseUser.getCurrentUser().put(USER_HEARTS, totalHeartsForThisExercise);
        ParseUser.getCurrentUser().put(EXERCISE_TOTAL_WEIGHT, totalWeight);
        ParseUser.getCurrentUser().put(TOTAL_EXERCISES, totalExercises + 1);
        if (totalWeight == 0) {
            int totalNonWeightExercises = ParseUser.getCurrentUser().getInt(TOTAL_NON_WEIGHT_EXERCISES);
            ParseUser.getCurrentUser().put(TOTAL_NON_WEIGHT_EXERCISES, totalNonWeightExercises + 1);
        } else {
            int totalWeightExercises = ParseUser.getCurrentUser().getInt(TOTAL_WEIGHT_EXERCISES);
            ParseUser.getCurrentUser().put(TOTAL_WEIGHT_EXERCISES, totalWeightExercises + 1);
        }
        final int finalTotalWeight = totalWeight;
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                checkAchievements(finalTotalWeight);
            }
        });

        String homePark = ParseUser.getCurrentUser().getString(HOME_PARK_KEY);
        ParseObject homeParkObj = ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, homePark);
        int currentHearts = homeParkObj.fetchIfNeeded().getInt(LOCATION_TOTAL_HEARTS) + totalNumber;
        homeParkObj.put(LOCATION_TOTAL_HEARTS, currentHearts);
        homeParkObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ArrayList<String> exerciseNames = new ArrayList<>();
                for (ExerciseListModel exercise : exerciseList) {
                    exerciseNames.add(exercise.getExerciseName());
                }
                ParseQuery<ParseObject> getIds = ParseQuery.getQuery(EXERCISES_CLASS_NAME);
                getIds.whereContainedIn(EXERCISE_NAME_KEY, exerciseNames);
                getIds.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            ArrayList<String> exerciseIds = new ArrayList<>();
                            for (ParseObject exercise : objects) {
                                exerciseIds.add(exercise.getObjectId());
                            }
                            utils.addedToTrackedEvents(totalNumber, exerciseIds);
                        } else {
                            Log.e("debug", "failed to add to tracked events: " + e.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }

    private ArrayList<Integer> weightHearts;
    private void checkAchievements(final int totalWeight) {
        Log.e(StartedExerciseActivity.class.getSimpleName(), "gets into weight achievements");
        ParseQuery<ParseObject> aQuery = ParseQuery.getQuery(ACHIEVEMENTS_CLASS);
        // added this
        aQuery.whereEqualTo("isFrench", SharedPreferencesManager.getString(context, "locale").equals("fr"));
        aQuery.orderByAscending(EXERCISE_WEIGHT);
        aQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                weightHearts = new ArrayList<>();

                for (ParseObject j : objects) {
                    weightHearts.add(j.getInt("HeartsReceived"));
                }

                ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_ACHIEVEMENTS_CLASS);
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
                                if (!j.getBoolean(WORKING_OUT_COL)) {
                                    achievementsList.add(getString(R.string.working_out));
                                    j.put(WORKING_OUT_COL, true);
                                    Log.e("achievement", "working out");
                                    totalHearts[0] += weightHearts.get(19);
                                    addFlurryAchievement(getString(R.string.working_out));
                                }
                                if (j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 10) {
                                    if (!j.getBoolean(BODYWEIGHT_COPPER_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_copper));
                                        j.put(BODYWEIGHT_COPPER_COL, true);
                                        totalHearts[0] += weightHearts.get(5);
                                        addFlurryAchievement(getString(R.string.bodyweight_copper));
                                    }
                                }
                                if (j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 25) {
                                    if (!j.getBoolean(BODYWEIGHT_BRONZE_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_bronze));
                                        j.put(BODYWEIGHT_BRONZE_COL, true);
                                        totalHearts[0] += weightHearts.get(4);
                                        addFlurryAchievement(getString(R.string.bodyweight_bronze));
                                    }
                                }
                                if (j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 50) {
                                    if (!j.getBoolean(BODYWEIGHT_SILVER_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_silver));
                                        j.put(BODYWEIGHT_SILVER_COL, true);
                                        totalHearts[0] += weightHearts.get(3);
                                        addFlurryAchievement(getString(R.string.bodyweight_silver));
                                    }
                                }
                                if (j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 100) {
                                    if (!j.getBoolean(BODYWEIGHT_GOLD_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_gold));
                                        j.put(BODYWEIGHT_GOLD_COL, true);
                                        totalHearts[0] += weightHearts.get(2);
                                        addFlurryAchievement(getString(R.string.bodyweight_gold));
                                    }
                                }
                                if (j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 150) {
                                    if (!j.getBoolean(BODYWEIGHT_PLATINUM_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_platinum));
                                        j.put(BODYWEIGHT_PLATINUM_COL, true);
                                        totalHearts[0] += weightHearts.get(1);
                                        addFlurryAchievement(getString(R.string.bodyweight_platinum));
                                    }
                                }
                                if (j.getInt(TOTAL_NON_WEIGHT_EXERCISES) >= 200) {
                                    if (!j.getBoolean(BODYWEIGHT_DIAMOND_COL)) {
                                        achievementsList.add(getString(R.string.bodyweight_diamond));
                                        j.put(BODYWEIGHT_DIAMOND_COL, true);
                                        totalHearts[0] += weightHearts.get(0);
                                        addFlurryAchievement(getString(R.string.bodyweight_diamond));
                                    }
                                }
                                if (j.getInt(TOTAL_WEIGHT_EXERCISES) >= 25) {
                                    if (!j.getBoolean(PROLUDIC_COPPER_COL)) {
                                        achievementsList.add(getString(R.string.proludic_copper));
                                        j.put(PROLUDIC_COPPER_COL, true);
                                        totalHearts[0] += weightHearts.get(11);
                                        addFlurryAchievement(getString(R.string.proludic_copper));
                                    }
                                }
                                if (j.getInt(TOTAL_WEIGHT_EXERCISES) >= 50) {
                                    if (!j.getBoolean(PROLUDIC_BRONZE_COL)) {
                                        achievementsList.add(getString(R.string.proludic_bronze));
                                        j.put(PROLUDIC_BRONZE_COL, true);
                                        totalHearts[0] += weightHearts.get(10);
                                        addFlurryAchievement(getString(R.string.proludic_bronze));
                                    }
                                }
                                if (j.getInt(TOTAL_WEIGHT_EXERCISES) >= 75) {
                                    if (!j.getBoolean(PROLUDIC_SILVER_COL)) {
                                        achievementsList.add(getString(R.string.proludic_silver));
                                        j.put(PROLUDIC_SILVER_COL, true);
                                        totalHearts[0] += weightHearts.get(9);
                                        addFlurryAchievement(getString(R.string.proludic_silver));
                                    }
                                }
                                if (j.getInt(TOTAL_WEIGHT_EXERCISES) >= 100) {
                                    if (!j.getBoolean(PROLUDIC_GOLD_COL)) {
                                        achievementsList.add(getString(R.string.proludic_gold));
                                        j.put(PROLUDIC_GOLD_COL, true);
                                        totalHearts[0] += weightHearts.get(8);
                                        addFlurryAchievement(getString(R.string.proludic_gold));
                                    }
                                }
                                if (j.getInt(TOTAL_WEIGHT_EXERCISES) >= 150) {
                                    if (!j.getBoolean(PROLUDIC_PLATINUM_COL)) {
                                        achievementsList.add(getString(R.string.proludic_platinum));
                                        j.put(PROLUDIC_PLATINUM_COL, true);
                                        totalHearts[0] += weightHearts.get(7);
                                        addFlurryAchievement(getString(R.string.proludic_platinum));
                                    }
                                }
                                if (j.getInt(TOTAL_WEIGHT_EXERCISES) >= 200) {
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
                                            if (totalHearts[0] != 0) {
                                                totalHearts[0] = totalHearts[0] + ParseUser.getCurrentUser().getInt(USER_HEARTS);
                                            }
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
                                                                    AppEventsLogger logger = AppEventsLogger.newLogger(context);
                                                                    logger.logEvent("User Metrics Total");
                                                                    Map<String, String> articleParams = new HashMap<String, String>();
                                                                    //param keys and values have to be of String type
                                                                    articleParams.put("Total Achievements", ParseUser.getCurrentUser().getEmail() + " has earned " + totalAchievements + " in total.");
                                                                    articleParams.put("Total Hearts", ParseUser.getCurrentUser().getEmail() + " has earned " + totalHearts[0] + " in total.");
                                                                    articleParams.put("Total Exercises", ParseUser.getCurrentUser().getEmail() + " has completed " + ParseUser.getCurrentUser().getInt(USER_EXERCISES) + " in total.");
                                                                    //up to 10 params can be logged with each event
                                                                    FlurryAgent.logEvent("Started Exercise", articleParams);
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
    private void displayExerciseTime(int i) {
        long exerciseTotalMillis = exerciseList.get(i).getTotalMillis();
        String eTime = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(exerciseTotalMillis),
                TimeUnit.MILLISECONDS.toSeconds(exerciseTotalMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(exerciseTotalMillis))
        );
        exerciseTime.setText(eTime);
    }

    private void resumeMainTimer() {
        stopwatch.resume();
        handler.postDelayed(mainRunnable, ONE_SECOND);
    }

    private void pauseMainTimer() {
        stopwatch.pause();
        eStopwatch = new Stopwatch();
        handler.removeCallbacks(mainRunnable);
    }

    private void stopMainTimer(boolean isMain) {
        sortTime(isMain);
    }

    private void sortTime(boolean isMain) {
        if (isMain) {
            setTime(time);
            handler.removeCallbacks(mainRunnable);
            stopwatch.stop();
        } else {
            eHandler.removeCallbacks(exerciseRunnable);
            eStopwatch.stop();
        }
    }

    private void startMainTimer(boolean isMain) {
        if (isMain) {
            stopwatch.start();
            handler.postDelayed(mainRunnable, ONE_SECOND);
        } else {
            eStopwatch.start();
            eHandler.postDelayed(exerciseRunnable, ONE_SECOND);
        }
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setETime(String time) {
        this.eTime = time;
    }

    long stopwatchMillis;
    public void setCurrentTime(TextView workoutTimerTV) {
        stopwatchMillis = stopwatch.getElapsedTimeMili();
        time = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(stopwatchMillis),
                TimeUnit.MILLISECONDS.toSeconds(stopwatchMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stopwatchMillis))
        );
        workoutTimerTV.setText(time);
    }

    long eStopwatchMillis;
    public void setCurrentETime(TextView workoutTimerTV) {
        eStopwatchMillis = eStopwatch.getElapsedTimeMili();
        eTime = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(eStopwatchMillis),
                TimeUnit.MILLISECONDS.toSeconds(eStopwatchMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eStopwatchMillis))
        );
        workoutTimerTV.setText(eTime);
    }
}
