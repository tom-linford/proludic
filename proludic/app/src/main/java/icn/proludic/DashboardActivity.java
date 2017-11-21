package icn.proludic;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import icn.proludic.adapters.RecyclerViewHomeParkAdapter;
import icn.proludic.adapters.ViewPagerAdapter;
import icn.proludic.fragments.CommunityFragment;
import icn.proludic.fragments.FitnessFragment;
import icn.proludic.fragments.HomeFragment;
import icn.proludic.fragments.ProfileFragment;
import icn.proludic.fragments.TermsFAQsFragment;
import icn.proludic.misc.Constants;
import icn.proludic.misc.DatabaseHandler;
import icn.proludic.misc.RequestLocationProvider;
import icn.proludic.misc.SashidoHelper;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.misc.Utils;
import icn.proludic.models.AvailabilityModel;
import icn.proludic.models.ExercisesModel;
import icn.proludic.models.Fitness;
import icn.proludic.models.HomeParkModel;
import icn.proludic.models.WorkoutsModel;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_LONG;
import static icn.proludic.misc.Constants.EXERCISES_CLASS_NAME;
import static icn.proludic.misc.Constants.EXERCISE_IDS;
import static icn.proludic.misc.Constants.EXERCISE_NAME_KEY;
import static icn.proludic.misc.Constants.FAQS_KEY;
import static icn.proludic.misc.Constants.HOME_PARK_AND_CURRENT_TYPE;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.IS_AT_PARK_KEY;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.LOCATION_IMAGE_KEY;
import static icn.proludic.misc.Constants.LOCATION_LATITUDE;
import static icn.proludic.misc.Constants.LOCATION_LONGITUDE;
import static icn.proludic.misc.Constants.LOCATION_NAME_KEY;
import static icn.proludic.misc.Constants.LOCATION_STARTING_POINT_KEY;
import static icn.proludic.misc.Constants.LOGOUT_ALERT_ID;
import static icn.proludic.misc.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATION;
import static icn.proludic.misc.Constants.NORMAL_PARK_TYPE;
import static icn.proludic.misc.Constants.NO_PARK_TYPE;
import static icn.proludic.misc.Constants.PRESET_WORKOUT_CLASS_NAME;
import static icn.proludic.misc.Constants.SINGLE_HOME_PARK_TYPE;
import static icn.proludic.misc.Constants.TERMS_FAQS_FRAGMENT_TAG;
import static icn.proludic.misc.Constants.TERMS_KEY;
import static icn.proludic.misc.Constants.TWENTY_FIVE_METERS;
import static icn.proludic.misc.Constants.TWENTY_FIVE_MILES;
import static icn.proludic.misc.Constants.TWO_SECONDS;
import static icn.proludic.misc.Constants.WORKOUT_IDS;
import static icn.proludic.misc.Constants.WORKOUT_NAME_KEY;

public class DashboardActivity extends GalleryIntentActivity {

