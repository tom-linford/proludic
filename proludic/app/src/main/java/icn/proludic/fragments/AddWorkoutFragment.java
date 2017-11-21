package icn.proludic.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import icn.proludic.DashboardActivity;
import icn.proludic.R;
import icn.proludic.adapters.RecyclerViewAddWorkoutAdapter;
import icn.proludic.misc.ProfanityFilter;
import icn.proludic.misc.Utils;
import icn.proludic.models.SimpleExerciseModel;

import static icn.proludic.misc.Constants.EXERCISES_CLASS_NAME;
import static icn.proludic.misc.Constants.EXERCISE_IDS;
import static icn.proludic.misc.Constants.EXERCISE_NAME_KEY;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.LOCATION_NAME_KEY;
import static icn.proludic.misc.Constants.PRESET_WORKOUT_CLASS_NAME;
import static icn.proludic.misc.Constants.WORKOUT_IDS;
import static icn.proludic.misc.Constants.WORKOUT_NAME_KEY;

/**
 * Author: Tom Linford
 * Date: 19/10/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class AddWorkoutFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAddWorkoutAdapter adapter;
    ProgressBar progressBar;
    Utils utils;
    ArrayList<String> selectedExercises;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_workout, container, false);
        utils = new Utils(getActivity());
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        selectedExercises = new ArrayList<>();
        initRecyclerView(view);
        TextView finishButton = view.findViewById(R.id.finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedExercises.size() == 0) {
                    utils.makeText(getString(R.string.add_one_exercise), Toast.LENGTH_SHORT);
                } else {
                    showInputNameDialog();
                }
            }
        });
    }

    AlertDialog dialog;
    private void showInputNameDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        final EditText input = new EditText(getContext());
        input.setSingleLine();
        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(params);
        input.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        container.addView(input);
        alert.setTitle(R.string.new_workout_name);
        alert.setMessage(R.string.enter_workout_name);
        alert.setView(container);
        alert.setPositiveButton("OK", null);
        alert.setNegativeButton("Cancel", null);
        dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button pos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = input.getText().toString().trim();
                        if (title.isEmpty()) {
                            utils.makeText(getString(R.string.must_enter_title), Toast.LENGTH_SHORT);
                        } else if (ProfanityFilter.filterText(title)) {
                            utils.makeText(getString(R.string.no_workout_title_profanity), Toast.LENGTH_SHORT);
                        } else {
                            saveWorkout(title);
                        }
                    }
                });
                Button neg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void saveWorkout(String title) {
        final ParseObject workout = new ParseObject(PRESET_WORKOUT_CLASS_NAME);
        workout.put(WORKOUT_NAME_KEY, title);
        workout.put(EXERCISE_IDS, getSelectedExercisesInJSON());
        workout.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseQuery<ParseObject> getCurrentPark = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
                    getCurrentPark.whereEqualTo(LOCATION_NAME_KEY, ((DashboardActivity) getActivity()).getParkName());
                    getCurrentPark.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                object.add(WORKOUT_IDS, workout.getObjectId());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            dialog.dismiss();
                                            utils.makeText(getString(R.string.workout_created), Toast.LENGTH_LONG);
                                            getActivity().getSupportFragmentManager().popBackStack();
                                        } else {
                                            dialog.dismiss();
                                            utils.makeText(getString(R.string.unexpected_error), Toast.LENGTH_SHORT);
                                            Log.e("AddWorkoutFragment", "failed to add workout to current park: " + e.getLocalizedMessage());
                                        }
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                utils.makeText(getString(R.string.unexpected_error), Toast.LENGTH_SHORT);
                                Log.e("AddWorkoutFragment", "failed to get current park workouts: " + e.getLocalizedMessage());
                            }
                        }
                    });
                } else {
                    dialog.dismiss();
                    utils.makeText(getString(R.string.unexpected_error), Toast.LENGTH_SHORT);
                    Log.e("AddWorkoutFragment", "failed to save new workout: " + e.getLocalizedMessage());
                }
            }
        });
    }

    private JSONArray getSelectedExercisesInJSON() {
        JSONArray exercisesJSON = new JSONArray();
        for (String exercise : selectedExercises) {
            exercisesJSON.put(exercise);
        }
        return exercisesJSON;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.exercises_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        new GetExercises().execute();
    }

    ArrayList<SimpleExerciseModel> exercises;
    private class GetExercises extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            exercises = new ArrayList<>();
            ParseQuery<ParseObject> getExercises = ParseQuery.getQuery(EXERCISES_CLASS_NAME);
            // getExercises.whereEqualTo("isFrench", SharedPreferencesManager.getString(getContext(), "locale").equals("fr"));
            getExercises.orderByAscending(EXERCISE_NAME_KEY);
            List<String> availableExercises = ((DashboardActivity) getActivity()).getAvailableExercises();
            getExercises.whereContainedIn(EXERCISE_NAME_KEY, availableExercises);
            getExercises.setLimit(999);
            getExercises.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject exercise : objects) {
                            exercises.add(new SimpleExerciseModel(exercise.getObjectId(), exercise.getString(EXERCISE_NAME_KEY)));
                        }
                        adapter = new RecyclerViewAddWorkoutAdapter(getContext(), exercises);
                        adapter.setOnItemClickListener(new RecyclerViewAddWorkoutAdapter.onExerciseClickListener() {
                            @Override
                            public void onItemClickListener(View view, int position, String id, boolean isSelected) {
                                if (isSelected) {
                                    selectedExercises.remove(id);
                                } else {
                                    selectedExercises.add(id);
                                }
                            }
                        });
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        utils.makeText(getString(R.string.exercise_list_error), Toast.LENGTH_SHORT);
                        Log.e("AddWorkoutFragment", "failed to get exercises: " + e.getLocalizedMessage());
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
    }
}
