package icn.proludic.fragments;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import icn.proludic.DashboardActivity;
import icn.proludic.R;
import icn.proludic.adapters.RecyclerViewExerciseAdapter;
import icn.proludic.adapters.RecyclerViewWorkoutsAdapter;
import icn.proludic.misc.DatabaseHandler;
import icn.proludic.models.ExercisesModel;
import icn.proludic.models.Fitness;
import icn.proludic.models.WorkoutsModel;

import static android.view.View.GONE;
import static icn.proludic.misc.Constants.BROWSE_ALL;
import static icn.proludic.misc.Constants.BROWSE_FRAGMENT_TAG;
import static icn.proludic.misc.Constants.EXERCISES_CLASS_NAME;
import static icn.proludic.misc.Constants.EXERCISES_KEY;
import static icn.proludic.misc.Constants.EXERCISE_DESCRIPTION_KEY;
import static icn.proludic.misc.Constants.EXERCISE_IDS;
import static icn.proludic.misc.Constants.EXERCISE_IMAGE_KEY;
import static icn.proludic.misc.Constants.EXERCISE_NAME_KEY;
import static icn.proludic.misc.Constants.EXERCISE_VIDEO_URL_KEY;
import static icn.proludic.misc.Constants.FAVOURITED;
import static icn.proludic.misc.Constants.FAVOURITE_EXERCISES;
import static icn.proludic.misc.Constants.FAVOURITE_WORKOUTS;
import static icn.proludic.misc.Constants.FITNESS_NAME_TAG;
import static icn.proludic.misc.Constants.HOME_PARK_AND_CURRENT_TYPE;
import static icn.proludic.misc.Constants.MOST_USED;
import static icn.proludic.misc.Constants.NO_PARK_TYPE;
import static icn.proludic.misc.Constants.PRESET_WORKOUT_CLASS_NAME;
import static icn.proludic.misc.Constants.SINGLE_FITNESS_TAG;
import static icn.proludic.misc.Constants.WORKOUTS_KEY;
import static icn.proludic.misc.Constants.WORKOUT_BRAND_IMAGE_KEY;
import static icn.proludic.misc.Constants.WORKOUT_BRAND_NAME_KEY;
import static icn.proludic.misc.Constants.WORKOUT_NAME_KEY;

/**
 * Author:  Bradley Wilson
 * Date: 13/04/2017
 * Package: icn.proludic.fragments
 * Project Name: proludic
 */

public class BrowseFragment extends Fragment {

    private String isSelected = BROWSE_ALL;
    private RecyclerView browseRecyclerView;
    private EditText et;
    private RecyclerViewExerciseAdapter exercisesAdapter;
    private ArrayList<ExercisesModel> placeholderList = new ArrayList<>();
    private DatabaseHandler db;
    private ArrayList<Fitness> mostUsedExercises;
    private TextView browseAll, mostUsed, myFavourites;
    private String bundleID;
    private RecyclerViewWorkoutsAdapter workoutAdapter;
    private boolean isAvailable;
    private ArrayList<ExercisesModel> exercisesList;
    private ArrayList<WorkoutsModel> workoutsList;
    private TextView noDataTV;
    private List<Fitness> fitnessArray;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        TextView noParkTV = view.findViewById(R.id.no_park_tv);
        if (((DashboardActivity) getActivity()).getParkType().equals(NO_PARK_TYPE)) {
            noParkTV.setVisibility(View.VISIBLE);
        } else {
            browseAll = view.findViewById(R.id.tv_browse_all);
            browseAll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            browseAll.setOnClickListener(customListener);
            mostUsed =  view.findViewById(R.id.tv_most_used);
            mostUsed.setOnClickListener(customListener);
            noDataTV = view.findViewById(R.id.no_data_tv);
            myFavourites = (TextView) view.findViewById(R.id.tv_my_favourites);
            myFavourites.setOnClickListener(customListener);
            db = ((DashboardActivity) getActivity()).utils.getSQLDatabase();
            et = view.findViewById(R.id.browse_et);
            et.addTextChangedListener(customTextChangeListener);
            browseRecyclerView = view.findViewById(R.id.browse_recyclerView);
            browseRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            browseRecyclerView.setLayoutManager(mLayoutManager);
            determineWhatToPopulate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (noDataTV != null) {
            noDataTV.setVisibility(GONE);
        }
    }