    private ViewPager viewPager;
    private Context context = this;
    private Stack<Integer> stack = new Stack<>();
    public Utils utils;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout atAParkDialogContainer;
    private String parkName;
    private List<String> exerciseNames, workoutNames, currentParkExercises;
    private String parkType = "empty";
    private final String NOT_SELECTED = "NotSelected";
    private RecyclerView selectHomeParkRecycler;
    private TextView selectHomeParkTitle;
    private List<String> currentParkWorkouts;
    private boolean isNetworkEnabled, isGPSEnabled;
    private LocationManager locationManager;
    private List<Fitness> mostUsedList;
    public DatabaseHandler db;
    private boolean IS_DIALOG_SHOWING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        utils = new Utils(context);
        SashidoHelper.subscribeUserID(ParseUser.getCurrentUser().getObjectId());
        initViews();
        initLocation();
        initSQLDatabase();
    }

    private List<Fitness> initialFitnessList = new ArrayList<>();
    private void initSQLDatabase() {
        doQuery(EXERCISES_CLASS_NAME, EXERCISE_NAME_KEY);
    }

    private void doQuery(final String className, final String nameKey) {
        db = new DatabaseHandler(context);
        utils.setSQLDatabase(db);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
        query.setLimit(500);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.e("fitness", "size: " + Integer.toString(objects.size()));
                    for (ParseObject j : objects) {
                        int isExercise;
                        if (className.equals(EXERCISES_CLASS_NAME)) {
                            isExercise = 1;
                        } else {
                            isExercise = 0;
                        }
                        initialFitnessList.add(new Fitness(0, j.getString(nameKey), 0, isExercise));
                    }

                    if (className.equals(EXERCISES_CLASS_NAME)) {
                        doQuery(PRESET_WORKOUT_CLASS_NAME, WORKOUT_NAME_KEY);
                    }

                    if (className.equals(PRESET_WORKOUT_CLASS_NAME)) {
                        // was previously if db.getFitnessCount() == 0
                        db.deleteCurrentRecords();
                        db.insertInitialData(initialFitnessList);
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void initViews() {
        initViewPager();
        setCustomToolbar();
        setupTabLayout();
        atAParkDialogContainer = (LinearLayout) findViewById(R.id.at_a_park_dialog_container);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.dashboard_coordinatorLayout);
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.pTabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.home_background);
        tabLayout.getTabAt(1).setIcon(R.drawable.exercises_background);
        tabLayout.getTabAt(2).setIcon(R.drawable.profile_background);
        tabLayout.getTabAt(3).setIcon(R.drawable.community_background);
        // tabLayout.getTabAt(4).setIcon(R.drawable.offers_background);
        tabLayout.addOnTabSelectedListener(customTabSelecterListener);
    }

    public void setCustomToolbar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setElevation(0);
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(R.layout.dashboard_toolbar);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
    }

    public void initLocation() {
        setAtPark(false);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = checkGPSEnabled();
        isNetworkEnabled = checkNetworkEnabled();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestLocationPermissions();
        } else {
            getUserLocation();
        }
    }

    private boolean checkGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean checkNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private TabLayout.OnTabSelectedListener customTabSelecterListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int tabPosition = tab.getPosition();
            viewPager.setCurrentItem(tab.getPosition());
            if (tabPosition == 1) {
                if (selectHomeParkRecycler != null) {
                    if (selectHomeParkRecycler.getVisibility() == View.VISIBLE) {
                        selectHomeParkRecycler.setVisibility(View.GONE);
                        selectHomeParkTitle.setVisibility(View.GONE);
                    }
                }
            }

            if (stack.empty()) {
                stack.push(0);
            }

            if (stack.contains(tabPosition)) {
                stack.remove(stack.indexOf(tabPosition));
                stack.push(tabPosition);
            } else {
                stack.push(tabPosition);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(HomeFragment.newInstance(0));
        adapter.addFrag(FitnessFragment.newInstance(1));
        adapter.addFrag(ProfileFragment.newInstance(2));
        adapter.addFrag(CommunityFragment.newInstance(3));
//        adapter.addFrag(OffersFragment.newInstance(4));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TermsFAQsFragment frag = new TermsFAQsFragment();
        FragmentTransaction trans;
        Bundle bundle = new Bundle();
        trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out, R.anim.slide_in_left, R.anim.slide_out);
        switch (item.getItemId()) {
            case R.id.logout:
                showLogoutDialog();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, ActivitySettings.class);
                intent.putExtra(LOCATION_LATITUDE, utils.getLatitude());
                intent.putExtra(LOCATION_LONGITUDE, utils.getLongitude());
                startActivity(intent);
                break;
            case R.id.tncs:
                trans.addToBackStack(TERMS_FAQS_FRAGMENT_TAG);
                bundle.putString(TERMS_FAQS_FRAGMENT_TAG, TERMS_KEY); frag.setArguments(bundle);
                trans.replace(R.id.dashboard_fragment_container, frag);
                trans.commit();
                break;
            case R.id.faqs:
                trans.addToBackStack(TERMS_FAQS_FRAGMENT_TAG);
                bundle.putString(TERMS_FAQS_FRAGMENT_TAG, FAQS_KEY);
                frag.setArguments(bundle);
                trans.replace(R.id.dashboard_fragment_container, frag);
                trans.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        utils.showStandardDialog(context, context.getString(R.string.logout), context.getString(R.string.logout_alert_dialog_message), context.getString(R.string.logout),
                context.getString(R.string.cancel), true, true, LOGOUT_ALERT_ID, DashboardActivity.this);
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DashboardActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DashboardActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(DashboardActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            getUserLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if the user has not confirmed that they are at a park
        if (!SharedPreferencesManager.getBoolean(DashboardActivity.this, IS_AT_PARK_KEY)) {
            initLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (grantResults[0] == PERMISSION_GRANTED) {
                        getUserLocation();
                        utils.showSnackBar(context, mCoordinatorLayout, getString(R.string.permission_granted), Snackbar.LENGTH_LONG);
                    } else {
                        ActivityCompat.requestPermissions(DashboardActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                    }
                }
                break;
        }
    }

    public void getUserLocation() {
        RequestLocationProvider.requestSingleUpdate(context,
                new RequestLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(RequestLocationProvider.GPSCoordinates gps) {
                        Log.e("Location", "my location is: " + gps.location.getLatitude() + " " +  gps.location.getLongitude());
                        utils.setLongitude(gps.location.getLongitude());
                        utils.setLatitude(gps.location.getLatitude());
                        checkHomeParkExists();
                    }
                }, DashboardActivity.this, locationManager, isNetworkEnabled, isGPSEnabled);
    }

    private void checkIFWithin25MetersOfAPark(final ParseObject homePark) {
        final boolean[] isWithin25Meters = new boolean[1];
        ParseGeoPoint userLocation = new ParseGeoPoint(utils.getLatitude(), utils.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
        // changed this
        query.whereWithinKilometers(LOCATION_STARTING_POINT_KEY, userLocation, TWENTY_FIVE_METERS * 3);
        query.setLimit(999);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    isWithin25Meters[0] = objects.size() > 0;
                    Log.e("isWithin25Meters", String.valueOf(isWithin25Meters[0]));
                    // if within 25 meters of a park
                    if (isWithin25Meters[0]) {
                        String parkName = objects.get(0).getString(LOCATION_NAME_KEY);
                        // if the closest park isn't the user's home park, or if the user hasn't selected a home park, or the closest park is the user's home park
                        if (!objects.get(0).getObjectId().equals(ParseUser.getCurrentUser().getString(HOME_PARK_KEY)) || ParseUser.getCurrentUser().getString(HOME_PARK_KEY).equals(NOT_SELECTED) || objects.get(0).getObjectId().equals(ParseUser.getCurrentUser().getString(HOME_PARK_KEY))) {
                            // ask if the user is at their nearest park
                            showAtAParkDialog(parkName);
                        }
                        try {
                        if (homePark != null) {
                            Log.e("homePark", "isNotNull");
                            if (parkName.equals(homePark.fetchIfNeeded().getString(LOCATION_NAME_KEY))) {
                                Log.e("HomeParkOnly", HOME_PARK_KEY);
                                setParkType(SINGLE_HOME_PARK_TYPE);
                                // if home park exists and matches current park, just get home park exercises/workouts
                                getAvailableExerciseNames(homePark.fetchIfNeeded().getJSONArray(EXERCISE_IDS), SINGLE_HOME_PARK_TYPE);
                                getAvailableWorkoutNames(homePark.fetchIfNeeded().getJSONArray(WORKOUT_IDS), SINGLE_HOME_PARK_TYPE);
                            } else {
                                Log.e("HomeParkCurrentPark", HOME_PARK_AND_CURRENT_TYPE);
                                setParkType(HOME_PARK_AND_CURRENT_TYPE);
                                // if home park exists but doesn't match current park, they're not at home, so get both home park and current park exercises/workouts
                                getAvailableExerciseNames(objects.get(0).getJSONArray(EXERCISE_IDS), HOME_PARK_AND_CURRENT_TYPE);
                                getAvailableWorkoutNames(objects.get(0).getJSONArray(WORKOUT_IDS), HOME_PARK_AND_CURRENT_TYPE);
                            }
                        } else {
                            Log.e("NormalParkType", NORMAL_PARK_TYPE);
                            setParkType(NORMAL_PARK_TYPE);
                            // if no home park exists, just get current park information for exercises and workouts
                            getAvailableExerciseNames(objects.get(0).getJSONArray(EXERCISE_IDS), NORMAL_PARK_TYPE);
                            getAvailableWorkoutNames(objects.get(0).getJSONArray(WORKOUT_IDS), NORMAL_PARK_TYPE);
                        }
                        } catch (ParseException e1) {
                            Log.e("failed try", "failed " + e1.toString());
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Log.e("NoParkType", NO_PARK_TYPE);
                        setParkType(NO_PARK_TYPE);
                    }
                } else {
                    Log.e("failed find", "failed" + e.getMessage());
                }
            }
        });
    }

