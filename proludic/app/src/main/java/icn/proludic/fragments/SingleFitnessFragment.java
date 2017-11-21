package icn.proludic.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import icn.proludic.R;
import icn.proludic.StartedExerciseActivity;
import icn.proludic.StartedWorkoutActivity;
import icn.proludic.adapters.RecyclerViewImageOnlyAdapter;
import icn.proludic.misc.CircleTransform;
import icn.proludic.misc.Connectivity;
import icn.proludic.misc.Constants;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.models.ExerciseListModel;
import icn.proludic.models.ExercisesModel;

import static icn.proludic.misc.Constants.EXERCISES_CLASS_NAME;
import static icn.proludic.misc.Constants.EXERCISES_KEY;
import static icn.proludic.misc.Constants.EXERCISE_AVERAGE_REPS;
import static icn.proludic.misc.Constants.EXERCISE_AVERAGE_TIME;
import static icn.proludic.misc.Constants.EXERCISE_DESCRIPTION_KEY;
import static icn.proludic.misc.Constants.EXERCISE_IDS;
import static icn.proludic.misc.Constants.EXERCISE_IMAGE_KEY;
import static icn.proludic.misc.Constants.EXERCISE_IS_WEIGHT;
import static icn.proludic.misc.Constants.EXERCISE_LIST;
import static icn.proludic.misc.Constants.EXERCISE_MUSCLE_GROUP_IMAGE_KEY;
import static icn.proludic.misc.Constants.EXERCISE_NAME_KEY;
import static icn.proludic.misc.Constants.EXERCISE_NO_WEIGHT_DESCRIPTION;
import static icn.proludic.misc.Constants.EXERCISE_RESISTANCE;
import static icn.proludic.misc.Constants.EXERCISE_REST_TIME;
import static icn.proludic.misc.Constants.EXERCISE_TOTAL_REPS;
import static icn.proludic.misc.Constants.EXERCISE_TOTAL_SETS;
import static icn.proludic.misc.Constants.EXERCISE_TOTAL_WEIGHT;
import static icn.proludic.misc.Constants.EXERCISE_VIDEO_URL_KEY;
import static icn.proludic.misc.Constants.EXERCISE_WEIGHT;
import static icn.proludic.misc.Constants.FITNESS_NAME_TAG;
import static icn.proludic.misc.Constants.NO_VIDEO;
import static icn.proludic.misc.Constants.PRESET_WORKOUT_CLASS_NAME;
import static icn.proludic.misc.Constants.SINGLE_FITNESS_TAG;
import static icn.proludic.misc.Constants.TOTAL_EXERCISES;
import static icn.proludic.misc.Constants.WORKOUTS_KEY;
import static icn.proludic.misc.Constants.WORKOUT_BRAND_IMAGE_KEY;
import static icn.proludic.misc.Constants.WORKOUT_BRAND_NAME_KEY;
import static icn.proludic.misc.Constants.WORKOUT_DESCRIPTION_KEY;
import static icn.proludic.misc.Constants.WORKOUT_NAME_KEY;