    private View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_browse_all:
                    mostUsed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    myFavourites.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    noDataTV.setVisibility(GONE);
                    populateBrowseAllRecyclerView(bundleID, 0, BROWSE_ALL);
                    break;
                case R.id.tv_most_used:
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    browseAll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    myFavourites.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    noDataTV.setVisibility(GONE);
                    populateMostUsedRecyclerView(bundleID, 0, MOST_USED);
                    break;
                case R.id.tv_my_favourites:
                    v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                    mostUsed.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    browseAll.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryHalf));
                    noDataTV.setVisibility(GONE);
                    populateMyFavouritesRecyclerView(bundleID, 0, FAVOURITED);
                    break;
            }
        }
    };

    private void determineWhatToPopulate() {
        Bundle bundle = getArguments();
        bundleID = bundle.getString(BROWSE_FRAGMENT_TAG);
        assert bundleID != null;
        switch (bundleID) {
            case EXERCISES_KEY:
                populateBrowseAllRecyclerView(bundleID, 0, BROWSE_ALL);
                break;
            case WORKOUTS_KEY:
                populateBrowseAllRecyclerView(bundleID, 0, BROWSE_ALL);
                break;
        }
    }

    private void populateBrowseAllRecyclerView(final String bundleID, final int position, String selectionType) {
        List<String> fitnessNames = new ArrayList<>();
        ParseQuery<ParseObject> query = getCorrespondingQuery(bundleID, selectionType);
        switch (bundleID) {
            case EXERCISES_KEY:
                fitnessNames = ((DashboardActivity) getActivity()).getAvailableExercises();
                query.whereContainedIn(EXERCISE_NAME_KEY, fitnessNames);
                break;
            case WORKOUTS_KEY:
                fitnessNames = ((DashboardActivity) getActivity()).getAvailableWorkouts();
                query.whereContainedIn(WORKOUT_NAME_KEY, fitnessNames);
                break;
        }
        doTheRest(fitnessNames, query, bundleID, position, selectionType);
    }

    private void populateMostUsedRecyclerView(String bundleID, int position, String selectionType) {
        List<String> fitnessNames = new ArrayList<>();
        fitnessArray = new ArrayList<>();
        ParseQuery<ParseObject> query = getCorrespondingQuery(bundleID, selectionType);
        switch (bundleID) {
            case EXERCISES_KEY:
                fitnessArray = ((DashboardActivity) getActivity()).db.getAllExercises(false);
                Collections.sort(fitnessArray, new Comparator<Fitness>() {
                    @Override
                    public int compare(Fitness s1, Fitness s2) {
                        return s1.getName().compareToIgnoreCase(s2.getName());
                    }
                });
                for (int i = 0; i < fitnessArray.size(); i++) {
                    Log.e(BrowseFragment.class.getSimpleName(), fitnessArray.get(i).getName());
                    fitnessNames.add(fitnessArray.get(i).getName());
                }
                Log.e("feFitnessSize", String.valueOf(fitnessNames.size()));
                query.whereContainedIn(EXERCISE_NAME_KEY, fitnessNames);
                break;
            case WORKOUTS_KEY:
                fitnessArray = ((DashboardActivity) getActivity()).db.getAllExercises(true);
                Collections.sort(fitnessArray, new Comparator<Fitness>() {
                    @Override
                    public int compare(Fitness s1, Fitness s2) {
                        return s1.getName().compareToIgnoreCase(s2.getName());
                    }
                });
                for (int i = 0; i < fitnessArray.size(); i++) {
                    Log.e(BrowseFragment.class.getSimpleName(), fitnessArray.get(i).getName());
                    fitnessNames.add(fitnessArray.get(i).getName());
                }
                Log.e("weFitnessSize", String.valueOf(fitnessNames.size()));
                query.whereContainedIn(WORKOUT_NAME_KEY, fitnessNames);
                break;
        }

        if (fitnessNames.size() > 0) {
            doTheRest(fitnessNames, query, bundleID, position, selectionType);
        } else {
            noDataTV.setVisibility(View.VISIBLE);
            noDataTV.setText(getString(R.string.no_most_used));
        }
    }

    private void populateMyFavouritesRecyclerView(String bundleID, int position, String selectionType) {
        List<String> fitnessNames = new ArrayList<>();
        ParseQuery<ParseObject> query = getCorrespondingQuery(bundleID, selectionType);
        switch (bundleID) {
            case EXERCISES_KEY:
                fitnessNames = getFavouriteExercises(FAVOURITE_EXERCISES, EXERCISES_CLASS_NAME, EXERCISE_NAME_KEY);
                Log.e("feFitnessSize", String.valueOf(fitnessNames.size()));
                query.whereContainedIn(EXERCISE_NAME_KEY, fitnessNames);
                break;
            case WORKOUTS_KEY:
                fitnessNames = getFavouriteExercises(FAVOURITE_WORKOUTS, PRESET_WORKOUT_CLASS_NAME, WORKOUT_NAME_KEY);
                Log.e("fwFitnessSize", String.valueOf(fitnessNames.size()));
                query.whereContainedIn(WORKOUT_NAME_KEY, fitnessNames);
                break;
        }

        if (fitnessNames.size() > 0) {
            noDataTV.setVisibility(View.GONE);
            doTheRest(fitnessNames, query, bundleID, position, selectionType);
        } else {
            noDataTV.setVisibility(View.VISIBLE);
            noDataTV.setText(getActivity().getResources().getString(R.string.nofavourites));
        }
    }

    private void doTheRest(List<String> fitnessNames, ParseQuery<ParseObject> query, final String bundleID, final int position, final String selectionType) {
        final List<String> finalFitnessNames = fitnessNames;
        exercisesList = new ArrayList<>();
        workoutsList = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    switch (bundleID) {
                        case EXERCISES_KEY:
                            int eCount = 0;
                            for (ParseObject j : objects) {
                                if (finalFitnessNames != null) {
                                    if (getActivity() != null) {
                                        if (((DashboardActivity) getActivity()).getParkType() != null) {
                                            if (((DashboardActivity) getActivity()).getParkType().equals(HOME_PARK_AND_CURRENT_TYPE)) {
                                                // an exercise is available if it isn't in the list of finalFitnessNames and if the home park doesn't contain it
                                                isAvailable = !(finalFitnessNames.contains(j.getString(EXERCISE_NAME_KEY)) && /*!*/((DashboardActivity) getActivity()).getHomeParkExerciseNames().contains(j.getString(EXERCISE_NAME_KEY)));
                                                Log.e("isAvailable", String.valueOf(isAvailable));
                                            } else {
                                                isAvailable = true;
                                            }
                                        } else {
                                            isAvailable = true;
                                        }
                                    }
                                    if (selectionType.equals(MOST_USED)) {
                                        isAvailable = false;
                                    }
                                    boolean isFavourited = ParseUser.getCurrentUser().getJSONArray(FAVOURITE_EXERCISES) != null && ParseUser.getCurrentUser().getJSONArray(FAVOURITE_EXERCISES).toString().contains(j.getObjectId());
                                    if (selectionType.equals(MOST_USED)) {
                                        exercisesList.add(new ExercisesModel(j.getObjectId(), j.getString(EXERCISE_NAME_KEY), j.getParseFile(EXERCISE_IMAGE_KEY).getUrl(), j.getString(EXERCISE_VIDEO_URL_KEY), j.getString(EXERCISE_DESCRIPTION_KEY), isAvailable, isFavourited, fitnessArray.get(eCount).getUses()));
                                    } else {
                                        exercisesList.add(new ExercisesModel(j.getObjectId(), j.getString(EXERCISE_NAME_KEY), j.getParseFile(EXERCISE_IMAGE_KEY).getUrl(), j.getString(EXERCISE_VIDEO_URL_KEY), j.getString(EXERCISE_DESCRIPTION_KEY), isAvailable, isFavourited));
                                    }
                                } else {
                                    Log.e("exerciseNames", "isNull");
                                }
                                eCount++;
                            }
                            exercisesAdapter = new RecyclerViewExerciseAdapter(getActivity(), exercisesList, selectionType);
                            browseRecyclerView.setAdapter(exercisesAdapter);
                            exercisesAdapter.setOnItemClickListener(new RecyclerViewExerciseAdapter.onExerciseItemClickListener() {
                                @Override
                                public void onItemClickListener(View v, int position, String name, String objectId, boolean isFavourited) {
                                    switch (v.getId()) {
                                        case R.id.e_container:
                                        case R.id.name:
                                            doTransaction(EXERCISES_KEY, name);
                                            break;
                                        case R.id.favourite_icon:
                                            if (!isFavourited) {
                                                ImageView iv = (ImageView) browseRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.favourite_icon);
                                                iv.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
                                                addFitnessToFavourites(objectId, FAVOURITE_EXERCISES, bundleID, position, selectionType);
                                            } else {
                                                ImageView iv = (ImageView) browseRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.favourite_icon);
                                                iv.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.black), PorterDuff.Mode.SRC_IN));
                                                removeFitnessFromFavourites(objectId, FAVOURITE_EXERCISES, bundleID, position, selectionType);
                                            }
                                            break;
                                    }
                                }
                            });
                            exercisesAdapter.notifyDataSetChanged();
                            browseRecyclerView.scrollToPosition(position);
                            break;
                        case WORKOUTS_KEY:
                            int wCount = 0;
                            for (ParseObject j : objects) {
                                if (((DashboardActivity) getActivity()).getParkType() != null) {
                                    if (((DashboardActivity) getActivity()).getParkType().equals(HOME_PARK_AND_CURRENT_TYPE)) {
                                        isAvailable = !(finalFitnessNames.contains(j.getString(WORKOUT_NAME_KEY)) && /*!*/((DashboardActivity) getActivity()).getHomeParkWorkoutNames().contains(j.getString(WORKOUT_NAME_KEY)));
                                        Log.e("isAvailable", String.valueOf(isAvailable));
                                    } else {
                                        isAvailable = true;
                                    }
                                }

                                if (selectionType.equals(MOST_USED)) {
                                    isAvailable = false;
                                }

                                boolean isFavourited = ParseUser.getCurrentUser().getJSONArray(FAVOURITE_WORKOUTS) != null && ParseUser.getCurrentUser().getJSONArray(FAVOURITE_WORKOUTS).toString().contains(j.getObjectId());
                                WorkoutsModel w;
                                if (selectionType.equals(MOST_USED)) {
                                    w = new WorkoutsModel(j.getObjectId(), j.getString(WORKOUT_NAME_KEY), j.getJSONArray(EXERCISE_IDS), isAvailable, isFavourited, fitnessArray.get(wCount).getUses());
                                } else {
                                    w = new WorkoutsModel(j.getObjectId(), j.getString(WORKOUT_NAME_KEY), j.getJSONArray(EXERCISE_IDS), isAvailable, isFavourited);
                                }
                                String name = j.getString(WORKOUT_BRAND_NAME_KEY);
                                if (name != null) {
                                    Log.e("debug", "saved one branded workout");
                                    w.setBrandInfo(j.getParseFile(WORKOUT_BRAND_IMAGE_KEY).getUrl(), name);
                                }
                                workoutsList.add(w);
                                wCount++;
                            }
                            workoutAdapter = new RecyclerViewWorkoutsAdapter(getActivity(), workoutsList, selectionType);
                            browseRecyclerView.setAdapter(workoutAdapter);
                            workoutAdapter.setOnItemClickListener(new RecyclerViewWorkoutsAdapter.onWorkoutItemClickListener() {
                                @Override
                                public void onItemClickListener(View v, int position, String name, String objectId, boolean isFavourited) {
                                    switch (v.getId()) {
                                        case R.id.e_container:
                                        case R.id.name:
                                            doTransaction(WORKOUTS_KEY, name);
                                            break;
                                        case R.id.favourite_icon:
                                            if (!isFavourited) {
                                                ImageView iv = (ImageView) browseRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.favourite_icon);
                                                iv.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN));
                                                addFitnessToFavourites(objectId, FAVOURITE_WORKOUTS, bundleID, position, selectionType);
                                            } else {
                                                ImageView iv = (ImageView) browseRecyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.favourite_icon);
                                                iv.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.black), PorterDuff.Mode.SRC_IN));
                                                removeFitnessFromFavourites(objectId, FAVOURITE_WORKOUTS, bundleID, position, selectionType);
                                            }
                                            break;
                                    }
                                }
                            });
                            workoutAdapter.notifyDataSetChanged();
                            browseRecyclerView.scrollToPosition(position);
                            break;
                    }
                } else {
                    Log.e("failed", "failed" + e.getLocalizedMessage());
                }
            }
        });
    }

    private void removeFitnessFromFavourites(String objectId, String exerciseKey, final String bundleID, final int position, final String selectionType) {
        JSONArray exerciseArray = ParseUser.getCurrentUser().getJSONArray(exerciseKey);
        if (exerciseArray != null) {
            if (exerciseArray.length() > 0) {
                for (int i = 0; i < exerciseArray.length(); i++) {
                    try {
                        if (exerciseArray.getString(i).equals(objectId)) {
                            exerciseArray.remove(i);
                        }
                    } catch (JSONException e) {
                        Log.e("failed", "failed" + e.getLocalizedMessage());
                    }
                }
            }
        }
        ParseUser.getCurrentUser().put(exerciseKey, exerciseArray);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    switch (selectionType) {
                        case BROWSE_ALL:
                            populateBrowseAllRecyclerView(bundleID, position, BROWSE_ALL);
                            break;
                        case FAVOURITED:
                            populateMyFavouritesRecyclerView(bundleID, position, FAVOURITED);
                            break;
                    }
                } else {
                    Log.e("Failed", "failed" + e.getLocalizedMessage());
                }
            }
        });
    }

    private void addFitnessToFavourites(String objectId, String exerciseKey, final String bundleID, final int position, final String selectionType) {
        JSONArray exerciseArray = ParseUser.getCurrentUser().getJSONArray(exerciseKey);
        ArrayList<String> exerciseObjectIds = new ArrayList<>();
        if (exerciseArray != null) {
            if (exerciseArray.length() > 0) {
                for (int i = 0; i < exerciseArray.length(); i++) {
                    try {
                        exerciseObjectIds.add(exerciseArray.getString(i));
                    } catch (JSONException e) {
                        Log.e("failed", "failed" + e.getLocalizedMessage());
                    }
                }
                exerciseObjectIds.add(objectId);
            } else {
                exerciseObjectIds.add(objectId);
            }
        } else {
            exerciseObjectIds.add(objectId);
        }
        ParseUser.getCurrentUser().put(exerciseKey, exerciseObjectIds);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    switch (selectionType) {
                        case BROWSE_ALL:
                            populateBrowseAllRecyclerView(bundleID, position, BROWSE_ALL);
                            break;
                        case FAVOURITED:
                            populateMyFavouritesRecyclerView(bundleID, position, FAVOURITED);
                            break;
                    }
                } else {
                    Log.e("failed", "failed" + e.getLocalizedMessage());
                }
            }
        });
    }

    private void doTransaction(String fitnessKey, String fitnessName) {
        SingleFitnessFragment frag = new SingleFitnessFragment();
        FragmentTransaction trans;
        Bundle bundle = new Bundle();
        trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out, R.anim.slide_in_left, R.anim.slide_out);
        trans.addToBackStack(SINGLE_FITNESS_TAG);
        bundle.putString(SINGLE_FITNESS_TAG, fitnessKey);
        bundle.putString(FITNESS_NAME_TAG, fitnessName);
        frag.setArguments(bundle);
        trans.replace(R.id.dashboard_fragment_container, frag);
        trans.commit();
    }

    private ParseQuery<ParseObject> getCorrespondingQuery(String bundleID, String selectionType) {
        ParseQuery<ParseObject> query = null;
        switch (bundleID) {
            case EXERCISES_KEY:
                query = ParseQuery.getQuery(EXERCISES_CLASS_NAME);
                // query.whereEqualTo("isFrench", SharedPreferencesManager.getString(getContext(), "locale").equals("fr"));
                query.orderByAscending(EXERCISE_NAME_KEY);
                break;
            case WORKOUTS_KEY:
                query = ParseQuery.getQuery(PRESET_WORKOUT_CLASS_NAME);
                // query.whereEqualTo("isFrench", SharedPreferencesManager.getString(getContext(), "locale").equals("fr"));
                query.orderByAscending(WORKOUT_NAME_KEY);
                break;
        }
        return query;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "isCalled");
        initViews(view);
    }

    private boolean isChanged = false;
    private TextWatcher customTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (bundleID.equals(WORKOUTS_KEY)) {
                isChanged = true;
                filterWorkouts(s.toString().toLowerCase());
            } else {
                isChanged = true;
                filterExercises(s.toString().toLowerCase());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void filterWorkouts(String s) {
        ArrayList<WorkoutsModel> temp = new ArrayList<>();
        for(WorkoutsModel d : workoutsList){
            //or use .contains(text)
            if(d.getName().toLowerCase().startsWith(s)){
                temp.add(d);
            }
        }
        temp = ((DashboardActivity) getActivity()).removeWorkoutDuplicates(temp);

        if (s.length() == 0) {
            temp = workoutsList;
            noDataTV.setVisibility(GONE);
        }

        if (temp.size() == 0 && isChanged) {
            noDataTV.setVisibility(View.VISIBLE);
            noDataTV.setText(getActivity().getResources().getString(R.string.nosearchresults));
        } else {
            noDataTV.setVisibility(View.GONE);
            workoutAdapter.updateList(temp);
        }
    }

    void filterExercises(String text){
        ArrayList<ExercisesModel> temp = new ArrayList<>();
        for(ExercisesModel d : exercisesList){
            //or use .contains(text)
            if(d.getName().toLowerCase().startsWith(text)){
                temp.add(d);
            }
        }
        temp = ((DashboardActivity) getActivity()).removeExerciseDuplicates(temp);
        if (text.length() == 0) {
            temp = exercisesList;
            noDataTV.setVisibility(View.GONE);
        }

        if (temp.size() == 0 && isChanged) {
            noDataTV.setVisibility(View.VISIBLE);
            noDataTV.setText(getActivity().getResources().getString(R.string.nosearchresults));
        } else {
            noDataTV.setVisibility(View.GONE);
            exercisesAdapter.updateList(temp);
        }
    }

    public List<String> getFavouriteExercises(String exerciseType, String className, String nameKey) {
        ArrayList<String> favouriteExercises = new ArrayList<>();
        JSONArray jsonExercises = ParseUser.getCurrentUser().getJSONArray(exerciseType);
        if (jsonExercises != null) {
            for (int i = 0; i < jsonExercises.length(); i++) {
                try {
                    ParseObject j = ParseObject.createWithoutData(className, jsonExercises.getString(i));
                    favouriteExercises.add(j.fetchIfNeeded().getString(nameKey));
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return favouriteExercises;
    }
}