//    private void getAllParksWithinA25MileRadius() {
//        final boolean[] isWithin25Miles = new boolean[1];
//        ParseGeoPoint userLocation = generateGeoPointLocation();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
//        query.whereWithinKilometers(LOCATION_STARTING_POINT_KEY, userLocation, TWENTY_FIVE_MILES);
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                isWithin25Miles[0] = objects.size() > 0;
//                if (isWithin25Miles[0]) {
//                    Log.e("isWithin25Miles", "Of a Park");
//                } else {
//                    getClosestParkOtherwise();
//                }
//            }
//        });
//    }

    private AvailabilityModel model;
    private void  getAvailableExerciseNames(JSONArray jsonExercises, String parkType) throws JSONException, ParseException {
        List<String> exercises;
        setParkType(parkType);
        switch (parkType) {
            case SINGLE_HOME_PARK_TYPE:
                //get just home park exercises
                exercises = getHomeParkExerciseNames();
                setAvailableExercises(exercises);
                break;
            case HOME_PARK_AND_CURRENT_TYPE:
                //get both home park and current park exercises
                exercises = getHomeParkAndCurrentParkExercises(jsonExercises, true);
                exercises = removeStringDuplicates(exercises);
                setAvailableExercises(exercises);
                break;
            case NORMAL_PARK_TYPE:
                //get just current park exercises
                exercises = getHomeParkAndCurrentParkExercises(jsonExercises, false);
                setAvailableExercises(exercises);
                break;
            case NO_PARK_TYPE:
                //get all exercises within parks for 25 miles
//                exercises = getAllExercisesWithin25Miles(jsonExercises);
//                setAvailableExercises(exercises);
                break;
        }
    }

    private List<String> getAllExercisesWithin25Miles(JSONArray jsonExercises) {
        return null;
    }


    public List<String> removeStringDuplicates(List<String> duplicatedList) {
        Set<String> removeDuplicates = new HashSet<>();
        removeDuplicates.addAll(duplicatedList);
        duplicatedList.clear();
        duplicatedList.addAll(removeDuplicates);
        return duplicatedList;
    }

    public ArrayList<ExercisesModel> removeExerciseDuplicates(ArrayList<ExercisesModel> duplicatedList) {
        Set<ExercisesModel> removeDuplicates = new HashSet<>();
        removeDuplicates.addAll(duplicatedList);
        duplicatedList.clear();
        duplicatedList.addAll(removeDuplicates);
        return duplicatedList;
    }


    private List<String> getCurrentParkExercises(JSONArray jsonExercises) throws JSONException, ParseException {
        List<String> currentParkSingleExercises = new ArrayList<>();
        List<ParseObject> currentParkAvailableSingleExercises = new ArrayList<>();
        List<String> currentParkAvailableSingleExercisesNames = new ArrayList<>();

        if (jsonExercises != null) {
            for (int i = 0; i < jsonExercises.length(); i++){
                currentParkSingleExercises.add(jsonExercises.get(i).toString());
            }
        }
        for (String objectId : currentParkSingleExercises) {
            currentParkAvailableSingleExercises.add(ParseObject.createWithoutData(EXERCISES_CLASS_NAME, objectId));
        }
        ParseObject.fetchAll(currentParkAvailableSingleExercises);
        for (ParseObject j : currentParkAvailableSingleExercises) {
            currentParkAvailableSingleExercisesNames.add(j.getString(EXERCISE_NAME_KEY));
            Log.e("e-names", j.getString(EXERCISE_NAME_KEY));
        }
        return currentParkAvailableSingleExercisesNames;
    }

    private List<String> getHomeParkAndCurrentParkExercises(JSONArray jsonExercises, boolean b) throws JSONException, ParseException {
        List<String> currentParkExercises = new ArrayList<>();
        List<ParseObject> currentParkAvailableExercises = new ArrayList<>();
        List<String> currentParkAvailableExercisesNames = new ArrayList<>();
        if (jsonExercises != null) {
            for (int i = 0; i < jsonExercises.length(); i++){
                currentParkExercises.add(jsonExercises.get(i).toString());
            }
        }
        for (String objectId : currentParkExercises) {
            currentParkAvailableExercises.add(ParseObject.createWithoutData(EXERCISES_CLASS_NAME, objectId));
        }
        ParseObject.fetchAll(currentParkAvailableExercises);
        for (ParseObject j : currentParkAvailableExercises) {
            currentParkAvailableExercisesNames.add(j.getString(EXERCISE_NAME_KEY));
            Log.e("ch-names", j.getString(EXERCISE_NAME_KEY));
        }
        if (b) {
            setCurrentParkExercises(currentParkAvailableExercisesNames);
            currentParkAvailableExercisesNames.addAll(getHomeParkExerciseNames());
        }
        return currentParkAvailableExercisesNames;
    }

    private void getAvailableWorkoutNames(JSONArray jsonWorkouts, String parkType) throws ParseException, JSONException {
        List<String> workouts;
        setParkType(parkType);
        switch (parkType) {
            case SINGLE_HOME_PARK_TYPE:
                workouts = getHomeParkWorkoutNames();
                setAvailableWorkouts(workouts);
                break;
            case HOME_PARK_AND_CURRENT_TYPE:
                workouts = getHomeParkAndCurrentParkWorkouts(jsonWorkouts, true);
                workouts = removeStringDuplicates(workouts);
                setAvailableWorkouts(workouts);
                break;
            case NORMAL_PARK_TYPE:
                workouts = getHomeParkAndCurrentParkWorkouts(jsonWorkouts, false);
                setAvailableWorkouts(workouts);
                break;
            case NO_PARK_TYPE:
                break;
        }
    }

    private List<String> getHomeParkAndCurrentParkWorkouts(JSONArray jsonWorkouts, boolean b) throws JSONException, ParseException {
        List<String> currentParkWorkouts = new ArrayList<>();
        List<ParseObject> currentParkAvailableWorkouts = new ArrayList<>();
        List<String> currentParkAvailableWorkoutNames = new ArrayList<>();
        if (jsonWorkouts != null) {
            for (int i = 0; i < jsonWorkouts.length(); i++){
                currentParkWorkouts.add(jsonWorkouts.get(i).toString());
            }
        }
        for (String objectId : currentParkWorkouts) {
            currentParkAvailableWorkouts.add(ParseObject.createWithoutData(PRESET_WORKOUT_CLASS_NAME, objectId));
        }
        ParseObject.fetchAll(currentParkAvailableWorkouts);
        for (ParseObject j : currentParkAvailableWorkouts) {
            currentParkAvailableWorkoutNames.add(j.getString(WORKOUT_NAME_KEY));
            Log.e("ch-names", j.getString(WORKOUT_NAME_KEY));
        }
        if (b) {
            setCurrentParkWorkouts(currentParkAvailableWorkoutNames);
            currentParkAvailableWorkoutNames.addAll(getHomeParkWorkoutNames());
        }
        return currentParkAvailableWorkoutNames;
    }

    private void showAtAParkDialog(String location) {
        setParkName(location);
        TextView yes = (TextView) findViewById(R.id.at_a_park_dialog_yes);
        TextView no = (TextView) findViewById(R.id.at_a_park_dialog_no);
        TextView locationDescription = (TextView) findViewById(R.id.at_a_park_dialog_description);
        locationDescription.setText(getString(R.string.are_you_currently_at) + " " + getParkName() + "?");
        yes.setOnTouchListener(customTouchListener);
        no.setOnTouchListener(customTouchListener);
        atAParkDialogContainer.setVisibility(View.VISIBLE);
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent("Is at Park");
        Map<String, String> articleParams = new HashMap<String, String>();
        //param keys and values have to be of String type
        articleParams.put("Is at Park", ParseUser.getCurrentUser().getEmail() + " is at " + getParkName() + ".");
        //up to 10 params can be logged with each event
        FlurryAgent.logEvent("Is at Park", articleParams);
    }

    private View.OnTouchListener customTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.at_a_park_dialog_yes:
                        hideAtAParkDialog();
                        setAtPark(true);
                        break;
                    case R.id.at_a_park_dialog_no:
                        atAParkDialogContainer.setVisibility(View.GONE);
                        atAParkDialogContainer.setVisibility(View.GONE);
                        setAtPark(false);
                        break;
                }
                return true;
            } else {
                return false;
            }
        }
    };

    private void setAtPark(boolean isAtPark) {
        SharedPreferencesManager.setBoolean(context, IS_AT_PARK_KEY, isAtPark);
    }

    private void hideAtAParkDialog() {
        atAParkDialogContainer.setVisibility(View.GONE);
        utils.makeText(getString(R.string.now_signed_into) + " " + getParkName() + getString(R.string.have_fun), Constants.LENGTH_LONG);
    }

    private long mBackPressed;
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager() != null) {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                if (mBackPressed + TWO_SECONDS > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    utils.makeText(getString(R.string.back_pressed_app), LENGTH_LONG);
                }
                mBackPressed = System.currentTimeMillis();
            }
        }
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkName() {
        return parkName;
    }

    public void setAvailableExercises(List<String> availableExercises) {
        this.exerciseNames = availableExercises;
    }

    public List<String> getAvailableExercises() {
        return exerciseNames;
    }

    public void setAvailableWorkouts(List<String> availableWorkouts) {
        this.workoutNames = availableWorkouts;
    }

    public List<String> getAvailableWorkouts() {
        return workoutNames;
    }

    public ParseGeoPoint generateGeoPointLocation() {
        return new ParseGeoPoint(utils.getLatitude(), utils.getLongitude());
    }

    public void checkHomeParkExists() {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        String homeParkId = objects.get(0).getString(HOME_PARK_KEY);
                        ParseObject homePark = ParseObject.createWithoutData(LOCATIONS_CLASS_KEY, homeParkId);
                        if (!homeParkId.equals(NOT_SELECTED)) {
                            Log.e("homeParkId", homeParkId);
                            try {
                                setHomeParkExerciseNames(homePark.fetchIfNeeded().getJSONArray(EXERCISE_IDS));
                                setHomeParkWorkoutNames(homePark.fetchIfNeeded().getJSONArray(WORKOUT_IDS));
                                checkIFWithin25MetersOfAPark(homePark);
                            } catch (ParseException | JSONException e1) {
                                Log.e("failed", "failed " + e1.toString());
                            }
                        } else {
                            Log.e("noHomeParkSelected", "noHomeParkSelected");
                            showSelectHomeParkDialog();
                            checkIFWithin25MetersOfAPark(null);
                        }
                    } else {
                        Log.e("failed", "No Users Found");
                    }
                } else {
                    Log.e("failed", "failed " + e.getLocalizedMessage());
                }
            }
        });
    }

    private Dialog selectHomeParkDialog;

    private void showSelectHomeParkDialog() {
        if (IS_DIALOG_SHOWING) {
            return;
        }

        selectHomeParkDialog = new Dialog(context);
        selectHomeParkDialog.setContentView(R.layout.dialog_select_home_park);
        selectHomeParkDialog.setCancelable(false);
        selectHomeParkDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        selectHomeParkRecycler = selectHomeParkDialog.findViewById(R.id.select_home_park_recycler_view);
        selectHomeParkRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        selectHomeParkRecycler.setLayoutManager(mLayoutManager);
        selectHomeParkTitle = (TextView) findViewById(R.id.select_home_park_title);
        selectHomeParkRecycler.setVisibility(View.VISIBLE);
        selectHomeParkTitle.setVisibility(View.VISIBLE);
        populateRecyclerView();

        selectHomeParkDialog.show();
        IS_DIALOG_SHOWING = true;
    }

    /*private void makeHomeParkSelectionVisible() {
        selectHomeParkRecycler = (RecyclerView) findViewById(R.id.select_home_park_recycler_view);
        selectHomeParkRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        selectHomeParkRecycler.setLayoutManager(mLayoutManager);
        selectHomeParkTitle = (TextView) findViewById(R.id.select_home_park_title);
        selectHomeParkRecycler.setVisibility(View.VISIBLE);
        selectHomeParkTitle.setVisibility(View.VISIBLE);
        populateRecyclerView();
    }*/

    private void populateRecyclerView() {
        final ArrayList<HomeParkModel> homeParksList = new ArrayList<>();
        ParseGeoPoint userLocation = new ParseGeoPoint(utils.getLatitude(), utils.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
        query.whereWithinKilometers(LOCATION_STARTING_POINT_KEY, userLocation, TWENTY_FIVE_MILES);
        query.setLimit(999);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject j : objects) {
                            Log.e("objects", j.getObjectId());
                            homeParksList.add(new HomeParkModel(j.getObjectId(), j.getString(LOCATION_NAME_KEY), j.getParseFile(LOCATION_IMAGE_KEY).getUrl()));
                        }
                        RecyclerViewHomeParkAdapter adapter = new RecyclerViewHomeParkAdapter(context, homeParksList);
                        selectHomeParkRecycler.setAdapter(adapter);
                        adapter.setOnItemClickListener(new RecyclerViewHomeParkAdapter.onHomeParkItemClickListener() {
                            @Override
                            public void onItemClickListener(View view, int position, final String objectId, String name) {
                                utils.setHomeObjectId(objectId);
                                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.customAlertDialog).create();
                                alertDialog.setTitle(context.getResources().getString(R.string.selectHomePark));
                                alertDialog.setMessage(getString(R.string.sure_you_would_like_to_make) + " " + name + " " + getString(R.string.your_home_park));
                                alertDialog.setButton(BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ParseUser.getCurrentUser().put(HOME_PARK_KEY, objectId);
                                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    selectHomeParkRecycler.setVisibility(View.GONE);
                                                    selectHomeParkTitle.setVisibility(View.GONE);
                                                    selectHomeParkDialog.dismiss();
                                                    IS_DIALOG_SHOWING = false;
                                                    // initViews();
//                                                  checkHomeParkExists();
                                                }
                                            }
                                        });
                                    }
                                });
                                alertDialog.setButton(BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.show();
                            }
                        });
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("noParksWithin25Miles", "true");
                    }
                } else {
                    Log.e("failed", "failed " + e.getMessage());
                }
            }
        });
    }

    private List<String> availableHomeWorkoutNames;
    private void setHomeParkWorkoutNames(JSONArray jsonWorkouts) throws ParseException, JSONException {
        List<String> workoutIds = new ArrayList<>();
        List<ParseObject> availableWorkouts = new ArrayList<>();
        List<String> availableWorkoutNames = new ArrayList<>();

        if (jsonWorkouts != null) {
            for (int i = 0; i < jsonWorkouts.length(); i++){
                workoutIds.add(jsonWorkouts.get(i).toString());
            }
        }
        for (String objectId : workoutIds) {
            availableWorkouts.add(ParseObject.createWithoutData(PRESET_WORKOUT_CLASS_NAME, objectId));
        }
        ParseObject.fetchAll(availableWorkouts);
        for (ParseObject j : availableWorkouts) {
            availableWorkoutNames.add(j.getString(WORKOUT_NAME_KEY));
            Log.e("w-names", j.getString(WORKOUT_NAME_KEY));
        }
        this.availableHomeWorkoutNames = availableWorkoutNames;
    }

    private List<String> availableHomeExerciseNames;
    private void setHomeParkExerciseNames(JSONArray jsonExercises) throws ParseException, JSONException {
        Log.e("exercises", "park exercises");
        List<String> exerciseIds = new ArrayList<>();
        List<ParseObject> availableExercises = new ArrayList<>();
        List<String> availableExerciseNames = new ArrayList<>();
        if (jsonExercises != null) {
            for (int i = 0; i < jsonExercises.length(); i++){
                exerciseIds.add(jsonExercises.get(i).toString());
            }
        } else {
            Log.e("jsonExercises", "isNull");
        }
        for (String objectId : exerciseIds) {
            availableExercises.add(ParseObject.createWithoutData(EXERCISES_CLASS_NAME, objectId));
        }
        ParseObject.fetchAll(availableExercises);
        for (ParseObject j : availableExercises) {
            availableExerciseNames.add(j.getString(EXERCISE_NAME_KEY));
            Log.e("e-names", j.getString(EXERCISE_NAME_KEY));
        }
        this.availableHomeExerciseNames = availableExerciseNames;
    }

    public List<String> getHomeParkExerciseNames() {
        return availableHomeExerciseNames;
    }

    public List<String> getHomeParkWorkoutNames() {
        return availableHomeWorkoutNames;
    }

    public void setCurrentParkExercises(List<String> currentParkExercises) {
        this.currentParkExercises = currentParkExercises;
    }

    public List<String> getCurrentParkExercises() {
        return currentParkExercises;
    }

    public void setParkType(String type) {
        this.parkType = type;
    }

    public String getParkType() {
        return parkType;
    }

    public void setCurrentParkWorkouts(List<String> currentParkWorkouts) {
        this.currentParkWorkouts = currentParkWorkouts;
    }

    public List<String> getCurrentParkWorkouts() {
        return currentParkWorkouts;
    }

    public ArrayList<WorkoutsModel> removeWorkoutDuplicates(ArrayList<WorkoutsModel> duplicatedList) {
            Set<WorkoutsModel> removeDuplicates = new HashSet<>();
            removeDuplicates.addAll(duplicatedList);
            duplicatedList.clear();
            duplicatedList.addAll(removeDuplicates);
            return duplicatedList;
    }

    public void setMostUsedList(List<Fitness> mostUsedList) {
        this.mostUsedList = mostUsedList;
    }

    public List<Fitness> getMostUsedList() {
        return mostUsedList;
    }
}