/**
 * Author:  Bradley Wilson
 * Date: 05/05/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class SingleFitnessFragment extends Fragment {

    private String fitnessName;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            removeThisFragmentFromBackStack();
        }
    };
    private int totalExercises;
    private View view;
    private final static String FITNESS_TYPE = "Fitness";
    private final static String ENDURANCE_TYPE = "Endurance";
    private final static String MUSCLE_TYPE = "Muscle";
    private long averageTimeMillis;
    private long restTimeMillis;
    private String weight;
    private ArrayList<ExerciseListModel> exerciseList = new ArrayList<>();
    private String exerciseVideo;
    private String averageSets;
    private String imageUrl;
    private int totalWeight;
    private int sets, reps;
    private boolean isWeight = false;
    private String weightDescription = "";
    private ImageView viewBrandButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = null;
        Bundle bundle = getArguments();
        String bundleID = bundle.getString(SINGLE_FITNESS_TAG);
        fitnessName =  bundle.getString(FITNESS_NAME_TAG);
        if (bundleID != null) {
            Log.e("bundleID", "isNotNull");
            switch (bundleID) {
                case WORKOUTS_KEY:
                    Log.e("currentKey", WORKOUTS_KEY);
                    view = inflater.inflate(R.layout.fragment_single_workout, container, false);
                    checkIfBranded();
                    showDialog(view, bundleID);
                    break;
                case EXERCISES_KEY:
                    Log.e("currentKey", EXERCISES_KEY);
                    view = inflater.inflate(R.layout.fragment_single_exercise, container, false);
                    showDialog(view, bundleID);
                    break;
            }
        }
        return view;
    }

    private void checkIfBranded() {
        viewBrandButton = view.findViewById(R.id.view_brand_info);
        viewBrandButton.setOnClickListener(customListener);

        ParseQuery<ParseObject> workout = ParseQuery.getQuery(PRESET_WORKOUT_CLASS_NAME);
        workout.whereEqualTo(WORKOUT_NAME_KEY, fitnessName);
        workout.whereExists(WORKOUT_BRAND_NAME_KEY);
        workout.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    viewBrandButton.setVisibility(View.VISIBLE);
                } else {
                    viewBrandButton.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showDialog(final View view, final String bundleID) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fitness_type);
        dialog.setCancelable(false);
        ImageView btnFitness = (ImageView) dialog.findViewById(R.id.btn_fitness);
        btnFitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (bundleID) {
                    case EXERCISES_KEY:
                        initExerciseViews(view, fitnessName, FITNESS_TYPE);
                        dialog.cancel();
                        break;
                    case WORKOUTS_KEY:
                        initWorkoutViews(view, fitnessName, FITNESS_TYPE);
                        dialog.cancel();
                        break;
                }
            }
        });
        ImageView btnEndurance = (ImageView) dialog.findViewById(R.id.btn_endurance);
        btnEndurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (bundleID) {
                    case EXERCISES_KEY:
                        initExerciseViews(view, fitnessName, ENDURANCE_TYPE);
                        dialog.cancel();
                        break;
                    case WORKOUTS_KEY:
                        initWorkoutViews(view, fitnessName, ENDURANCE_TYPE);
                        dialog.cancel();
                        break;
                }
            }
        });
        ImageView btnMuscle = (ImageView) dialog.findViewById(R.id.btn_muscle);
        btnMuscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (bundleID) {
                    case EXERCISES_KEY:
                        initExerciseViews(view, fitnessName, MUSCLE_TYPE);
                        dialog.cancel();
                        break;
                    case WORKOUTS_KEY:
                        initWorkoutViews(view, fitnessName, MUSCLE_TYPE);
                        dialog.cancel();
                        break;
                }
            }
        });
       dialog.show();
    }

    private void initExerciseViews(final View view, final String fitnessName, final String fitnessType) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(EXERCISES_CLASS_NAME);
        // query.whereEqualTo("isFrench", SharedPreferencesManager.getString(getContext(), "locale").equals("fr"));
        query.whereEqualTo(EXERCISE_NAME_KEY, fitnessName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        TextView exerciseName = (TextView) view.findViewById(R.id.single_exercise_title);
                        exerciseName.setText(fitnessName);

                        ImageView exerciseThumbnail = (ImageView) view.findViewById(R.id.single_exercise_video_thumbnail);
                        Picasso.with(getActivity()).load(j.getParseFile(EXERCISE_IMAGE_KEY).getUrl()).into(exerciseThumbnail);
                        exerciseThumbnail.setOnClickListener(customListener);

                        ImageView playButton = (ImageView) view.findViewById(R.id.play_button);
                        exerciseVideo = j.getString(EXERCISE_VIDEO_URL_KEY);
                        if (!exerciseVideo.equals(NO_VIDEO)) {
                            playButton.setOnClickListener(customListener);
                        } else {
                            playButton.setVisibility(View.GONE);
                        }

                        imageUrl = j.getParseFile(EXERCISE_MUSCLE_GROUP_IMAGE_KEY).getUrl();

                        TextView exerciseDescription = (TextView) view.findViewById(R.id.single_exercise_description);
                        exerciseDescription.setText(j.getString(EXERCISE_DESCRIPTION_KEY));

                        JSONArray exerciseDetailsArray = j.getJSONArray(fitnessType);
                        try {
                            LinearLayout lp = (LinearLayout) view.findViewById(R.id.lower_level_container);
                            try {
                                totalWeight = Integer.parseInt(exerciseDetailsArray.getString(5));
                            } catch (NumberFormatException e1) {
                                Log.e("", e1.getLocalizedMessage());
                            } finally {
                                totalWeight = (int) Double.parseDouble(exerciseDetailsArray.getString(5));
                            }
                            weight = (String) exerciseDetailsArray.get(2);
                            TextView averageWeight = (TextView) view.findViewById(R.id.single_exercise_weight);
                            averageWeight.setText((String) exerciseDetailsArray.get(2));
                            if (totalWeight == 0) {
                                isWeight = true;
                                weightDescription = exerciseDetailsArray.getString(6);
                                TextView weight_description = (TextView) view.findViewById(R.id.no_weight_description);
                                weight_description.setText(exerciseDetailsArray.getString(6));
                                weight_description.setVisibility(View.VISIBLE);
                                lp.setVisibility(View.INVISIBLE);
                            } else {
                                lp.setVisibility(View.VISIBLE);
                                showWeightDialog(weight, j.getString(EXERCISE_RESISTANCE), fitnessType);
                            }

                            sets = Integer.parseInt(exerciseDetailsArray.getString(0));
                            reps = Integer.parseInt(exerciseDetailsArray.getString(1));
                            averageSets = exerciseDetailsArray.get(0) + " x " + exerciseDetailsArray.get(1);
                            TextView averageReps = view.findViewById(R.id.single_exercise_reps);
                            averageReps.setText(averageSets);

                            restTimeMillis = Long.parseLong((String) exerciseDetailsArray.get(4));
                            String restTime = String.format(Locale.getDefault(), "%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(restTimeMillis),
                                    TimeUnit.MILLISECONDS.toSeconds(restTimeMillis) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(restTimeMillis))
                            );

                            averageTimeMillis = Long.parseLong((String) exerciseDetailsArray.get(3));
                            String avgTime = String.format(Locale.getDefault(), "%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(averageTimeMillis),
                                    TimeUnit.MILLISECONDS.toSeconds(averageTimeMillis) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(averageTimeMillis))
                            );
                            TextView averageTime = (TextView) view.findViewById(R.id.single_exercise_time);
                            averageTime.setText(avgTime);

                            TextView restTimeTV = (TextView) view.findViewById(R.id.single_exercise_rest_time);
                            restTimeTV.setText(restTime);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });

        TextView startExerciseButton = (TextView) view.findViewById(R.id.start_exercise_button);
        startExerciseButton.setOnClickListener(customListener);
    }

    private void showWeightDialog(final String weight, final String resistance, String fitnessType) {
        final Dialog dialog = new Dialog(getActivity(), R.style.customAlertDialog);
        dialog.setContentView(R.layout.dialog_slider);
        dialog.setTitle(getString(R.string.chooseweight));
        dialog.setCancelable(false);

        String weightArray[] = weight.split("-");
        Log.e("resistance", resistance);
        final int min = Integer.parseInt(weightArray[0]);
        int max = Integer.parseInt(weightArray[1]);
        SeekBar time_seekbar = (SeekBar) dialog.findViewById(R.id.timer_seekbar);
        time_seekbar.setProgress(0);
        time_seekbar.setMax(max - min);
        final TextView seekBarValue = (TextView) dialog.findViewById(R.id.value_tv);
        seekBarValue.setText(String.valueOf(min));
        time_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText(String.valueOf(progress + min));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel_button);
        cancelButton.setVisibility(View.INVISIBLE);
        TextView confirmButton = (TextView) dialog.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] weights = resistance.split("-");
                int total = Integer.valueOf(weights[1]) - Integer.valueOf(weights[0]);
                int increment = total / 8;
                totalWeight = increment * Integer.valueOf(seekBarValue.getText().toString());
                Log.e("debug", "multiplier: " + seekBarValue.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initWorkoutViews(final View view, final String fitnessName, final String fitnessType) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PRESET_WORKOUT_CLASS_NAME);
        query.whereEqualTo(WORKOUT_NAME_KEY, fitnessName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    RecyclerView workoutExercisesThumbnails = (RecyclerView) view.findViewById(R.id.workouts_exercises_thumbnails);
                    workoutExercisesThumbnails.setHasFixedSize(true);
                    RecyclerView.LayoutManager wLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    workoutExercisesThumbnails.setLayoutManager(wLayoutManager);
                    try {
                        populateThumbnails(workoutExercisesThumbnails, objects, fitnessType);
                    } catch (JSONException | ParseException e1) {
                        e1.printStackTrace();
                    }
                    for (ParseObject j : objects) {
                        TextView exerciseName = (TextView) view.findViewById(R.id.single_workout_title);
                        exerciseName.setText(fitnessName);
                        TextView exerciseDescription = (TextView) view.findViewById(R.id.single_workout_description);
                        exerciseDescription.setText(j.getString(WORKOUT_DESCRIPTION_KEY));
                        TextView exercisesTotalTV = (TextView) view.findViewById(R.id.single_workout_exercises_amount);
                        exercisesTotalTV.setText(String.valueOf(totalExercises));
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });

        TextView startExerciseButton = (TextView) view.findViewById(R.id.start_workout_button);
        startExerciseButton.setOnClickListener(customListener);
    }

    private void populateThumbnails(RecyclerView workoutExercisesThumbnails, List<ParseObject> objects, String fitnessType) throws JSONException, ParseException {
        List<String> exerciseObjects = new ArrayList<>();
        List<ParseObject> currentWorkoutExerciseParseObjects = new ArrayList<>();
        List<ExercisesModel> currentWorkoutExerciseImages = new ArrayList<>();
        JSONArray exerciseIds = objects.get(0).getJSONArray(EXERCISE_IDS);

        for (int i = 0; i < exerciseIds.length(); i++) {
            exerciseObjects.add(exerciseIds.getString(i));
        }
        for (String objectId : exerciseObjects) {
            currentWorkoutExerciseParseObjects.add(ParseObject.createWithoutData(EXERCISES_CLASS_NAME, objectId));
        }
        ParseObject.fetchAll(currentWorkoutExerciseParseObjects);
        for (ParseObject j : currentWorkoutExerciseParseObjects) {
            currentWorkoutExerciseImages.add(new ExercisesModel(j.getParseFile(EXERCISE_IMAGE_KEY).getUrl(), j.getString(EXERCISE_VIDEO_URL_KEY)));
            int totalReps = Integer.parseInt(j.getJSONArray(fitnessType).getString(0)) * Integer.parseInt(j.getJSONArray(fitnessType).getString(1));
            long totalTimeExercising = Integer.parseInt(j.getJSONArray(fitnessType).getString(0)) * Long.parseLong(j.getJSONArray(fitnessType).getString(3));
            long totalTimeResting = Integer.parseInt(j.getJSONArray(fitnessType).getString(0)) * Long.parseLong(j.getJSONArray(fitnessType).getString(4));
            long totalTime = totalTimeExercising + totalTimeResting;
            int totalWeight = Integer.parseInt(j.getJSONArray(fitnessType).getString(5)) * totalReps;
            exerciseList.add(new ExerciseListModel(j.getParseFile(EXERCISE_IMAGE_KEY).getUrl(), j.getString(EXERCISE_NAME_KEY), totalReps, totalTime, j.getJSONArray(fitnessType).getString(2), totalWeight));
        }
        totalExercises = currentWorkoutExerciseImages.size();
        RecyclerViewImageOnlyAdapter adapter = new RecyclerViewImageOnlyAdapter(getActivity(), currentWorkoutExerciseImages);
        workoutExercisesThumbnails.setAdapter(adapter);
        adapter.setOnItemClickListener(new RecyclerViewImageOnlyAdapter.onThumbnailItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position, ExercisesModel model) {
                try {
                    if (!SharedPreferencesManager.getBoolean(getActivity(), Constants.SETTINGS_ENABLE_3G_4G)) {
                        if (Connectivity.isConnectedWifi(getContext())) {
                            showVideoDialog(model.getVideoURL());
                        } else {
                            AlertDialog diaBox = AskOption(model.getVideoURL());
                            diaBox.show();
                        }
                    } else {
                        showVideoDialog(model.getVideoURL());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        adapter.notifyDataSetChanged();
    }


    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_exercise_button:
                    Intent intent = new Intent(getActivity(), StartedExerciseActivity.class);
                    intent.putExtra(EXERCISE_NAME_KEY, fitnessName);
                    intent.putExtra(EXERCISE_AVERAGE_TIME, averageTimeMillis);
                    intent.putExtra(EXERCISE_WEIGHT, weight);
                    intent.putExtra(EXERCISE_REST_TIME, restTimeMillis);
                    intent.putExtra(EXERCISE_AVERAGE_REPS, averageSets);
                    intent.putExtra(EXERCISE_TOTAL_WEIGHT, totalWeight);
                    intent.putExtra(EXERCISE_TOTAL_SETS, sets);
                    intent.putExtra(EXERCISE_TOTAL_REPS, reps);
                    intent.putExtra(EXERCISE_IS_WEIGHT, isWeight);
                    intent.putExtra(EXERCISE_NO_WEIGHT_DESCRIPTION, weightDescription);
                    intent.putExtra(EXERCISE_MUSCLE_GROUP_IMAGE_KEY, imageUrl);
                    getActivity().startActivity(intent);
                    removeThisFragmentFromBackStack();
                    break;
                case R.id.start_workout_button:
                    Intent intent1= new Intent(getActivity(), StartedWorkoutActivity.class);
                    intent1.putExtra(WORKOUT_NAME_KEY, fitnessName);
                    intent1.putExtra(EXERCISE_LIST, exerciseList);
                    intent1.putExtra(TOTAL_EXERCISES, totalExercises);
                    Log.e("debug", fitnessName);
                    Log.e("debug", exerciseList.toString());
                    Log.e("debug", Integer.toString(totalExercises));
                    getActivity().startActivity(intent1);
                    removeThisFragmentFromBackStack();
                    break;
                case R.id.play_button:
                    try {
                        if (!SharedPreferencesManager.getBoolean(getActivity(), Constants.SETTINGS_ENABLE_3G_4G)) {
                            if (Connectivity.isConnectedWifi(getContext())) {
                                showVideoDialog(exerciseVideo);
                            } else {
                                AlertDialog diaBox = AskOption(exerciseVideo);
                                diaBox.show();
                            }
                        } else {
                            showVideoDialog(exerciseVideo);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.view_brand_info:
                    showBrandInfoDialog();
                    break;
            }
        }
    };

    private void showBrandInfoDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_brand_info);
        dialog.setCancelable(true);
        ImageView brandImage = dialog.findViewById(R.id.brand_image);
        TextView brandName = dialog.findViewById(R.id.brand_name);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PRESET_WORKOUT_CLASS_NAME);
        query.whereEqualTo(WORKOUT_NAME_KEY, fitnessName);
        try {
            ParseObject workout = query.getFirst();
            Picasso.with(getContext()).load(workout.getParseFile(WORKOUT_BRAND_IMAGE_KEY).getUrl()).transform(new CircleTransform()).into(brandImage);
            brandName.setText(workout.getString(WORKOUT_BRAND_NAME_KEY));
        } catch (ParseException e) {
            Log.e("debug", e.getLocalizedMessage());
        }
        dialog.show();
    }

    private AlertDialog AskOption(final String videoURL)
    {
        return new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle(getString(R.string.connectivity))
                .setMessage(getString(R.string.not_connected_wifi))
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton(getString(R.string.continue_text), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            showVideoDialog(videoURL);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create();
    }

    private void showVideoDialog(String exerciseVideo) throws ParseException {
        final Dialog dialog = new Dialog(getContext());// add here your class name
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_intro_video);
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
        final VideoView mVideoView = (VideoView) dialog.findViewById(R.id.introVideo);
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mVideoView.setVideoURI(Uri.parse(exerciseVideo));
        mVideoView.start();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dialog.cancel();
            }
        });
    }

    private void removeThisFragmentFromBackStack() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction trans = manager.beginTransaction();
        trans.remove(this);
        trans.commit();
        manager.popBackStack();
    }
}
